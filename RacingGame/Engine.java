public class Engine {
    private final float horsePower;
    private final float maxSpeed;

    public Engine(float horsePower, float maxSpeed) {
        this.horsePower = horsePower;
        this.maxSpeed = maxSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getAcceleration() {
        return horsePower / 10.0f;
    }
}
