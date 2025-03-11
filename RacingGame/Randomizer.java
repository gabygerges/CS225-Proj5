import java.awt.Color;
import java.util.Random;

public class Randomizer {
    private static final Random rand = new Random();

    // Random speed between ~50% and 100% of max.
    public static float generateRandomSpeed(float maxSpeed) {
        float minSpeed = maxSpeed / 2;
        return minSpeed + rand.nextFloat() * (maxSpeed - minSpeed);
    }

    // Small random lateral offset for AI (-2..2).
    public static float generateRandomLateralDelta() {
        return (float) ((Math.random() * 4) - 2);
    }
    
    // Generate an obstacle near or on the track.
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
        String[] types = {"Oil Spill", "Pothole", "Debris"};
        String type = types[rand.nextInt(types.length)];
        float impact = 1 + rand.nextFloat() * 3;
        int lifetime = 30 + rand.nextInt(40);
        
        String[] shapes = {"circle", "square", "triangle"};
        String shapeType = shapes[rand.nextInt(shapes.length)];
        
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
            default:
                color = Color.MAGENTA;
                break;
        }
        
        return new Obstacle(type, impact, obsX, obsY, lifetime, color, shapeType);
    }
}
