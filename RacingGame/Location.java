public class Location {
    private final String name;
    private final float x;
    private final float y;
    private final float angle; 
    private final boolean hasObstacle;

    public Location(String name, float x, float y, float angle) {
        this(name, x, y, angle, false);
    }

    public Location(String name, float x, float y, float angle, boolean hasObstacle) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.hasObstacle = hasObstacle;
    }

    public String getName() {
        return name;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean checkForObstacle() {
        return hasObstacle;
    }
    
    public float getAngle() {
        return angle;
    }
}
