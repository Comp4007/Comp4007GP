package MyApp.kiosk;

import MyApp.building.Floor;
import MyApp.elevator.Elevator;
import MyApp.misc.*;

import java.util.HashMap;
import java.util.logging.Level;

import MyApp.building.Building;


public class Kiosk extends AppThread {
    public static int kioskCount = 0;
    private int kioskid;
    private KioskPanel kp;
    private Floor floor;
    private RFID rfid;

    public Kiosk(String id, Building building, Floor floor) {
        super(id, building);
        this.floor = floor;
        kioskid = kioskCount++;
        kp = new KioskPanel(building);
        rfid = new RFID();
    }

    public Floor getFloor() {
        return floor;
    }


    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public String addRequest(String target) {
        // String elevatorID = building.getResult(target, id);
        Elevator assignedTo = null;
        try {
            assignedTo = this.building.putNewHopRequest(this, target);
        } catch (IndexOutOfBoundsException e) {
            return "Error! please try again!";
        }

        if (assignedTo == null) {
            log.info(String.format("cannot assign for target %s", target));
            return "Assigned not success.";
        } else {
            log.info(String.format("Floor \"%s\" request assigned to elevator %d", target, assignedTo.getElevatorId()));
            return "Floor" + target + " request assigned to elevator " + assignedTo.getID();
        }
    }

    protected String readKeypad(String destFloor) {
        return addRequest(destFloor);
    }

    protected String readRFID(String id) {
        // TODO: rfid id to floor?
    	System.out.println(id);
        String destFloor = rfid.getFloorById(id);
      
        if(destFloor != "na"){
        	building.getLogger().log(Level.INFO, String.format("read keypad, nfc id = %s, dest = %s", id, destFloor));
        	return addRequest(destFloor);//dummy
        }else{
        	return "Wrong ID, Please try again.";
        }
    }

    protected void elevatorIn() {
        building.getLogger().log(Level.INFO, "Floor " + floor.getName() + "Enter elevator arrived");
        //TODO search if any elevator is arrived
        kp.updateDisplay("texting",kioskid);
        finishRequest("");
    }

    public HashMap<Integer, String> getQueue() {
        return queue;
    }

    //TODO:  finishRequest
    private void finishRequest(String elevatorID) {
        //delete all element in queue with this elevatorID
    	
    }

    public void run() {
        //create GUI with RFID/keypad input
        Msg msg = mbox.receive();
        //System.out.println(id + ": Received msg: " + msg);

        //call finish request if elevator tell kiosk the request is finished
    }
}
