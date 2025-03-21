import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create a Race instance.
        Race race = new Race();
        
        // Prompt user for number of laps.
        String lapsInput = JOptionPane.showInputDialog(null, 
                "Enter the number of laps:", 
                "2");
        int laps = 2;
        try {
            laps = Integer.parseInt(lapsInput);
        } catch (NumberFormatException e) {
            // fallback to 2 if invalid
        }
        race.setTotalLaps(laps);

        /*
         * We'll define an oval with four main checkpoints: 
         *   A (angle=0), B (angle=π/2), C (angle=π), D (angle=3π/2)
         * and then a FIFTH checkpoint physically identical to the first 
         * at angle=2π. 
         * That ensures we only finalize a lap once the car crosses 
         * the start/finish line again.
         */

        // --- Car #1 route: A->B->C->D->A(2π)
        Location A0 = new Location("A", 850, 250, 0f);
        Location B  = new Location("B", 500, 430, (float)(Math.PI / 2));
        Location C  = new Location("C", 150, 250, (float)Math.PI);
        Location D  = new Location("D", 500, 70,  (float)(3 * Math.PI / 2));
        Location A2 = new Location("A", 850, 250, (float)(2 * Math.PI)); 
        // physically same as A0, but angle=2π

        Route route1 = new Route(Arrays.asList(A0, B, C, D, A2));

        // --- Car #2 route: B->C->D->A(2π)->B(2π+π/2)
        Location Bstart = new Location("B", 500, 430, (float)(Math.PI / 2));
        Location Bend   = new Location("B", 500, 430, (float)(Math.PI * 2 + Math.PI / 2));
        Route route2 = new Route(Arrays.asList(
            Bstart,
            new Location("C", 150, 250, (float)Math.PI),
            new Location("D", 500, 70,  (float)(3 * Math.PI / 2)),
            new Location("A", 850, 250, (float)(2 * Math.PI)),
            Bend
        ));

        // --- Car #3 route: C->D->A(2π)->B(2π+π/2)->C(3π)
        Location Cstart = new Location("C", 150, 250, (float)Math.PI);
        Location Cend   = new Location("C", 150, 250, (float)(Math.PI * 3));
        Route route3 = new Route(Arrays.asList(
            Cstart,
            new Location("D", 500, 70,  (float)(3 * Math.PI / 2)),
            new Location("A", 850, 250, (float)(2 * Math.PI)),
            new Location("B", 500, 430, (float)(2 * Math.PI + Math.PI / 2)),
            Cend
        ));

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

        // Assign total laps (multi-lap logic).
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
