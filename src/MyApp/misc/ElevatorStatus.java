package MyApp.misc;

import MyApp.elevator.Elevator;

public class ElevatorStatus {
	private Elevator elevator;
	private double height;
	private double velocity;
	private double breakDistance;
	private double acceleration;
	private int queueCount;
	
	public ElevatorStatus(Elevator elevator, double height, double velocity, double breakDistance, double acceleration, int queueCount) {
		this.elevator = elevator;
		this.height = height;
		this.velocity = velocity;
		this.breakDistance = breakDistance;
		this.acceleration = acceleration;
		this.queueCount = queueCount;
	}

	public Elevator getElevator() {
		return elevator;
	}

	public double getYPosition(){
		return this.height;
	}

	/**
	 * The velocity (a.k.a. the speed) of the elevator that is used to traveling in the life shaft. <br/>
	 * Positive value means to travel upward, where negative value means to travel downward.
	 * @return The velocity (a.k.a. the speed) of the elevator.
	 */
	public double getVelocity(){
		return this.velocity;
	}

	public int getDirection() {
        if (getVelocity() > 0) {
            return 1;
        } else if (getVelocity() < 0) {
            return -1;
        }
        return 0;
    }

	public double getBreakDistance(){
		return this.breakDistance;
	}

	public double getAcceleration(){
		return this.acceleration;
	}

	public int getQueueCount() {
		return queueCount;
	}
}
