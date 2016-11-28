package MyApp.kiosk;

import MyApp.building.Floor;
import MyApp.elevator.Elevator;
import MyApp.misc.*;

import java.util.HashMap;
import java.util.logging.Level;

import MyApp.building.Building;
import com.sun.media.jfxmedia.logging.Logger;


public class Kiosk extends AppThread {
	public static int kioskCount = 0;
	private KioskPanel kp;
	private Floor floor;
	
    public Kiosk(String id, Building building, Floor floor) {
    	super(id, building);
    	this.floor = floor;
    	kioskCount++;
    	kp = new KioskPanel(building);
    }

    public Floor getFloor() {
        return floor;
    }

<<<<<<< HEAD
    public void setFloor(Floor floor) {
        this.floor = floor;
    }
    
    public String addRequest(String target){
=======
//    public void setFloor(Floor floor) {
//        this.floor = floor;
//    }

    public boolean addRequest(String target){
>>>>>>> origin/master
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

<<<<<<< HEAD
    protected String readKeypad(String destFloor){
    	System.out.println(super.id + "/" + destFloor);
    	return addRequest(destFloor);//dummy	
    }
    
    protected String readRFID(int id){
=======
    protected boolean readKeypad(String destFloor){
        building.getLogger().log(Level.INFO, String.format("read keypad, dest = %s", destFloor));
    	return addRequest(destFloor);//dummy
    }
    
    protected boolean readRFID(int id){
        // TODO: rfid id to floor?
>>>>>>> origin/master
        String destFloor = "";
        building.getLogger().log(Level.INFO, String.format("read keypad, nfc id = %d, dest = %s", id, destFloor));
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
