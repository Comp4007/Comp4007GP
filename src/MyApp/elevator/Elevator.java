package MyApp.elevator;

import MyApp.misc.*;
import MyApp.timer.Timer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;

import MyApp.Building;
import MyApp.kiosk.Kiosk;


public class Elevator extends AppThread {
	/**
	 * This is count number of elevator. Also for building getElevatorQueue() to get no. of elevator
	 */
	public static int elevatorCount = 0;
	/**
	 * Default setting in config file. Assume each floor has 2.5m
	 */
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
	 * Default setting in config file. Assume the elevator move 60 meter per 1 mins
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
     * Sort the HashMap(Request of queue), Also this is elevator mission queue
     */
    private SortedSet<Integer> sortedQueue;
    
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
    	for(int i = 0; i < Kiosk.koiskCount; i++){
    		kioskMBox.add(building.getThread("k" + i).getMBox());
    	}//for communication with kiosk
    	/**
    	 * Based on the default setting of minOfMeter and accelerationParameter to count brakDistance
    	 */
    	breakDistance = (Math.pow((minOfMeter/60), 2) / accelerationParameter)*0.5;
    	addQueue(1);
    }

    //for the controller to check if an elevator can stop
    public ElevatorStatus getStatus(){
    	return new ElevatorStatus(height,velocity,breakDistance,acceleration);
    }
    
    /**
     * When building finish the simulate, the target result will use this method to pass in elvator mission queue
     * @param target
     */
    public void addQueue(int target){
    	queue.put(target, id);
    	sortedQueue = new TreeSet<Integer>(queue.keySet());
    }
    
    private void simulate(){
    	int target = sortedQueue.first();
    	
    	if((target-1) * heightOfFloor < height){
    		if(velocity > -1 || velocity != 0){
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
	        	}else if(velocity < -1){
	        		velocity = -1;
	        		acceleration = 0;
	        	}
        	
		}
    	
    	if((target-1) * heightOfFloor > height){
    		if(velocity < 1 || velocity != 0){
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
	        	}else if(velocity > 1){
	        		velocity = 1;
	        		acceleration = 0;
	        	}
        
		}
    	
    	
    	
    	//log.info(velocity+"");
    	
    	height += velocity*timeDuration/1000 + 0.5*(acceleration)*Math.pow(timeDuration/1000,2);
    	
    	if(velocity == 0){
    			height = (target-1) * heightOfFloor;
    			queue.remove(sortedQueue.first());
    			sortedQueue = new TreeSet<Integer>(queue.keySet());
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

	// TODO: JavaDoc for buildingPushNewDestination(String)
	protected final synchronized boolean buildingPushNewDestination(String floorName) {
		// TODO: what do you do when I(Building) gives you new destination?
		// TODO: return boolean if you may stop there. otherwise I'll assign another lift then -- Charles
		return true;
	}
} // PlayerA
