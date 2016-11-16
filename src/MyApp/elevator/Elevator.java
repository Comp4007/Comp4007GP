package MyApp.elevator;

import MyApp.misc.*;
import MyApp.timer.Timer;

import java.util.ArrayList;
import java.util.Hashtable;

import MyApp.Building;
import MyApp.kiosk.Kiosk;


public class Elevator extends AppThread {
    public static int elevatorCount = 0;
    private ArrayList<MBox> kioskMBox;
    private double height;
    private double velocity;
    //private String id;

    public Elevator(String id, Building building) {
        super(id, building);
        kioskMBox = new ArrayList<MBox>();
        //this.id = id;
        for (int i = 0; i < Kiosk.koiskCount; i++) {
            kioskMBox.add(building.getThread("k" + i).getMBox());
        }//for communication with kiosk

    }

    //for the controller to check if an elevator can stop
    public ElevatorStatus getStatus() {
        return new ElevatorStatus(height, velocity);
    }

    public void addQueue(int target) {
        //add and sort new target into the queue
    }

    //------------------------------------------------------------
    // run
    public void run() {
        while (true) {
            int timerID = Timer.setTimer(id, 1000);
            Msg msg = mbox.receive();

            if (msg.getSender().equals("Timer")) {
                //simulate the lift itself with height, velocity, destination, etc..
                //including stop, idle...
            } else {
                break;
            }
        }
        System.out.println(id + ": Terminating This Lift!");
        System.exit(0);
    } // run
} // PlayerA
