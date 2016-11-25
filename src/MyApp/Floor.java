package MyApp;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Floor implements Serializable, Comparable<Floor> {
    private final String floor;
    private final double yDisplacement;

    private Floor lowerFloor = null;
    private Floor upperFloor = null;
    private int sortIndex = 0; // TODO: auto-update

    public Floor(String floor, double yDisplacement) {
        this.floor = floor;
        this.yDisplacement = yDisplacement;
    }

    public String getFloor() {
        return floor;
    }

    public double getYDisplacement() {
        return yDisplacement;
    }

    public Floor getLowerFloor() {
        return lowerFloor;
    }

    public Floor getUpperFloor() {
        return upperFloor;
    }

    public void setLowerFloor(Floor lowerFloor) {
        this.lowerFloor = lowerFloor;
        if (lowerFloor == null) return;
        this.lowerFloor.upperFloor = this;
        this.updateLowerFloorSortIndex();
    }

    public void setUpperFloor(Floor upperFloor) {
        this.upperFloor = upperFloor;
        if (upperFloor == null) return;
        this.upperFloor.lowerFloor = this;
        this.updateUpperFloorSortIndex();
    }

    private void updateLowerFloorSortIndex() {
        Floor prev = this;
        Floor current = this;
        for (; (current = current.getLowerFloor()) != null; prev = current) {
            current.sortIndex = prev.sortIndex - 1;
        }
    }

    private void updateUpperFloorSortIndex() {
        Floor prev = this;
        Floor current = this;
        for (; (current = current.getLowerFloor()) != null; prev = current) {
            current.sortIndex = prev.sortIndex + 1;
        }
    }

    @Override
    public int compareTo(Floor comparing) {
        return this.sortIndex - comparing.sortIndex;
    }
}
