package MyApp.panel;

import MyApp.Building;

public class ControlPanel implements Panel {

    private Building building;

    public ControlPanel(Building building) {
        this.building = building;
    }

    @Override
    public void showInfo() {
        // TODO Auto-generated method stub
        building.getThread("").getQueue();//sample
        building.getElevatorStatus();//sample
    }

}
