package MyApp.elevator;

import MyApp.Floor;
import MyApp.misc.*;
import MyApp.timer.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import MyApp.Building;
import MyApp.kiosk.Kiosk;


public class Elevator extends AppThread implements Comparable<Elevator> {
	/**
	 * This is count number of elevator. Also for building getElevatorQueue() to get no. of elevator
	 */
	public static int elevatorCount = 0;

    private int elevatorId;
	/**
	 * Default setting in config file. Assume each floor has 4m
	 */
	@Deprecated
	private double heightOfFloor;
	/**
	 * Default setting in config file. Assume the accelation is 5
	 */
	private	double accelerationParameter;
	/**
	 * Determine the direction of elevator. E.G. -5 or +5
	 */
	private double acceleration;
	/**
	 * Default setting in config file. Assume the elevator move 120 meter per 1 mins
	 * This is reference hitachi elevator spec.
	 */
	private	double minOfMeter;
	/**
	 * This is for elevator talk to kiosk. 
	 * When elevator let the passger in, elevator will send msg(call kiosk finishRequest() => remove the request)
	 */
    private ArrayList<MBox> kioskMBox;
    /**
     * This parameter represent height of elevator 
     * It will update every 30 ms
     */
    private double height = 0;
    /**
     *This parameter represent velocity of elevator 
     * It will update every 30 ms
     */
    private double velocity = 0;
    /**
     * It is an object save all the elevator data (height, breakDistance,...)
     * Other class can get the object and get those data for specific elevator
     */
    private ElevatorStatus status;
    /**
     * This parameter is for building to check elevator which can stop for sudden request
     * If elevator enough distance(height) to stop, it will serve the request otherwise skip it 
     */
    private double breakDistance;
    /**
     * Default setting in config file. Elevator will update itself for 30ms
     */
    private int timeDuration;
    //private String id;
    /**
     * Use array list become mission queue
     */
    private ArrayList<Integer> missionQueue;
    
    public Elevator(String id, Building building) {
    	super(id, building);
    	elevatorCount++;
    	/**
    	 * Get property from building object
    	 */
    	this.heightOfFloor = Double.parseDouble(building.getProperty("HeightOfFloor"));
    	this.accelerationParameter = Double.parseDouble(building.getProperty("Acceleration"));
    	this.minOfMeter = Double.parseDouble(building.getProperty("MinOfMeter"));
    	this.timeDuration = Integer.parseInt(building.getProperty("TimerTicks"));
    	/**
    	 * Get all kiosk MBox 
    	 */   	
    	kioskMBox = new ArrayList<MBox>();
    	//this.id = id;
    	for(int i = 0; i < Kiosk.kioskCount; i++){
    		kioskMBox.add(building.getThread("k" + i).getMBox());
    	}//for communication with kiosk
    	/**
    	 * Based on the default setting of minOfMeter and accelerationParameter to count brakDistance
    	 */
    	// v^2 - u^2 = 2as, v = initial m/s, u = target m/s, a = acceleration m/s/s, s = displacement m
    	breakDistance = (Math.pow((minOfMeter/60), 2) / accelerationParameter)*0.5;
    	elevatorId = elevatorCount++;
    	addQueue(2);
    }

    //for the controller to check if an elevator can stop
    public final ElevatorStatus getStatus(){
    	return new ElevatorStatus(this, height,velocity,breakDistance,acceleration, missionQueue.size());
    }

    public int getElevatorId() {
        return elevatorId;
    }

    /**
     * When building finish the simulate, the target result will use this method to pass in elevator mission queue
     * When elevator accept the request from building, it will rearrange the mission queue
     * @param target
     */
    public void addQueue(int target){
    	queue.put(target, id);
    	//
    }
    
    private void simulate(){
    	int target = missionQueue.get(0);
    	
    	if((target-1) * heightOfFloor < height){
    		if(velocity > -2 || velocity != 0){
    			acceleration = -accelerationParameter;
    		}
    		if(((target-1) * heightOfFloor + breakDistance) >= height){
    			log.info("should break");
        		acceleration = accelerationParameter;
        	}
    		
        		velocity = velocity+acceleration * timeDuration/1000;
        		
        		if(velocity > 0){
        		velocity = 0;
        		acceleration = 0;
	        	}else if(velocity < -2){
	        		velocity = -2;
	        		acceleration = 0;
	        	}
        	
		}
    	
    	if((target-1) * heightOfFloor > height){
    		if(velocity < 2 || velocity != 0){
    			acceleration = accelerationParameter;
    		}
    		if(((target-1) * heightOfFloor - breakDistance) <= height){
    			log.info("should break");
        		acceleration = -accelerationParameter;
        	}
    		
    		
        		velocity = velocity+acceleration * timeDuration/1000;
        		
        		if(velocity < 0){
        		velocity = 0;
        		acceleration = 0;
	        	}else if(velocity > 2){
	        		velocity = 2;
	        		acceleration = 0;
	        	}
        
		}
    	
    	
    	
    	//log.info(velocity+"");
    	
    	height += velocity*timeDuration/1000 + 0.5*(acceleration)*Math.pow(timeDuration/1000,2);
    	
    	if(velocity == 0){
    			height = (target-1) * heightOfFloor;
    			queue.remove(missionQueue.get(0));
    	}
    	
    	log.info(height+", "+velocity+", "+acceleration);
    	
    	return;
    }
    //------------------------------------------------------------
    // run
    public void run() {
	while (true) {
	    int timerID = Timer.setTimer(id, timeDuration);
	    Msg msg = mbox.receive();

	    if (msg.getSender().equals("Timer")) {
	    	try{
	    		simulate();
	    	}catch(Exception e){
	    		
	    	}
		}else{
			break;
		}
	}
	
	
	
	System.out.println(id + ": Terminating This Lift!");
	System.exit(0);
    } // run

	// TODO: JavaDoc for putNewDestination(String)
    public final boolean putNewDestination(String floorName) {
        Floor floor = building.getFloorPosition(floorName);
        return putNewDestination(floor);
	}

    /**
     * Building assign the request to elevator
     * Elevator will simulate the destination whether can stop or not
     * If it can stop, return true. Then manage the mission queue.
     * If it cannot stop, return false.
     * @param floor
     * @return
     */
    public final synchronized boolean putNewDestination(Floor floor) {
    	//Deafult return false 
    	boolean availableStop = false;
    	//Get the floor height plus breaking distance to compare with the height of elevator (Use the top(y position) of elevator as the height)
    	//First check the direction of elevator, if it is moving down(The height of elevator - 4m(height of floor)), y displacement + breaking distance
    	//if it is moving up, y displacement - breaking distance
    	if (velocity > 0) {
    		//Moving up
    		if(height < floor.getYDisplacement() - breakDistance){
    			availableStop = true;
    			//Add the request to mission queue, but the queue must rearrange
    			addQueue((int)floor.getYDisplacement());
    		}else{
    			availableStop = false;
    		}
        } else if (velocity < 0) {
        	//Moving down
        	if(height-heightOfFloor > floor.getYDisplacement() + breakDistance){
        		availableStop = true;
        		//Add the request to mission queue, but the queue must rearrange
        		addQueue((int)floor.getYDisplacement());
    		}else{
    			availableStop = false;
    		}
        	
        }	
        return availableStop;
    }

    @Override
    public int compareTo(Elevator o) {
        return this.elevatorId - o.elevatorId;
    }
} // PlayerA
