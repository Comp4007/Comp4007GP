package MyApp;

import java.lang.Thread;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.stream.Stream;

import MyApp.elevator.*;
import MyApp.kiosk.*;
import MyApp.misc.*;
import MyApp.panel.AdminPanel;
import MyApp.panel.ControlPanel;
import MyApp.panel.Panel;
import MyApp.timer.Timer;

import static java.util.stream.Collectors.toMap;

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
    private final int totalDisplacementMeters;
    /**
     * An atomic reference of a dictionary storing all stoppale hops (floors) and the position, in meters, of displacement where the hop is. <br/>
     * See http://stackoverflow.com/questions/21616234/concurrent-read-only-hashmap
     */
    private final AtomicReference<LinkedHashMap<String, Floor>> arefFloorPositions;

    // http://cookieandcoketw.blogspot.hk/2013/03/java-hashmap-hashtable.html
    // http://www.infoq.com/cn/articles/ConcurrentHashMap

    private final ConcurrentHashMap<String, Floor> kioskHoppingRequests;

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
            this.totalDisplacementMeters = Integer.parseInt(cfgProps.getProperty("DisplacementMeters"));
        else
            throw new InvalidPropertiesFormatException("missing DisplacementMeters");

        {
            String[] floorNames;
            if (cfgProps.containsKey("FloorNames"))
                floorNames = cfgProps.getProperty("FloorNames").split("\\|");
            else
                throw new InvalidPropertiesFormatException("missing FloorNames");

            double[] floorPositions;
            if (cfgProps.containsKey("FloorPositions")) {
                Stream<String> s = Arrays.stream(cfgProps.getProperty("FloorPositions").split("\\|"));
                floorPositions = s.mapToDouble(Double::parseDouble).toArray();
            }
            else
                throw new InvalidPropertiesFormatException("missing FloorPositions");

            // for key-value pair, asserting array length size is same is required
            if (floorNames.length != floorPositions.length)
                throw new InvalidPropertiesFormatException("floorNames.length != floorPositions.length");

            // A dictionary storing all stoppale hops (floors) and the position, in meters, of displacement where the hop is.
            Hashtable<String, Floor> floorPositions1 = new Hashtable<>();
            Floor lowerFloor = null;
            for (int i = 0; i < floorNames.length; i++) {
                Floor floor = new Floor(floorNames[i], floorPositions[i]);
                floor.setLowerFloor(lowerFloor);
                floorPositions1.put(floorNames[i], floor);
                lowerFloor = floor;
            }

            LinkedHashMap<String, Floor> floorPositions2 = floorPositions1.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));

            this.arefFloorPositions = new AtomicReference<>(floorPositions2);
        }

        // get and configure logger
        ConsoleHandler conHd = new ConsoleHandler();
        conHd.setFormatter(new LogFormatter());
        log = Logger.getLogger(Building.class.getName());
        log.setUseParentHandlers(false);
        log.addHandler(conHd);
        log.setLevel(Level.INFO);
        appThreads = new Hashtable<String, AppThread>();

        kioskHoppingRequests = new ConcurrentHashMap<>();
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
    public void putThread(AppThread appThread) {
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
        
        for(int i = 0; i < Kiosk.kioskCount; i++){
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
        for (int i = 0; i < Kiosk.kioskCount; i++) {
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
    public final int getTotalDisplacementMeters() {
        return totalDisplacementMeters;
    }

    /**
     * Get an dictionary for all floors, with their names and the displacement that matches the vertical position of the floor.
     * @return A <code>Map</code> object that contains a list of: <br/>
     * <code>String</code> of the floor names and <br/>
     * <code>Floor</code> of the real floor object, which contains displacement that matches the vertical position of the floor in meters.
     */
    public final Map<String, Floor> getFloorPositions() {
        return arefFloorPositions.get();
    }

    public final String[] getFloorNames() {
        Map<String, Floor> fp = getFloorPositions();
        return fp.keySet().toArray(new String[fp.size()]);
    }

    public final Floor getFloorPosition(String floorName) {
        return arefFloorPositions.get().get(floorName);
    }

    // TODO: JavaDoc for kioskPushNewHopRequest(Kiosk, String)
    public synchronized Elevator kioskPushNewHopRequest(Kiosk kiosk, String destFloor) throws IndexOutOfBoundsException {
        Floor src = kiosk.getFloor();
        Floor dest = getFloorPositions().get(destFloor);

        if (dest == null)
            throw new IndexOutOfBoundsException("destFloor key not exist in floorPositions");

        // TODO: you got the source floor and dest floor pair, what to do?
        // TODO: remember to calculate which lift to catch the request and
        // TODO: push back to the lift to update its next destination.

        // TODO: return an Elevator that such src:dest pair assigned to
        return null;
    }
}
