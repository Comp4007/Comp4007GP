package MyApp.panel;

import MyApp.Building;

public class ControlPanel implements Panel{
	
	private Building building;
	
	public ControlPanel(Building building){
		this.building = building;
	}

	@Override
	public void showInfo() {
		// TODO Auto-generated method stub
		System.out.println("Elevator status :");
	//	for(int i=0;i< ~elevatorcount~ ; i++)
	//		System.out.println("Elevator "+i + ": " + building.getElevatorStatus());//sample
		System.out.println();
		System.out.println("Elevator queue :");
	//	for(int i=0;i< ~elevatorcount~ ; i++)
	//		System.out.println("Queue of Elevator "+i + ": " + building.getThread("e"+i).getQueue());//sample
		System.out.println();
		System.out.println("Kiosk queue :");
	//	for(int i=0;i< ~kioskcount~ ; i++)
	//		System.out.println("Queue of Elevator "+i + ": " + ~);
		
	}
	
}
