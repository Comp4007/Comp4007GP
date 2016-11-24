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

    //------------------------------------------------------------
    // main
    public static void main(String args[]) {
        Panel window = new AdminPanel();
        window.showInfo();
        Building building = new Building();
        building.startApp();
    } // main


    //------------------------------------------------------------
    // AppKickstarter
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


    //------------------------------------------------------------
    // startApp
    public void startApp() {
        // create threads
        /**
         * This is for elevator use  implement by steven and kers
         */
        Timer timer = new Timer("timer", this);

        /**
         * Create Kiosks  k0 = floor 1 kiosk, k1 = floor 2 kiosk ......
         */
        Kiosk.koiskCount = new Integer(this.getProperty("Kiosks"));
        for (int i = 0; i < Kiosk.koiskCount; i++) {
            Kiosk kiosk = new Kiosk("k" + i, this);
            new Thread(kiosk).start();
        }

        /**
         * Create elevator  e0 = elevator 1, e1 = elevator 2 ......
         */
        Elevator.elevatorCount = new Integer(this.getProperty("Elevators"));
        for (int i = 0; i < Elevator.elevatorCount; i++) {
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
    } // startApp


    //------------------------------------------------------------
    // getLogger
    public Logger getLogger() {
        return log;
    } // getLogger


    /**
     * Kiosk and elevator are appThread object. When they create, they will add into this method.
     * This method is for Building getThread(String id){}
     */
    //------------------------------------------------------------
    // regThread
    public void regThread(AppThread appThread) {
        appThreads.put(appThread.getID(), appThread);
    } // regThread

    /**
     * This method is for getting the specify thread. Also get the thread's attribute
     * E.G. Eleavtor ((Elevator)(this.getThread("e" + 1))).getStatus() => get the e1(Elevator 2) status
     */
    //------------------------------------------------------------
    // getThread
    public AppThread getThread(String id) {
        return appThreads.get(id);
    } // getThread

    /**
     * Get config file key value pair
     */
    //------------------------------------------------------------
    // getProperty
    public String getProperty(String property) {
        return cfgProps.getProperty(property);
    } // getThread

    public ArrayList<ElevatorStatus> getElevatorStatus() {
        ArrayList<ElevatorStatus> es = null;
        for (int i = 0; i < Elevator.elevatorCount; i++) {
            es.add(((Elevator) (this.getThread("e" + i))).getStatus());
        }

        return es;
    }

    public String getResult(int floor, String id) {

        for (int i = 0; i < Kiosk.koiskCount; i++) {
            Hashtable<Integer, String> rq = this.getThread("k" + i).getQueue();
            //demo of how to get queue of kiosk,
            //can also use this to get queue of elevator
        }

        //ElevatorStatus status = ((Elevator)(this.getThread("e" + 1))).getStatus();
        //Algorithm stuff

        return "";//for duplicated request
    }
} // AppKickstarter
