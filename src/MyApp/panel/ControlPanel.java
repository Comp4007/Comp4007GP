package MyApp.panel;

import MyApp.building.Building;

import javax.swing.JFrame;

public class ControlPanel implements Panel{
	private JFrame frame;
	
	private Building building;
	
	public ControlPanel(Building building){
		this.building = building;
	}

	public void showInfo() {
		// TODO Auto-generated method stub
		System.out.println("Elevator status :");
		//System.out.println(building.getElevatorStatus());//sample
		System.out.println();
		
		System.out.println("Elevator queue :");
		//System.out.printf(building.getElevatorQueueString());//sample
		System.out.println();
		
		System.out.println("Kiosk queue: ");
		System.out.printf(building.getKioskQueueString());
		
	}

    @Override
    public void dismissInfo() {

    }
}
