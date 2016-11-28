package MyApp.kiosk;

import MyApp.building.Floor;
import MyApp.elevator.Elevator;
import MyApp.misc.*;

import java.util.HashMap;

import MyApp.building.Building;


public class Kiosk extends AppThread {
	public static int kioskCount = 0;
	private KioskPanel kp;
	private Floor floor;
	
    public Kiosk(String id, Building building) {
    	super(id, building);
    	this.floor = (Floor) building.getFloorPositions().values().toArray()[kioskCount];
    	kioskCount++;
    	kp = new KioskPanel(building);
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }
    
    public String addRequest(String target){
    	// String elevatorID = building.getResult(target, id);

        Elevator assignedTo = null;
        try {
            assignedTo = this.building.putNewHopRequest(this, target);
        } catch (IndexOutOfBoundsException e) {
        	return "Error! please try again!";
        }
        
        if(assignedTo != null){
        	log.info("Floor" + target + "request assigned to elevator " + assignedTo.getID());
        	return "Floor" + target + "request assigned to elevator " + assignedTo.getID();
        }
        return "Assigned not success.";
    }

    protected String readKeypad(String destFloor){
    	System.out.println(super.id + "/" + destFloor);
    	return addRequest(destFloor);//dummy	
    }
    
    protected String readRFID(int id){
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
