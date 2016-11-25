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

        Elevator assignedTo;
        try {
            assignedTo = this.building.kioskPushNewHopRequest(this, target);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        log.info("Floor" + target + "request assigned to elevator " + assignedTo.getID());
        //update GUI

        return false;
    }

    protected boolean readKeypad(String destFloor){
    	return addRequest(destFloor);//dummy
    }
    
    protected boolean readRFID(int id){
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
