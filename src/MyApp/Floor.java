package MyApp;

import java.io.Serializable;

public final class Floor implements Serializable, Comparable<Floor> {
    private final String name;
    private final double yDisplacement;

    private Floor lowerFloor = null;
    private Floor upperFloor = null;

    public Floor(String name, double yDisplacement) {
        this.name = name;
        this.yDisplacement = yDisplacement;
    }

    public String getName() {
        return name;
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
    }

    public void setUpperFloor(Floor upperFloor) {
        this.upperFloor = upperFloor;
        if (upperFloor == null) return;
        this.upperFloor.lowerFloor = this;
    }

    @Override
    public int compareTo(Floor comparing) {
        if (this.equals(comparing)) return 0;
        return (int)this.yDisplacement - (int)comparing.yDisplacement;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null &&
                (obj.getClass() == Floor.class) &&
                this.name.equals(((Floor)obj).name);
    }
}
