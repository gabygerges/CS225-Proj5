/**
 * The Randomizer class provides utility methods to generate random
 * values used during the car racing game.
 * It supports generation of random speeds, lateral movement for AI cars,
 * and fully configured random obstacles to be placed on or near the track.
 *
 * Responsibilities:
 *   Provide random speed values for car behavior
 *   Create slight lateral offsets to simulate AI drifting
 *   Generate various random obstacles with types, colors, shapes, and positions
 *
 *   Developed by: Abraham Arocha, Lucas Martins
 */
import java.awt.Color;
import java.util.Random;

public class Randomizer {
    private static final Random rand = new Random();

    /**
     * Generates a random speed between 50% and 100% of the car's max speed.
     * @param maxSpeed the car's maximum speed
     * @return a random float speed within the valid range
     */
    public static float generateRandomSpeed(float maxSpeed) {
        float minSpeed = maxSpeed / 2;
        return minSpeed + rand.nextFloat() * (maxSpeed - minSpeed);
    }

    /**
     * Produces a small random lateral movement (between -2 and +2) for AI simulation.
     * @return a float representing lateral drift delta
     */
    public static float generateRandomLateralDelta() {
        return (float) ((Math.random() * 4) - 2);
    }

    /**
     * Generates a random obstacle on or near the race track with varied properties.
     * @return a configured Obstacle instance
     */
    public static Obstacle generateRandomObstacle() {
        float centerX = RaceDisplay.TRACK_CENTER_X;
        float centerY = RaceDisplay.TRACK_CENTER_Y;
        float a = RaceDisplay.TRACK_A;
        float b = RaceDisplay.TRACK_B;

        // Random angle on the ellipse.
        float angle = (float) (Math.random() * 2 * Math.PI);
        float baseX = centerX + a * (float)Math.cos(angle);
        float baseY = centerY + b * (float)Math.sin(angle);

        // Tangent & right normal for lateral offset (±30).
        double tx = -a * Math.sin(angle);
        double ty = b * Math.cos(angle);
        double norm = Math.sqrt(tx*tx + ty*ty);
        if (norm < 0.0001) {
            norm = 0.0001;
        }
        double unitTx = tx / norm;
        double unitTy = ty / norm;
        double rightNormalX = unitTy;
        double rightNormalY = -unitTx;

        float lateral = (float) ((Math.random() * 60) - 30);
        float obsX = baseX + lateral * (float)rightNormalX;
        float obsY = baseY + lateral * (float)rightNormalY;

        // Obstacle type, shape, color.
        String[] types = {"Oil Spill", "Pothole", "Debris", "Boost"};
        String type = types[rand.nextInt(types.length)];
        float impact = 1 + rand.nextFloat() * 3;
        int lifetime = 30 + rand.nextInt(40);

        String shapeType;
        if (type.equalsIgnoreCase("boost")) {
            shapeType = "circle";
        } else {
            String[] shapes = {"circle", "square", "triangle"};
            shapeType = shapes[rand.nextInt(shapes.length)];
        }

        Color color;
        switch (type) {
            case "Oil Spill":
                color = Color.BLACK;
                break;
            case "Pothole":
                color = Color.DARK_GRAY;
                break;
            case "Debris":
                color = Color.ORANGE;
                break;
            case "Boost":
                color = Color.GREEN;
                break;
            default:
                color = Color.MAGENTA;
                break;
        }
        return new Obstacle(type, impact, obsX, obsY, lifetime, color, shapeType);
    }
}
