/**
 * The Wheel class models a car's wheel in the racing game.
 * Each wheel has a grip level and a wear-based condition that degrades
 * over time. The wheel's effective grip decreases as the condition worsens.
 *
 * Developed by: Lucas Martins
 */

public class Wheel {
    private float condition;  // 1.0 => new, 0 => worn out
    private final float gripLevel;

    /**
     * Constructs a new {@code Wheel} with the given condition and grip level.
     * @param condition initial condition (0.0 to 1.0)
     * @param gripLevel base grip factor
     */
    public Wheel(float condition, float gripLevel) {
        this.condition = condition;
        this.gripLevel = gripLevel;
    }

    /**
     * Simulates wear on the wheel by reducing its condition slightly.
     * Minimum value is 0.0.
     */
    public void wearDown() {
        condition = Math.max(0, condition - 0.01f);
    }

    /**
     * Returns the effective grip of the wheel based on its current condition.
     * @return a float value representing actual grip
     */
    public float getGripLevel() {
        return gripLevel * condition;
    }
}
