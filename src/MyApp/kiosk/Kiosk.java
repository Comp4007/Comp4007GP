package MyApp.kiosk;

import MyApp.misc.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JFrame;

import MyApp.Building;
import MyApp.elevator.Elevator;


public class Kiosk extends AppThread {
	public static int koiskCount = 0;
	private JFrame Panel;
	private int floor;
	
    public Kiosk(String id, Building building) {
    	super(id, building);
    	this.floor = koiskCount;
    	koiskCount++;
    	
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
    
    public HashMap<Integer, String> getQueue(){
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
