package MyApp;

import java.lang.Thread;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import MyApp.elevator.*;
import MyApp.kiosk.*;
import MyApp.misc.*;
import MyApp.panel.AdminPanel;
import MyApp.panel.ControlPanel;
import MyApp.panel.Panel;
import MyApp.timer.*;

/**
 * Simulates all functionality of a centralised controller inside a building. <br/>
 * This may be used as entry point for simulation.
 */
public class Building {
    /**
     * This is config file path
     */
    private final String cfgFName = "etc/MyApp.cfg";
    /**
     * Logging module for verbose, debugging and warning/error messages.
     */
    private Logger log = null;
    /**
     * A hash table storing all created thread-object in this respective building, with its identifier as the key.
     */
    private Hashtable<String, AppThread> appThreads = null;
    /**
     * Accessors for different properties in this building configuration.
     */
    private Properties cfgProps = null;
    /**
     * Indicates the total meters that an Elevator may move vertically for.
     */
    private final int displacementMeters = 0;
    /**
     * A dictionary storing all stoppale hops (floors) and the position, in meters, of displacement where the hop is.
     */
    private final Hashtable<String, Integer> floorPositions = null;

    /**
     * Java.exe entry point for loading up the Building simulation element.
     */
    public static void main(String args[]) {
        Panel window = new AdminPanel();
        window.showInfo();
        Building building = new Building();
        building.startApp();
    } // main

    /**
     * Initialisation of the Building simulation element. <br/>
     * It will also instantiate all lifts, kiosks, control panels and other related stuffs.
     */
    public Building() {
        // read system config from property file
        try {
            cfgProps = new Properties();
            FileInputStream in = new FileInputStream(cfgFName);
            cfgProps.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Failed to open config file (" + cfgFName + ").");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Error reading config file (" + cfgFName + ").");
            System.exit(-1);
        }

        // get and configure logger
        ConsoleHandler conHd = new ConsoleHandler();
        conHd.setFormatter(new LogFormatter());
        log = Logger.getLogger(Building.class.getName());
        log.setUseParentHandlers(false);
        log.addHandler(conHd);
        log.setLevel(Level.INFO);
        appThreads = new Hashtable<String, AppThread>();
    } // AppKickstarter


    /**
     * Start running up the world of the simulation.
     */
    public void startApp() {
        // create threads
        /**
         * This is for elevator use  implement by steven and kers
         */
        Timer timer = new Timer("timer", this);

        /**
         * Create Kiosks  k0 = floor 1 kiosk, k1 = floor 2 kiosk ......
         */
        int kc = new Integer(this.getProperty("Kiosks"));
        for (int i = 0; i < kc; i++) {
            Kiosk kiosk = new Kiosk("k" + i, this);
            new Thread(kiosk).start();
        }

        /**
         * Create elevator  e0 = elevator 1, e1 = elevator 2 ......
         */
        int e = new Integer(this.getProperty("Elevators"));
        for (int i = 0; i < e; i++) {
            Elevator elevator = new Elevator("e" + i, this);
            new Thread(elevator).start();
        }
        // start threads

        /**
         * This is for elevator use  implement by steven and kers
         */
        new Thread(timer).start();

        /**
         * Wait all the thread object created. Then open control panel GUI
         */
        Panel cp = new ControlPanel(this);
        cp.showInfo();
    }


    /**
     * Retrieves the Logger module for recording.
     * @return The Logger moudle.
     */
    public Logger getLogger() {
        return log;
    } // getLogger


    /**
     * Kiosk and elevator are appThread object. When they create, they will add into this method.
     * This method is for Building getThread(String id){}
     */
    public void regThread(AppThread appThread) {
        appThreads.put(appThread.getID(), appThread);
    } // regThread

    /**
     * This method is for getting the specify thread. Also get the thread's attribute
     * E.G. Eleavtor ((Elevator)(this.getThread("e" + 1))).getStatus() => get the e1(Elevator 2) status
     * @param id The element identifier for the simulation object.
     */
    public AppThread getThread(String id) {
        return appThreads.get(id);
    } // getThread

    /**
     * Get config file key value pair
     * @param property Key of the configuration property.
     * @return The value of the specified configuration property.
     */
    public String getProperty(String property) {
        return cfgProps.getProperty(property);
    } // getThread

    //getElevatorQueue()
    public String getElevatorQueue(){
        String geq = "";
    
        for(int i = 0; i < Elevator.elevatorCount; i++){
            HashMap<Integer, String> rq =  this.getThread("e" + i).getQueue();
            
             geq+= "Elevator "+i+ ": " + rq +"\n";
        }
        return geq;
    }

    /**
     * Returns the status of kiosk queue in a textual format for user to see.
     * @return A string representation of the status of the kiosk queue.
     * @see ControlPanel
     */
    public String getKioskQueue(){
        String gkq = "";
        
        for(int i = 0; i < Kiosk.koiskCount; i++){
            HashMap<Integer, String> rq =  this.getThread("k" + i).getQueue();
            //demo of how to get queue of kiosk, 
            //can also use this to get queue of elevator
             gkq+= "Floor "+i+ ": " + rq +"\n";
        }
        
        //ElevatorStatus status = ((Elevator)(this.getThread("e" + 1))).getStatus();
        //Algorithm stuff
        
        return gkq;
    }

    /**
     * Get all statuses of different elevators accordingly.
     * @return An array list of elevator statuses.
     */
    public ArrayList<ElevatorStatus> getElevatorStatus() {
        ArrayList<ElevatorStatus> es = null;
        for (int i = 0; i < Elevator.elevatorCount; i++) {
            es.add(((Elevator) (this.getThread("e" + i))).getStatus());
        }

        return es;
    }

    // TODO: Removing this method. Consider using push-in-push-out method to control the next hop of different elevators.
    @Deprecated
    public String getResult(int floor, String id) {
        for (int i = 0; i < Kiosk.koiskCount; i++) {
            HashMap<Integer, String> rq = this.getThread("k" + i).getQueue();
            //demo of how to get queue of kiosk,
            //can also use this to get queue of elevator
        }

        //ElevatorStatus status = ((Elevator)(this.getThread("e" + 1))).getStatus();
        //Algorithm stuff

        return "";//for duplicated request
    }
}
