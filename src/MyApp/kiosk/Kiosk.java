package MyApp.kiosk;

import MyApp.building.Floor;
import MyApp.elevator.Elevator;
import MyApp.misc.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.logging.Level;

import MyApp.building.Building;


public class Kiosk extends AppThread implements Comparable<Kiosk> {
    public static int kioskCount = 0;
    private int kioskid;
    private Floor floor;
	private String[] floorList;
    private RFID rfid;
    private String kioskUpdate = "";

    private HashMap<Elevator, LinkedHashSet<Floor>> awaitingDestinations = new HashMap<>();

    public Kiosk(String id, Building building, Floor floor) {
        super(id, building);
        floorList = building.getFloorNames();
        this.floor = floor;
        kioskid = kioskCount++;
        rfid = new RFID();
    }
    
    /**
     * Get floor of koisk
     * @return
     */
    public Floor getFloor() {
        return floor;
    }

    /**
     * Set floor of koisk
     * @return
     */
    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    /**
     * Add Destination floor request
     * @param target
     * @return
     */
    public void addRequest(String target) {
        // String elevatorID = building.getResult(target, id);
        Elevator assignedTo = null;
        try {
            assignedTo = this.building.putNewHopRequest(this, target);
        } catch (IndexOutOfBoundsException e) {
        	kioskUpdate = "Error! please try again!";
        }

        if (assignedTo == null) {
            log.info(String.format("cannot assign for target %s", target));
            kioskUpdate = "Assigne not successful.";
        } else {
            log.info(String.format("Floor \"%s\" request assigned to elevator %d", target, assignedTo.getElevatorId()));
            putNewElevatorDestination(assignedTo, building.getFloorPosition(target));
            kioskUpdate = "Floor " + target + " request assigned to elevator " + assignedTo.getID();
        }
    }

    /**
     * add request to the 
     * @param e Elevator
     * @param dest Floor
     */
    private void putNewElevatorDestination(Elevator e, Floor dest) {
        LinkedHashSet<Floor> floors;

        if ((floors = this.awaitingDestinations.get(e)) == null) {
            LinkedHashSet<Floor> newLinkedHashSet = new LinkedHashSet<>();
            this.awaitingDestinations.put(e, newLinkedHashSet);
            floors = newLinkedHashSet;
        }

        floors.add(dest);
    }
    
    /**
     * Get the keypad input and add request in waiting list
     * @param destFloor
     */
    protected void readKeypad(String destFloor) {
        if(Arrays.asList(floorList).contains(destFloor)){
        	building.getLogger().log(Level.INFO, String.format("read keypad, nfc id = %s, dest = %s", id, destFloor));
        	addRequest(destFloor);//dummy
        }else{
        	kioskUpdate = "Wrong ID, Please try again.";
        }
        addRequest(destFloor);
    }

    /**
     * Get the rfid reader input and add request in waiting list
     * @param destFloor
     */
    protected void readRFID(String id) {
        String destFloor = rfid.getFloorById(id);
      
        if(destFloor != "na"){
        	building.getLogger().log(Level.INFO, String.format("read keypad, nfc id = %s, dest = %s", id, destFloor));
        	addRequest(destFloor);//dummy
        }else{
        	kioskUpdate = "Wrong ID, Please try again.";
        }
    }

    /**
     * 
     */
    protected void elevatorIn() {
        building.getLogger().log(Level.INFO, "Floor " + floor.getName() + "Enter elevator arrived");
        kioskUpdate = "Elevator arrived";
        //TODO search if any elevator is arrived
        finishHopRequest();
        
    }

    /**
     * 
     * @return awaitingDestinations
     */
    public HashMap<Elevator, LinkedHashSet<Floor>> getDestinationQueue() {
        return this.awaitingDestinations;
    }

    /**
     * Request the building centralised controller to fetch all docked <code>Elevators</code>, and putting destination floors from Kiosk into
     */
    private void finishHopRequest() {
        for (Elevator e : building.getDockedElevatorsFromFloor(this.getFloor())) {
            LinkedHashSet<Floor> destFloors = this.awaitingDestinations.remove(e);
            if (destFloors == null) continue;
            destFloors.forEach(e::putNewDestination);
        }
    	
    }

    /**
     * 
     */
    public void run() {
        //create GUI with RFID/keypad input
        Msg msg = mbox.receive();
        System.out.println(id + ": Received msg: " + msg);
        
        //call finish request if elevator tell kiosk the request is finished
    }

    @Override
    public int compareTo(Kiosk o) {
        return this.getID().compareTo(o.getID());
    }

    /**
     * Get kiosk update information for panel to update
     * @return 
     */
	public String getUpdate() {
		return kioskUpdate;
	}
	
	/**
	 * Update kiosk information 
	 * @param text
	 */
	public void setUpdate(String text) {
		kioskUpdate = text;
	}
}
