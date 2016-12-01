package MyApp.building;

import MyApp.misc.ElevatorStatus;

import java.util.Comparator;

/**
 * Comparator for Elevator to compare and sort distance between a Floor and all Elevators.
 */
class ElevatorStatusDistanceToFloorComparator implements Comparator<ElevatorStatus> {
    private final boolean goingUp;
    private final Floor floor;

    /**
     * Creates a comparator for Elevator to compare and sort distance between a Floor and all Elevators.
     * @param goingUp If traffic direction of requesting hopping is going upward.
     * @param floor The speficied source floor to hop on.
     */
    ElevatorStatusDistanceToFloorComparator(boolean goingUp, Floor floor) {
        if (floor == null) throw new IllegalArgumentException("floor cannot be null");

        this.goingUp = goingUp;
        this.floor = floor;
    }

    /**
     * Do comparision with 2 {@code ElevatorStatus}es by items in queues, directions, distance to the specified floor, speed
     * @param o1 The first elevator status.
     * @param o2 The second elevator status.
     * @return Negative value if first elevator should be used first, where positive value if second elevator should be used first. Returns {@code 0} if both has same priority to be used.
     */
    @Override
    public int compare(ElevatorStatus o1, ElevatorStatus o2) { // ASC
        // queue count
        int resultQueueCount = o1.getQueueCount() - o2.getQueueCount();
        if (resultQueueCount != 0)
            return resultQueueCount;

        // distance to src + brake distance
        double distanceToSrcElevator1 = Math.abs(Math.abs(o1.getYPosition() + o1.getActualDirection() * o1.getBrakeDistance()) - floor.getYPosition());
        double distanceToSrcElevator2 = Math.abs(Math.abs(o2.getYPosition() + o2.getActualDirection() * o2.getBrakeDistance()) - floor.getYPosition());
        int resultDistanceToSrc = (int) distanceToSrcElevator1 - (int) distanceToSrcElevator2;
        if (resultDistanceToSrc != 0)
            return resultDistanceToSrc;

        // direction: same, still, reverse
        int resultDirection = 0;
        int requestDirection = goingUp ? 1 : -1;
        if (o1.getServingDirection() == o2.getServingDirection()) {
            resultDirection = 0;
        } else if (o1.getServingDirection() != -requestDirection) { // same or elevator is still
            resultDirection = -1;
        } else if (o2.getServingDirection() != -requestDirection) { // same or elevator is still
            resultDirection = 1;
        }
        if (resultDirection != 0)
            return resultDirection;

        // speed / velocity: slow to fast
        int resultVelocity = Math.abs((int) o1.getVelocity()) - Math.abs((int) o2.getVelocity());
        if (resultVelocity != 0)
            return resultVelocity;

        return 0;
    }
}
