package MyApp.building;

import java.io.Serializable;

/**
 * Represents a floor that separates a building in a vertical spaces.
 */
public final class Floor implements Serializable, Comparable<Floor> {
    /**
     * Human-readable alias of the floor.
     */
    private final String name;

    /**
     * The vertical displacement between the ground of such floor and the sea level.
     */
    private final double yDisplacement;

    /**
     * The lower floor of this floor.
     */
    private Floor lowerFloor = null;
    /**
     * The upper floor of this floor.
     */
    private Floor upperFloor = null;

    /**
     * Creates a floor object.
     * @param name Human-readable alias of the floor.
     * @param yDisplacement The vertical displacement between the ground of such floor and the sea level.
     */
    public Floor(String name, double yDisplacement) {
        this.name = name;
        this.yDisplacement = yDisplacement;
    }

    /**
     * Gets the human-readable alias of the floor.
     * @return The name of the floor.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the vertical displacement between the ground of such floor and the sea level.
     * @return The vertical displacement between the ground of such floor and the sea level.
     */
    public double getYPosition() {
        return yDisplacement;
    }

    /**
     * Gets the lower floor.
     * @return The floor object that represents the lower floor.
     */
    public Floor getLowerFloor() {
        return lowerFloor;
    }

    /**
     * Gets the upper floor.
     * @return The floor object that represents the upper floor.
     */
    public Floor getUpperFloor() {
        return upperFloor;
    }

    /**
     * Sets the lower floor.
     * @param lowerFloor The lower floor.
     */
    public void setLowerFloor(Floor lowerFloor) {
        this.lowerFloor = lowerFloor;
        if (lowerFloor == null) return;
        this.lowerFloor.upperFloor = this;
    }

    /**
     * Sets the upper floor.
     * @param upperFloor The upper floor.
     */
    public void setUpperFloor(Floor upperFloor) {
        this.upperFloor = upperFloor;
        if (upperFloor == null) return;
        this.upperFloor.lowerFloor = this;
    }

    /**
     * Comparing this floor with another.
     * @param comparing The another floor.
     * @return Negative if this, or positive if another is upper floor.
     */
    @Override
    public int compareTo(Floor comparing) {
        if (this.equals(comparing)) return 0;
        return (int)this.yDisplacement - (int)comparing.yDisplacement;
    }

    /**
     * Comparing if this and another floor is the same floor.
     * @param obj Another floor to compare.
     * @return If these floors are same.
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null &&
                (obj.getClass() == Floor.class) &&
                this.yDisplacement == ((Floor)obj).yDisplacement &&
                this.name.equals(((Floor)obj).name);
    }
}
