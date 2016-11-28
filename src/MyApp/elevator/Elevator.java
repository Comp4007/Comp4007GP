package MyApp.elevator;

import MyApp.building.Floor;
import MyApp.misc.*;
import MyApp.timer.Timer;
import java.util.ArrayList;
import java.util.Collections;

import MyApp.building.Building;
import MyApp.kiosk.Kiosk;

public class Elevator extends AppThread implements Comparable<Elevator> {
	/**
	 * This is count number of elevator. Also for building getElevatorQueueString() to get no. of elevator
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
    private double height = 4;
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
    /**
     * Use array list become mission queue
     * One is for elevator move up , one is for elevator move down 
     * They will clean one direction of mission first, then use other one
     * This process will repeat
     */
    private ArrayList<Integer> missionQueueUpward = new ArrayList<Integer>();
    private ArrayList<Integer> missionQueueDownward = new ArrayList<Integer>();
    /**
     * Get the floor list form building for the target number
     */
    private String[] floorList;
    /**
     * True is upward, False is downward When one direction of queue is finish, it will filp to use counter direction queue
     */
    private boolean direction = true;
    
    public Elevator(String id, Building building) {
    	super(id, building);
    	//Get property from building object
    	this.heightOfFloor = Double.parseDouble(building.getProperty("HeightOfFloor"));
    	this.accelerationParameter = Double.parseDouble(building.getProperty("Acceleration"));
    	this.minOfMeter = Double.parseDouble(building.getProperty("MinOfMeter"));
    	this.timeDuration = Integer.parseInt(building.getProperty("TimerTicks"));
    	//Get all kiosk MBox for communication with kiosk  	
    	kioskMBox = new ArrayList<MBox>();
    	for(int i = 0; i < Kiosk.kioskCount; i++){
    		kioskMBox.add(building.getThread("k" + i).getMBox());
    	}
    	//Based on the default setting of minOfMeter and accelerationParameter to count brakDistance
    	// v^2 - u^2 = 2as, v = initial m/s, u = target m/s, a = acceleration m/s/s, s = displacement m
    	breakDistance = (Math.pow((minOfMeter/60), 2) / accelerationParameter)*0.5;
    	elevatorId = elevatorCount++;
    	floorList = building.getFloorNames();
    }

    /**
     * It is for every class get all the status of the elevator 
     * @return
     */
    public final ElevatorStatus getStatus(){
    	return new ElevatorStatus(this, height,velocity,breakDistance,acceleration, missionQueueUpward.size()+missionQueueDownward.size());
    }

    public int getElevatorId() {
        return elevatorId;
    }
    
    public int getFloorIndex(String floorName){
    	int count = 0;
    	for(int i=0; i<floorList.length; i++){
    		if(floorList[i].equals(floorName)){
    			count = i;
    			break;
    		}
    	}
    	return count+1;
    }

    /**
     * When building finish the simulate, the target result will use this method to pass in elevator mission queue
     * When elevator accept the request from building, it will rearrange the mission queue
     * @param target
     */
    public void addQueue(int target, ArrayList<Integer> missionQueue,String order){
    	queue.put(target, id);  	
    	//If the target is already in mission queue, no need to add.
    	if(!missionQueue.contains(target)){
    		//The rearrange the mission queue(Split two mission queue one is up one is down)
    		if(order.equals("ASC")){
    			missionQueue.add(target);
    			Collections.sort(missionQueue);
    		}else if(order.equals("DSC")){
    			missionQueue.add(target);
    			Collections.sort(missionQueue, Collections.reverseOrder());
    		}
    	}
    }
    
    private void simulate(ArrayList<Integer> missionQueue) throws InterruptedException{
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
    	height += velocity*timeDuration/1000 + 0.5*(acceleration)*Math.pow(timeDuration/1000,2);	
    	if(velocity == 0){
    		height = (target-1) * heightOfFloor;
    		queue.remove(target);
    		missionQueue.remove(0);
    		//Flip the direction (true is upward and false is downward)
    		if(missionQueue.size()==0){
    			direction = !direction;
    		}
    		//This is the time of open door
    		Thread.sleep(5000);
    	}
    	log.info(String.format("elevator %d: height = %.2f m, %.2f m/s, %.2f m/s/s", this.getElevatorId(), height, velocity, acceleration));
    }

    public void run() {
	while (true) {
	    int timerID = Timer.setTimer(id, timeDuration);
	    Msg msg = mbox.receive();

	    if (msg.getSender().equals("Timer")) {
	    	try{
	    		//At the beginning all elevator should be at the B2 floor
	    		//So elevator should solve the upward request first
	    		if (direction) {
	    			simulate(missionQueueUpward);
	    		}else{
	    			simulate(missionQueueDownward);
	    		}
	    	}catch(Exception e){
	    		
	    	}
		}else{
			break;
		}
	}
	System.out.println(id + ": Terminating This Lift!");
	System.exit(0);
    }

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
    		if(height < floor.getYDisplacement() - breakDistance){
    			availableStop = true;
    			//Add the request to mission queue, but the queue must rearrange (ascending order)
    			addQueue(getFloorIndex(floor.getName()),missionQueueUpward,"ASC");
    		}else if(height-heightOfFloor > floor.getYDisplacement() + breakDistance || floor.getYDisplacement()==0){
        		availableStop = true;
        		//Add the request to mission queue, but the queue must rearrange (descending order)
        		addQueue(getFloorIndex(floor.getName()),missionQueueDownward,"DSC");
    		}else{
    			availableStop = false;
    		}
        return availableStop;
    }

    @Override
    public int compareTo(Elevator o) {
        return this.elevatorId - o.elevatorId;
    }
}
