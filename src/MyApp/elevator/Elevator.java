package MyApp.elevator;

import MyApp.building.Floor;
import MyApp.misc.*;
import MyApp.timer.Timer;

import java.util.ArrayList;
import java.util.Collections;

import MyApp.building.Building;
import MyApp.kiosk.Kiosk;

/**
 * Represents an vertical-moving elevator used on a real life.
 */
public class Elevator extends AppThread implements Comparable<Elevator> {
    /**
     * This is count number of elevator. Also for building getElevatorQueueString() to get no. of elevator
     */
    public static int elevatorCount = 0;

    private int elevatorId;
    /**
     * Default setting in config file. Assume each floor has 4m
     */
    @Deprecated
    private double heightOfFloor;
    /**
     * Default setting in config file. Assume the accelation is 5
     */
    private double maxAccelerationRate;
    /**
     * Determine the direction of elevator. E.G. -5 or +5
     */
    private double accelerationRate;
    /**
     * Default setting in config file. Assume the elevator move 120 meter per 1 mins
     * This is reference hitachi elevator spec.
     */
    private double maxSpeed;
    /**
     * This is for elevator talk to kiosk.
     * When elevator let the passenger in, elevator will send msg(call kiosk finishRequest() => remove the request)
     */
    private ArrayList<MBox> kioskMBox;
    /**
     * This parameter represent the vertical position (Y-axis) of the elevator in the lift shaft.
     * This is calculated from the ground of the cab of the lift.
     */
    private double yPosition = 0; // initial position of lift
    /**
     * This parameter represent velocity of elevator
     */
    private double speed = 0;
    /**
     * It is an object save all the elevator data (height, breakDistance,...)
     * Other class can get the object and get those data for specific elevator
     */
    @Deprecated
    private ElevatorStatus status;
    /**
     * Default setting in config file. Elevator will update itself for 30ms
     */
    private int updateWaitDuration;
    /**
     * Storing the last moment that called the {@code Simulate()}.
     */
    private long lastCallSimulate;
    /**
     * Use array list become mission queue
     * One is for elevator move up , one is for elevator move down
     * They will clean one direction of mission first, then use other one
     * This process will repeat
     */
    private ArrayList<Floor> missionQueueUpward = new ArrayList<>();
    private ArrayList<Floor> missionQueueDownward = new ArrayList<>();
    /**
     * Get the floor list form building for the target number
     */
    private String[] floorList;
    /**
     * Indicates which direction of traffic this Elevator is serving and will serve first.
     */
    private int servingDirection = 0;

    /**
     * Creates an {@code Elevator} instance.
     * @param id The ID to be used.
     * @param building Building which this elevator belongs to.
     */
    public Elevator(String id, Building building) {
        super(id, building);
        //Get property from building object
        this.heightOfFloor = Double.parseDouble(building.getProperty("HeightOfFloor"));
        this.maxAccelerationRate = Double.parseDouble(building.getProperty("Acceleration"));
        this.maxSpeed = Double.parseDouble(building.getProperty("MaxSpeed"));
        this.updateWaitDuration = Integer.parseInt(building.getProperty("TimerTicks"));
        //Get all kiosk MBox for communication with kiosk
        kioskMBox = new ArrayList<MBox>();
        for (int i = 0; i < Kiosk.kioskCount; i++) {
            kioskMBox.add(building.getThread("k" + i).getMBox());
        }
        elevatorId = elevatorCount++;
        floorList = building.getFloorNames();
    }

    /**
     * Outputs the physics and operational status of this elevator.
     * @return The {@code ElevatorStatus} object instance that represents its status of this elevator at the moment.
     */
    public final synchronized ElevatorStatus getStatus() {
        return new ElevatorStatus(
                this,
                yPosition,
                speed,
                //Based on the default setting of minOfMeter and accelerationParameter to count brakDistance
                // v^2 - u^2 = 2as, v = initial m/s, u = target m/s, a = acceleration m/s/s, s = displacement m
                Math.abs(speed * -speed / -maxAccelerationRate / 2),
                accelerationRate,
                missionQueueUpward.size() + missionQueueDownward.size(),
                servingDirection);
    }

    /**
     * Get the Elevator-specific ID of this elevator.
     * @return The ID of this elevator.
     */
    public int getElevatorId() {
        return elevatorId;
    }

    /**
     * Get the index of floor in floor names dictionary.
     * @param floor The floor to ask for.
     * @return The index in the dictionary.
     */
    public int getFloorIndex(Floor floor) {
        for (int i = 0; i < floorList.length; i++) {
            if (floorList[i].equals(floor.getName()))
                return i;
        }
        return -1;
    }

    /**
     * When building finish the simulate, the target result will use this method to pass in elevator mission queue
     * When elevator accept the request from building, it will rearrange the mission queue
     *
     * @param target The destination floor to hop on.
     */
    public void addQueue(Floor target) {
        queue.put(getFloorIndex(target), id);

        ArrayList<Floor> missionQueue;
        int direction = (int)(target.getYPosition() - getStatus().getYPosition());

        if (direction > 0)
            missionQueue = missionQueueUpward;
        else
            missionQueue = missionQueueDownward;

        //If the target is already in mission queue, no need to add.
        if (missionQueue.contains(target))
            return;

        missionQueue.add(target);

        //The rearrange the mission queue(Split two mission queue one is up one is down)
        if (direction > 0) {
            Collections.sort(missionQueue);
        } else {
            Collections.sort(missionQueue, Collections.reverseOrder());
        }
    }

