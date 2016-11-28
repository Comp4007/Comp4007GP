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
	private Floor floor;
	
    public Kiosk(String id, Building building, Floor floor) {
    	super(id, building);
    	this.floor = floor;
    	kioskCount++;
    	
    }

    public Floor getFloor() {
        return floor;
    }

//    public void setFloor(Floor floor) {
//        this.floor = floor;
//    }

    public boolean addRequest(String target){
    	// String elevatorID = building.getResult(target, id);

        Elevator assignedTo;
        try {
            assignedTo = this.building.putNewHopRequest(this, target);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        if (assignedTo == null)
            log.info(String.format("cannot assign for target %s", target));
        else
            log.info(String.format("Floor \"%s\" request assigned to elevator %d", target, assignedTo.getElevatorId()));
        //update GUI

        return false;
    }

    protected boolean readKeypad(String destFloor){
        building.getLogger().log(Level.INFO, String.format("read keypad, dest = %s", destFloor));
    	return addRequest(destFloor);//dummy
    }
    
    protected boolean readRFID(int id){
        // TODO: rfid id to floor?
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
