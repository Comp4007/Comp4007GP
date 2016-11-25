package MyApp.kiosk;

import MyApp.Floor;
import MyApp.elevator.Elevator;
import MyApp.misc.*;

import java.util.HashMap;

import javax.swing.JFrame;

import MyApp.Building;


public class Kiosk extends AppThread {
	public static int kioskCount = 0;
	private Floor floor;
	
    public Kiosk(String id, Building building) {
    	super(id, building);
    	this.floor = (Floor) building.getFloorPositions().values().toArray()[kioskCount];
    	kioskCount++;
    	
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public boolean addRequest(String target){
    	// String elevatorID = building.getResult(target, id);

        boolean duplicated;
        try {
            duplicated = !this.building.kioskPushNewHopRequest(this, target);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

    	if (duplicated) {
            log.info("request handled but duplicated");
            //update GUI
        } else {
            log.info("Floor" + target + "request added to queue");
            //update GUI
        }

        return false;
    }

    private boolean readKeypad(String destFloor){
    	return addRequest(destFloor);//dummy
    }
    
    private boolean readRFID(int id){
        String destFloor = "";
    	return addRequest(destFloor);//dummy
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
