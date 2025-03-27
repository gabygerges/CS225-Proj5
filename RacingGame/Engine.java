/**
 * Represents the engine component of a car in the racing simulation.
 * Contains information about horsepower and maximum speed, and provides
 * acceleration derived from the horsepower.
 *
 * Developed by; Abraham Arocha
 */

public class Engine {
    private final float horsePower;
    private final float maxSpeed;

    /**
     * Constructs an Engine with the specified horsepower and maximum speed.
     *
     * @param horsePower the engine's horsepower
     * @param maxSpeed   the engine's maximum speed
     */
    public Engine(float horsePower, float maxSpeed) {
        this.horsePower = horsePower;
        this.maxSpeed = maxSpeed;
    }

    /**
     * Returns the maximum speed of the engine.
     *
     * @return the maximum speed
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Calculates and returns the acceleration of the engine.
     * Acceleration is computed as horsepower divided by 10.
     *
     * @return the calculated acceleration
     */
    public float getAcceleration() {
        return horsePower / 10.0f;
    }
}
