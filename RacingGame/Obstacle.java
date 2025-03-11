import java.awt.Color;

public class Obstacle {
    private final String type;
    private final float impact; 
    private final float x;
    private final float y;
    private int lifetime;       
    private final Color color;
    private final String shapeType; 

    public Obstacle(String type, float impact, float x, float y, int lifetime, Color color, String shapeType) {
        this.type = type;
        this.impact = impact;
        this.x = x;
        this.y = y;
        this.lifetime = lifetime;
        this.color = color;
        this.shapeType = shapeType;
    }

    public String getType() {
        return type;
    }

    public float getImpact() {
        return impact;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getLifetime() {
        return lifetime;
    }
    
    public void decreaseLifetime() {
        lifetime--;
    }

    public Color getColor() {
        return color;
    }

    public String getShapeType() {
        return shapeType;
    }
    
    // Applies this obstacle's effect to the car.
    public void applyToCar(Car car) {
        float newSpeed = car.getCurrentSpeed() - impact;
        car.setCurrentSpeed(newSpeed);
        RaceDisplay.log("Car " + car.getId() + " hit an obstacle (" 
                + type + ")! Speed reduced by " + impact);
    }
}