    /**
     * Perform physic simulations of the {@code Elevator} by changing its physic parameters during passing {@code elapseMillSec} ms of time.
     * @throws InterruptedException If this thread is interrupted by any other threads that needs it to be terminated.
     */
    private void simulate(long elapseMillSec) throws InterruptedException {

        // set this elevator may serve any direction if both jobs are done
        // switch direction if same direction has no jobs to work on
        if (servingDirection == 0) {
            if (missionQueueUpward.size() > 0) {
                servingDirection = 1;
            } if (missionQueueDownward.size() > 0) {
                servingDirection = -1;
            }
            return;
        } else if (missionQueueUpward.size() == 0 && missionQueueDownward.size() == 0){
            servingDirection = 0;
            return;
        } else if ((servingDirection > 0 && missionQueueUpward.size() == 0) || (servingDirection < 0 && missionQueueDownward.size() == 0)) {
            servingDirection = -servingDirection;
        }

        // select which queue to use, upward or downward
        ArrayList<Floor> missionQueue;
        if (servingDirection > 0)
            missionQueue = missionQueueUpward;
        else
            missionQueue = missionQueueDownward;

        double brakeDistance = getStatus().getBrakeDistance();
        Floor target = missionQueue.get(0);
        double targetYPos = target.getYPosition();

        // upward and downward use the same formula. generalised.
        if (targetYPos != this.yPosition) {
            // holding the speed or accelerate
            if (Math.abs(speed) >= maxSpeed) {
                speed = servingDirection * maxSpeed;
                accelerationRate = 0;
            } else if (Math.abs(speed) < maxSpeed) {
                accelerationRate = servingDirection * maxAccelerationRate;
            }

            // estimate if continue to accelerate, where this life will be at, where it should actually brake?
            double whatYPosWillBeIfNotBrake = this.yPosition + (speed + accelerationRate * elapseMillSec / 1000) * elapseMillSec / 1000 + 0.5 * (accelerationRate) * Math.pow(elapseMillSec / 1000, 2);

            // brake?
//            log.info(String.format("??? %.3f >= %.3f ???", yPosition, targetYPos - brakeDistance));
            if (servingDirection * whatYPosWillBeIfNotBrake >= servingDirection * (targetYPos - servingDirection * (brakeDistance + 0.05))) {
                log.info("should brake");
                accelerationRate = servingDirection * -maxAccelerationRate;
            }

            // change the physics of this elevator
            speed += accelerationRate * elapseMillSec / 1000;

            // over-speed controlling
            if (servingDirection * speed < 0) {
                speed = 0;
                accelerationRate = 0;
            } else if (servingDirection * speed > maxSpeed) {
                speed = maxSpeed;
                accelerationRate = 0;
            }
        }

        // do a movement physics
        this.yPosition += speed * elapseMillSec / 1000 + 0.5 * (accelerationRate) * Math.pow(elapseMillSec / 1000, 2);

        // if this lift is stable then it must reached the target, remove one
        if (speed == 0) {
            this.yPosition = targetYPos;
            queue.remove(getFloorIndex(target));
            missionQueue.remove(0);

            // simulation of time to open the door
            Thread.sleep(5000);
        }

        // output elevator physics info
        log.info(String.format("elevator %d: height = %.2f m, %.2f m/s, %.2f m/s/s", this.getElevatorId(), this.yPosition, speed, accelerationRate));

        lastCallSimulate = System.nanoTime();
    }

    /**
     * Called by the {@code Thread} class to simulate every elapse of running this elevator.
     */
    public void run() {
        while (true) {
            int timerID = Timer.setTimer(id, updateWaitDuration);
            Msg msg = mbox.receive();

            if (!msg.getSender().equals("Timer"))
                break;

            try {
                simulate(updateWaitDuration);
            } catch (InterruptedException e) {
                System.out.println("Elevator interrupted, terminating.");
            }
        }
        System.out.println(id + ": Terminating This Lift!");
        System.exit(0);
    }

    /**
     * Building assign the request to elevator
     * Elevator will simulate the destination whether can stop or not
     * If it can stop, return true. Then manage the mission queue.
     * If it cannot stop, return false.
     *
     * @param floor
     * @return
     */
    public final synchronized boolean putNewDestination(Floor floor) {
        boolean availableStop = false;

        ElevatorStatus status = getStatus();
        double yLift = status.getYPosition();
        double yFloor = floor.getYPosition();
        int dir = status.getActualDirection();
        double brakeDistance = status.getBrakeDistance();
        // Get the floor height plus breaking distance to compare with the height of elevator (Use the top(y position) of elevator as the height)
        // First check the direction of elevator, if it is moving down(The height of elevator - 4m(height of floor)), y displacement + breaking distance
        // if it is moving up, y displacement - breaking distance
        // (1)      yLift + brakeDistance <= yFloor (up)
        // (2)      yLift - brakeDistance >= yFloor (dn)
        // (flip 2) -yLift + brakeDistance <= -yFloor (dn)
        // (3=1+2)  dir * yLift + brakeDistance <= dir * yFloor
        if (availableStop = dir * yLift + brakeDistance <= dir * yFloor) {
            //Add the request to mission queue, but the queue must rearrange (ascending order)
            addQueue(floor);
        }

        return availableStop;
    }

    /**
     * Comparing this {@code Elevator} with another {@code Elevator}.
     * @param o The another elevator to be compared with.
     * @return An integer which indicates that this or another elevator has higher priority to be listed.
     */
    @Override
    public int compareTo(Elevator o) {
        return this.elevatorId - o.elevatorId;
    }
}
