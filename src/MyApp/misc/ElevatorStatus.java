package MyApp.misc;

import MyApp.elevator.Elevator;

public class ElevatorStatus {
	private Elevator elevator;
	private double height;
	private double velocity;
	private double breakDistance;
	private double acceleration;
	
	public ElevatorStatus(Elevator elevator, double height, double velocity, double breakDistance, double acceleration) {
		this.elevator = elevator;
		this.height = height;
		this.velocity = velocity;
		this.breakDistance = breakDistance;
		this.acceleration = acceleration;
	}

	public Elevator getElevator() {
		return elevator;
	}

	public double getYPosition(){
		return this.height;
	}
	
	public double getVelocity(){
		return this.velocity;
	}

	public double getBreakDistance(){
		return this.breakDistance;
	}

	public double getAcceleration(){
		return this.acceleration;
	}

}
