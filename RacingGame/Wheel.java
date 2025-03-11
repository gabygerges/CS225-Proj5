public class Wheel {
    private float condition;  // 1.0 => new, 0 => worn out
    private final float gripLevel;

    public Wheel(float condition, float gripLevel) {
        this.condition = condition;
        this.gripLevel = gripLevel;
    }

    // Each update, wheel wears a bit.
    public void wearDown() {
        condition = Math.max(0, condition - 0.01f);
    }

    // Effective grip based on current condition.
    public float getGripLevel() {
        return gripLevel * condition;
    }
}
