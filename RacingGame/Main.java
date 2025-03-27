import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create a Race instance.
        Race race = new Race();
        
        // Prompt user for number of laps.
        String lapsInput = JOptionPane.showInputDialog(null, 
                "Enter the number of laps:", "2");
        int laps = 2;
        try {
            laps = Integer.parseInt(lapsInput);
        } catch (NumberFormatException e) {
            // fallback to 2 if invalid
        }
        race.setTotalLaps(laps);

        /*
         *  We define FIVE checkpoints (A,B,C,D,E) so that the last checkpoint (E) 
         *  is physically the same as the first (A). This helps multi-lap transitions 
         *  appear seamless with no visible teleportation.
         *  
         *  TRACK_CENTER_X=500, TRACK_CENTER_Y=250, a=350, b=180
         *   angle=0    => (850, 250)
         *   angle=pi/2 => (500, 430)
         *   angle=pi   => (150, 250)
         *   angle=3pi/2=> (500, 70)
         *   angle=2pi  => (850, 250) same as angle=0
         */
        
        Location A = new Location("A", 850, 250, 0f);
        Location B = new Location("B", 500, 430, (float)(Math.PI/2));
        Location C = new Location("C", 150, 250, (float)Math.PI);
        Location D = new Location("D", 500, 70, (float)(3*Math.PI/2));
        // E is physically same as A:
        Location E = new Location("E", 850, 250, (float)(2*Math.PI));
        
        // For each route, we rotate the start location 
        // but still have all five checkpoints so the track loops smoothly.
        Route route1 = new Route(Arrays.asList(A, B, C, D, E));
        Route route2 = new Route(Arrays.asList(B, C, D, E, A));
        Route route3 = new Route(Arrays.asList(C, D, E, A, B));

        // Create engines.
        Engine engine1 = new Engine(200, 10);
        Engine engine2 = new Engine(220, 12);
        Engine engine3 = new Engine(210, 11);

        // Create wheels (all identical).
        List<Wheel> wheels1 = Arrays.asList(
            new Wheel(1.0f, 1.0f),
            new Wheel(1.0f, 1.0f),
            new Wheel(1.0f, 1.0f),
            new Wheel(1.0f, 1.0f)
        );
        List<Wheel> wheels2 = Arrays.asList(
            new Wheel(1.0f, 1.0f),
            new Wheel(1.0f, 1.0f),
            new Wheel(1.0f, 1.0f),
            new Wheel(1.0f, 1.0f)
        );
        List<Wheel> wheels3 = Arrays.asList(
            new Wheel(1.0f, 1.0f),
            new Wheel(1.0f, 1.0f),
            new Wheel(1.0f, 1.0f),
            new Wheel(1.0f, 1.0f)
        );

        // Create three cars, each assigned a route.
        Car car1 = new Car(1, engine1, wheels1, route1);
        Car car2 = new Car(2, engine2, wheels2, route2);
        Car car3 = new Car(3, engine3, wheels3, route3);

        // Set total laps for multi-lap logic.
        car1.setTotalLaps(laps);
        car2.setTotalLaps(laps);
        car3.setTotalLaps(laps);

        // Add cars to the race.
        race.addCar(car1);
        race.addCar(car2);
        race.addCar(car3);

        // Create GUI on the event thread.
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Oval Car Racing Simulator");
            RaceDisplay display = new RaceDisplay(race);
            race.setRaceDisplay(display);
            frame.add(display);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
