package MyApp.building;

import MyApp.misc.ElevatorStatus;

import java.util.Comparator;

/**
 * Comparator for Elevator to compare and sort distance between a Floor and all Elevators.
 */
class ElevatorStatusDistanceToFloorComparator implements Comparator<ElevatorStatus> {
    boolean goingUp;
    Floor floor;

    public ElevatorStatusDistanceToFloorComparator(boolean goingUp, Floor floor) {
        this.goingUp = goingUp;
        this.floor = floor;
    }

    // sort by: queueCount, direction, distance to src, speed (~=braking dist)
    @Override
    public int compare(ElevatorStatus o1, ElevatorStatus o2) { // ASC
        // queue count
        int resultQueueCount = o1.getQueueCount() - o2.getQueueCount();
        if (resultQueueCount != 0)
            return resultQueueCount;

        // direction: same, still, reverse
        int resultDirection = 0;
        int requestDirection = goingUp ? 1 : -1;
        if (o1.getDirection() == o2.getDirection()) {
            resultDirection = 0;
        } else if (o1.getDirection() == requestDirection) {
            resultDirection = -1;
        } else if (o2.getDirection() == requestDirection) {
            resultDirection = 1;
        }
        if (resultDirection != 0)
            return resultDirection;

        // speed / velocity: slow to fast
        int resultVelocity = Math.abs((int) o1.getVelocity()) - Math.abs((int) o2.getVelocity());
        if (resultVelocity != 0)
            return resultVelocity;

        // distance to src + brake distance
        double distanceToSrcElevator1 = Math.abs(Math.abs(o1.getYPosition() + o1.getDirection() * o1.getBrakeDistance()) - floor.getYPosition());
        double distanceToSrcElevator2 = Math.abs(Math.abs(o2.getYPosition() + o2.getDirection() * o2.getBrakeDistance()) - floor.getYPosition());
        int resultDistanceToSrc = (int) distanceToSrcElevator1 - (int) distanceToSrcElevator2;
        if (resultDistanceToSrc != 0)
            return resultDistanceToSrc;

        return 0;
    }
}
