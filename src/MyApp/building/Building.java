package MyApp.building;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import MyApp.elevator.*;
import MyApp.kiosk.*;
import MyApp.misc.*;
import MyApp.panel.AdminPanel;
import MyApp.panel.ControlPanel;
import MyApp.panel.Panel;
import MyApp.timer.Timer;

import static java.util.stream.Collectors.toMap;

// TODO: FUCK YOU! MORE COMMENTS IN CODES, NOT JUST JAVADOCS, LAZY SOUMA!

/**
 * Simulates all functionality of startElevatorStatusCacheThread centralised controller inside startElevatorStatusCacheThread building. <br/>
 * This may be used as entry point for simulation.
 */
public class Building {
    private static final int putStoppingHopMaxRetries = 100;
    /**
     * This is config file path
     */
    private static final String cfgFName = "etc/MyApp.cfg";
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
     * An atomic reference of startElevatorStatusCacheThread dictionary storing all stoppale hops (floors) and the position, in meters, of displacement where the hop is. <br/>
     * See http://stackoverflow.com/questions/21616234/concurrent-read-only-hashmap
     */
    private final AtomicReference<LinkedHashMap<String, Floor>> arefFloorPositions;
    // http://cookieandcoketw.blogspot.hk/2013/03/java-hashmap-hashtable.html
    // http://www.infoq.com/cn/articles/ConcurrentHashMap
    //JavaDoc for the kioskHoppingRequests
//    private final ConcurrentHashMap<String, Floor> kioskHoppingRequests;
    /**
     * Stores all the statuses of all the Elevators inside this Building, as a cache.
     */
    private final ConcurrentHashMap<Elevator, ElevatorStatus> elevatorsStatuses;
    /**
     * Holds the reference of the thread that refreshes the cache of statuses of all elevators.
     */
    private Thread threadBuildingRefreshElevatorStatusCache = null;

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
            } else
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

            LinkedHashMap<String, Floor> floorPositions2 = floorPositions1.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            this.arefFloorPositions = new AtomicReference<>(floorPositions2);
        }

        // get and configure logger
        ConsoleHandler conHd = new ConsoleHandler();
        conHd.setFormatter(new LogFormatter());
        log = Logger.getLogger(Building.class.getName());
        log.setUseParentHandlers(false);
        log.addHandler(conHd);
        log.setLevel(Level.INFO);
        appThreads = new Hashtable<>();

