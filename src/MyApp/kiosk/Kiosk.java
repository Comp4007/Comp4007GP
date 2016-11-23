package MyApp.kiosk;

import MyApp.misc.*;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;

import MyApp.Building;
import MyApp.elevator.Elevator;


public class Kiosk extends AppThread {
	public static int koiskCount = 0;
	//private ArrayList<MBox> elevatorMBox;
	private JFrame Panel;
	//private String floor;
	
    public Kiosk(String id, Building building) {
    	super(id, building);
    	koiskCount++;
//    	for(int i = 0; i < Elevator.elevatorCount; i++){
//    		ElevatorMBox.add(building.getThread("e" + i).getMBox());
//    	}//for communication with elevator
    	
    	Panel = new JFrame("uiui");
    }

    
    public void addRequest(int target){
    	String elevatorID = building.getResult(target, id);
    	if(elevatorID.equals("")){
    		System.out.print("request handled but duplicated");
    		//update GUI
    	}else{
    		queue.put(target, elevatorID);
    		log.info("Floor" + target + "request added to elevator" + elevatorID);
    		//update GUI
    	}
    }

    private void readKeypad(int floor){
    	addRequest(floor);//dummy
    }
    
    private void readRFID(int id){
    	//somehow simulate
    	addRequest(id);//dummy
    }
    
    public Hashtable<Integer, String> getQueue(){
    	return queue;
    }
    
    private void finishRequest(String elevatorID){
    	//delete all element in queue with this elevatorID
    }
    
    public void run() {
		//create GUI with RFID/keypad input
    	Msg msg = mbox.receive();
	    //System.out.println(id + ": Received msg: " + msg);
    	
    	//call finish request if elevator tell kiosk the request is finished
    }
}
