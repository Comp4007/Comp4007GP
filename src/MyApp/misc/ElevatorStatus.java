package MyApp.misc;

public class ElevatorStatus {
	
	public double height;
	public double velocity;
	private double breakDistance;
	private double acceleration;
	
	public ElevatorStatus(double height, double velocity, double breakDistance, double acceleration) {
		this.height = height;
		this.velocity = velocity;
		this.breakDistance = breakDistance;
		this.acceleration = acceleration;
	}
	
	public double getHeight(){
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
