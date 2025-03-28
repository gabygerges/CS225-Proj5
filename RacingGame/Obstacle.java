/**
 * The Obstacle class represents temporary obstacles or power-ups on the racetrack.
 * Obstacles affect cars upon collision, either boosting or reducing speed.
 * Each obstacle has a type, visual shape, impact strength, color, lifetime, and coordinates.
 *
 * Developed and tested by: Abraham Arocha, Lucas Martins
 */

import java.awt.Color;

public class Obstacle {
    private final String type;
    private final float impact;
    private final float x;
    private final float y;
    private int lifetime;
    private final Color color;
    private final String shapeType;

    /**
     * Constructs an obstacle with the specified properties.
     * @param type the obstacle type
     * @param impact the amount to alter car speed
     * @param x x-coordinate of the obstacle
     * @param y y-coordinate of the obstacle
     * @param lifetime duration (in frames or ticks) the obstacle lasts
     * @param color visual color of the obstacle
     * @param shapeType rendering shape type (circle, square, etc.)
     */
    public Obstacle(String type, float impact, float x, float y, int lifetime, Color color, String shapeType) {
        this.type = type;
        this.impact = impact;
        this.x = x;
        this.y = y;
        this.lifetime = lifetime;
        this.color = color;
        this.shapeType = shapeType;
    }

    /** @return obstacle type string */
    public String getType() {
        return type;
    }

    /** @return impact value (used to alter car speed) */
    public float getImpact() {
        return impact;
    }

    /** @return x-coordinate */
    public float getX() {
        return x;
    }

    /** @return y-coordinate */
    public float getY() {
        return y;
    }

    /** @return current remaining lifetime */
    public int getLifetime() {
        return lifetime;
    }

    /** Decreases the lifetime of this obstacle by one unit. */
    public void decreaseLifetime() {
        lifetime--;
    }

    /** @return color used for rendering */
    public Color getColor() {
        return color;
    }

    /** @return shape type for rendering */
    public String getShapeType() {
        return shapeType;
    }


    /**
     * Applies this obstacle's effect to the given car.
     * A boost increases the car's speed, while other obstacles reduce it.
     * @param car the Car instance affected by the obstacle
     */
    public void applyToCar(Car car) {
        if (type.equalsIgnoreCase("boost")) {
            float newSpeed = car.getCurrentSpeed() + impact;
            car.setCurrentSpeed(newSpeed);
            RaceDisplay.log("Car " + car.getId() + " hit a speed boost! Speed increased by " + impact);
        } else {
            float newSpeed = car.getCurrentSpeed() - impact;
            car.setCurrentSpeed(newSpeed);
            RaceDisplay.log("Car " + car.getId() + " hit an obstacle (" + type + ")! Speed reduced by " + impact);
        }
    }

}
