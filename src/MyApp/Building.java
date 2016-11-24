package MyApp;

import java.lang.Thread;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import MyApp.elevator.*;
import MyApp.kiosk.*;
import MyApp.misc.*;
import MyApp.panel.AdminPanel;
import MyApp.panel.ControlPanel;
import MyApp.panel.Panel;
import MyApp.timer.Timer;

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
    private final int displacementMeters;
    /**
     * A dictionary storing all stoppale hops (floors) and the position, in meters, of displacement where the hop is.
     */
    private final Hashtable<String, Double> floorPositions;
    private final AtomicReference<Hashtable<String, Double>> arefFloorPositions; // http://stackoverflow.com/questions/21616234/concurrent-read-only-hashmap

    /**
     * Java.exe entry point for loading up the Building simulation element.
     */
    public static void main(String args[]) {
        Panel window = new AdminPanel();
        window.showInfo();
        Building building;
        try {
            building = new Building();
        } catch (Exception e) {
            System.out.println("Cannot instantiate Building object:");
            e.printStackTrace();
            return;
        }
        building.startApp();
    }

    /**
     * Initialisation of the Building simulation element. <br/>
     * It will also instantiate all lifts, kiosks, control panels and other related stuffs.
     */
    public Building() throws InvalidPropertiesFormatException {
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

        // values for final properties
        if (cfgProps.containsKey("DisplacementMeters"))
            this.displacementMeters = Integer.parseInt(cfgProps.getProperty("DisplacementMeters"));
        else
            throw new InvalidPropertiesFormatException("missing DisplacementMeters");

        {
            String[] floorNames;
            if (cfgProps.containsKey("FloorNames"))
                floorNames = cfgProps.getProperty("FloorNames").split("|");
            else
                throw new InvalidPropertiesFormatException("missing FloorNames");

            double[] floorPositions;
            if (cfgProps.containsKey("FloorPositions"))
                floorPositions = Arrays.stream(cfgProps.getProperty("FloorPositions").split("|")).mapToDouble(Double::parseDouble).toArray();
            else
                throw new InvalidPropertiesFormatException("missing FloorPositions");

            // for key-value pair, asserting array length size is same is required
            if (floorNames.length != floorPositions.length)
                throw new InvalidPropertiesFormatException("floorNames.length != floorPositions.length");

            this.floorPositions = new Hashtable<>();
            for (int i = 0; i < floorNames.length; i++) {
                this.floorPositions.put(floorNames[i], floorPositions[i]);
            }

            this.arefFloorPositions = new AtomicReference<>(this.floorPositions);
        }

        // get and configure logger
        ConsoleHandler conHd = new ConsoleHandler();
        conHd.setFormatter(new LogFormatter());
        log = Logger.getLogger(Building.class.getName());
        log.setUseParentHandlers(false);
        log.addHandler(conHd);
        log.setLevel(Level.INFO);
        appThreads = new Hashtable<String, AppThread>();
    }

    /**
     * Start running up the world of the simulation.
     */
    public void startApp() {
        // This is for elevator use implement by steven and kers
        Timer timer = new Timer("timer", this);

        // Create Kiosks k0 = floor 1 kiosk, k1 = floor 2 kiosk ......
        int kc = new Integer(this.getProperty("Kiosks"));
        for (int i = 0; i < kc; i++) {
            Kiosk kiosk = new Kiosk("k" + i, this);
            new Thread(kiosk).start();
        }

        // Create elevator e0 = elevator 1, e1 = elevator 2 ......
        int e = new Integer(this.getProperty("Elevators"));
        for (int i = 0; i < e; i++) {
            Elevator elevator = new Elevator("e" + i, this);
            new Thread(elevator).start();
        }
        // start threads

        // This is for elevator use implement by steven and kers
        new Thread(timer).start();

        // Wait all the thread object created. Then open control panel GUI
        Panel cp = new ControlPanel(this);
        cp.showInfo();
    }

    /**
     * Retrieves the Logger module for recording.
     * @return The Logger moudle.
     */
    public Logger getLogger() {
        return log;
    }

    /**
     * Kiosk and elevator are appThread object. When they create, they will add into this method.<br/>
     * This method is for <code>Building:getThread(String id)</code>
     */
    public void regThread(AppThread appThread) {
        appThreads.put(appThread.getID(), appThread);
    }

    /**
     * Getting the specify thread. Also get the thread's attribute. <br/>
     * E.g.:
     * <code>Eleavtor ((Elevator)(this.getThread("e" + 1))).getStatus()</code>
     * → get the e1(Elevator 2) status
     * @param id The element identifier for the simulation object.
     */
    public AppThread getThread(String id) {
        return appThreads.get(id);
    }

    /**
     * Get config file key value pair
     * @param property Key of the configuration property.
     * @return The value of the specified configuration property.
     */
    public String getProperty(String property) {
        return cfgProps.getProperty(property);
    }

    /**
     * Returns the status of elevators queues in a textual format for user to see.
     * @return A string representation of the status of the elevators queues.
     * @see ControlPanel
     */
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
            // demo of how to get queue of kiosk, 
            // can also use this to get queue of elevator
             gkq+= "Floor "+i+ ": " + rq +"\n";
        }
        
        // ElevatorStatus status = ((Elevator)(this.getThread("e" + 1))).getStatus();
        // Algorithm stuff
        
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
            // demo of how to get queue of kiosk,
            // can also use this to get queue of elevator
        }

        // ElevatorStatus status = ((Elevator)(this.getThread("e" + 1))).getStatus();
        // Algorithm stuff

        return ""; // for duplicated request
    }

    /**
     * Get the total displacement that an elevator may travel for vertically within the building.
     * @return The displacement, in meters.
     */
    public final int getDisplacementMeters() {
        return displacementMeters;
    }

    /**
     * Get an dictionary for all floors, with their names and the displacement that matches the vertical position of the floor.
     * @return A <code>Hastable</code> object that contains a list of: <br/>
     * <code>String</code> of the floor name and <br/>
     * <code>double</code> of the displacement that matches the vertical position of the floor in meters.
     */
    public final Hashtable<String, Double> getFloorPositions() {
        return arefFloorPositions.get();
    }

    // TODO: JavaDoc for kioskPushNewHopRequest(Kiosk, String)
    public synchronized void kioskPushNewHopRequest(Kiosk kiosk, String destFloor) {
        // TODO: you got the source floor and dest floor pair, what to do?
        // TODO: remember to calculate which lift to catch the request and push back to the lift to update its next destination.
    }
}
