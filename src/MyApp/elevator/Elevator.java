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
	public static int elevatorCount = 0;
	private double heightOfFloor;
	private	double accelerationParameter;
	private double acceleration;
	private	double minOfMeter;
    private ArrayList<MBox> kioskMBox;
    private double height = 5;
    private double velocity = 0;
    private ElevatorStatus status;
    private double breakDistance;
    private int timeDuration;
    //private String id;
    private SortedSet<Integer> sortedQueue;
    
    public Elevator(String id, Building building) {
    	super(id, building);
    	this.heightOfFloor = Double.parseDouble(building.getProperty("HeightOfFloor"));
    	this.accelerationParameter = Double.parseDouble(building.getProperty("Acceleration"));
    	this.minOfMeter = Double.parseDouble(building.getProperty("MinOfMeter"));
    	this.timeDuration = Integer.parseInt(building.getProperty("TimerTicks"));
    	kioskMBox = new ArrayList<MBox>();
    	//this.id = id;
    	for(int i = 0; i < Kiosk.koiskCount; i++){
    		kioskMBox.add(building.getThread("k" + i).getMBox());
    	}//for communication with kiosk
    	breakDistance = (Math.pow((minOfMeter/60), 2) / accelerationParameter)*0.5;
    	addQueue(1);
    }

    //for the controller to check if an elevator can stop
    public ElevatorStatus getStatus(){
    	return new ElevatorStatus(height,velocity,breakDistance,acceleration);
    }
    
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
} // PlayerA