//        kioskHoppingRequests = new ConcurrentHashMap<>();
        elevatorsStatuses = new ConcurrentHashMap<>();
    }

    /**
     * Start running up the world of the simulation.
     */
    @SuppressWarnings("WeakerAccess")
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

        startElevatorStatusCacheThread();

        // This is for elevator use implement by steven and kers
        new Thread(timer).start();

        // Wait all the thread object created. Then open control panel GUI
        Panel cp = new ControlPanel(this);
        cp.showInfo();

        // show kiosk panel for testing
        KioskPanel kioskPanel = new KioskPanel(this);
        kioskPanel.showInfo();
    }

    /**
     * Ensures that the elevator status cache thread is running. Create new thread if not exist or not alive.
     */
    private void startElevatorStatusCacheThread() {
        if (threadBuildingRefreshElevatorStatusCache != null && threadBuildingRefreshElevatorStatusCache.isAlive())
            return;

        this.threadBuildingRefreshElevatorStatusCache = new Thread(() -> {
            List<Elevator> elevators = this.getThreads(Elevator.class);
            elevators.forEach(e -> this.elevatorsStatuses.put(e, e.getStatus()));
        }, "BuildingRefreshElevatorStatusCache");
    }

    /**
     * Retrieves the Logger module for recording.
     *
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
     *
     * @param id The element identifier for the simulation object.
     */
    public AppThread getThread(String id) {
        return appThreads.get(id);
    }

    /**
     * Get threads from the threads storage that matches the type extending.
     *
     * @param type The class object of the type that extends the AppThread.
     * @param <T>  The type that extends the AppThread.
     * @return A list of the threads that matches the type extending.
     */
    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public <T extends AppThread> List<T> getThreads(Class<T> type) {
        return appThreads.values().stream().filter((t) -> t.getClass() == type).map(t -> (T) t).collect(Collectors.toList());
    }

    /**
     * Get config file key value pair
     *
     * @param property Key of the configuration property.
     * @return The value of the specified configuration property.
     */
    public String getProperty(String property) {
        return cfgProps.getProperty(property);
    }

    /**
     * Returns the status of elevators queues in startElevatorStatusCacheThread textual format for user to see.
     *
     * @return A string representation of the status of the elevators queues.
     * @see ControlPanel
     */
    public String getElevatorQueue() {
        String geq = "";

        for (Elevator e : getThreads(Elevator.class)) {
            geq += "Elevator " + e.getID() + ": " + e.getQueue() + "\n";
        }
        return geq;
    }

    /**
     * Returns the status of kiosk queue in startElevatorStatusCacheThread textual format for user to see.
     *
     * @return A string representation of the status of the kiosk queue.
     * @see ControlPanel
     */
    public String getKioskQueue() {
        String gkq = "";

        for (int i = 0; i < Kiosk.kioskCount; i++) {
            HashMap<Integer, String> rq = this.getThread("k" + i).getQueue();
            // demo of how to get queue of kiosk, 
            // can also use this to get queue of elevator
            gkq += "Floor " + i + ": " + rq + "\n";
        }

        // ElevatorStatus status = ((Elevator)(this.getThread("e" + 1))).getStatus();
        // Algorithm stuff

        return gkq;
    }

    // TODO: remove if wasting space

    /**
     * [DEPRECATED]
     * Get all statuses of different elevators accordingly.
     *
     * @return An array list of elevator statuses.
     */
    @Deprecated
    public Collection<ElevatorStatus> getElevatorStatus() {
        /*
        ArrayList<ElevatorStatus> es = null;
        for (int i = 0; i < Elevator.elevatorCount; i++) {
            es.add(((Elevator) (this.getThread("e" + i))).getStatus());
        }

        return es;
        */
        return this.elevatorsStatuses.values();
    }

    /**
     * Get the total displacement that an elevator may travel for vertically within the building.
     *
     * @return The displacement, in meters.
     */
    public final int getTotalDisplacementMeters() {
        return totalDisplacementMeters;
    }

    /**
     * Get an dictionary for all floors, with their names and the displacement that matches the vertical position of the floor.
     *
     * @return A <code>Map</code> object that contains startElevatorStatusCacheThread list of: <br/>
     * <code>String</code> of the floor names and <br/>
     * <code>Floor</code> of the real floor object, which contains displacement that matches the vertical position of the floor in meters.
     */
    public final Map<String, Floor> getFloorPositions() {
        return arefFloorPositions.get();
    }

    /**
     * Get all the floor names as a String array.
     *
     * @return A string array of all floor names.
     */
    public final String[] getFloorNames() {
        Map<String, Floor> fp = getFloorPositions();
        return fp.keySet().toArray(new String[fp.size()]);
    }

    /**
     * Get floor object by the floor name.
     *
     * @param floorName The alias of the floor that is human-readable.
     * @return The floor object that is inside the building, containing the information and specifications about the floor.
     */
    public final Floor getFloorPosition(String floorName) {
        return arefFloorPositions.get().get(floorName);
    }

    /**
     * Provides a method for Kiosk to put a new hop request for an elevator to stop at.
     *
     * @param kiosk     The source Kiosk that puts the request into this Building.
     * @param destFloor The destination floor that, after passenger boarding from the source floor, which floor to let passenger alight.
     * @return The elevator that is assigned for passenger to board, or <code>null</code> if retried <code>putStoppingHopMaxRetries</code> times.
     * @throws IndexOutOfBoundsException Throws when floor name, which is value of <code>destFloor</code>, does not exist in <code>floorPositions</code>.
     */
    public synchronized Elevator putNewHopRequest(Kiosk kiosk, String destFloor) throws IndexOutOfBoundsException {
        Floor src = kiosk.getFloor();
        Floor dest = getFloorPositions().get(destFloor);

        if (dest == null)
            throw new IndexOutOfBoundsException("destFloor key not exist in floorPositions"); // TODO: throw or null;

        if (src.equals(dest))
            return null; // won't assign any but shit you donk

        boolean goingUp = dest.getYDisplacement() - src.getYDisplacement() > 0;

        // TODO: sort by: queueCount, direction, distance to src, speed (~=braking dist)
        // TODO: calculate which lift to catch the request
        LinkedList<ElevatorStatus> ess = new LinkedList<>(elevatorsStatuses.values());
//        ess.sort(new ElevatorStatusDistanceToFloorComparator(goingUp, src));

        int tries = 0;
        for (int i = 0; i < ess.size() && tries < putStoppingHopMaxRetries; i = ++tries % ess.size()) {
            ElevatorStatus es = ess.get(i);

            // ignoring such code, calculate if may stop within floor level by the Elevator itself.
            /*
            int direction = goingUp ? 1 : -1;
            double displacementElevatorStop = es.getYPosition() + es.getDirection() * es.getBreakDistance();
            double displacementFloor = dest.getYDisplacement();
            if (direction * displacementElevatorStop < direction * displacementFloor) // eg: 35 < 30 (going up) = false -> fail; -40 < -20 (going down) = true -> work
                continue;
            */

            // push back to the lift to update its next destination.
            if (es.getElevator().putNewDestination(dest)) {
                // return an Elevator that such src:dest pair assigned to
                return es.getElevator();
            }
        }

        // return null if retried many times but failed at all
        return null;
    }
}