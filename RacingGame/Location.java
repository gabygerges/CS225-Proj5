/**
 * Represents a specific checkpoint or coordinate in the race track.
 * Each location contains a name, coordinates (x, y), a directional angle,
 * and an optional obstacle indicator.
 *
 * Developed by: Abraham Arocha
 */

public class Location {
    private final String name;
    private final float x;
    private final float y;
    private final float angle; 
    private final boolean hasObstacle;

    /**
     * Constructs a Location without an obstacle.
     *
     * @param name  name of the location
     * @param x     X-coordinate
     * @param y     Y-coordinate
     * @param angle direction angle in radians
     */
    public Location(String name, float x, float y, float angle) {
        this(name, x, y, angle, false);
    }

    /**
     * Constructs a Location with full specification.
     *
     * @param name        name of the location
     * @param x           X-coordinate
     * @param y           Y-coordinate
     * @param angle       direction angle in radians
     * @param hasObstacle whether the location has an obstacle
     */
    public Location(String name, float x, float y, float angle, boolean hasObstacle) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.hasObstacle = hasObstacle;
    }

    /**
     * Returns the name of the location.
     *
     * @return location name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the X-coordinate of the location.
     *
     * @return X value
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the Y-coordinate of the location.
     *
     * @return Y value
     */
    public float getY() {
        return y;
    }

    /**
     * Checks whether the location has an obstacle.
     *
     * @return true if it has an obstacle, false otherwise
     */
    public boolean checkForObstacle() {
        return hasObstacle;
    }

    /**
     * Returns the angle representing direction from this location.
     *
     * @return angle in radians
     */
    public float getAngle() {
        return angle;
    }
}
