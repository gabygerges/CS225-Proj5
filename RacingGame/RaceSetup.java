/**
 * The RaceSetup class initializes a new race scenario for the Car Racing Game.
 * It prompts the user to choose the number of laps, defines hardcoded
 * routes for three cars (each with a unique path around the track), creates engines and wheels,
 * and attaches these components to the cars. All setup data is passed into a new Race object.
 *
 * Responsibilities:
 *   Prompt user for lap configuration
 *   Create routes using Location and Route
 *   Assign engines and wheels to each car
 *   Register cars with the race
 *
 *   Developed by: Abraham Arocha, Gabi Gerges
 */

import javax.swing.JOptionPane;
import java.util.Arrays;
import java.util.List;

public class RaceSetup {
    /**
     * Initializes the race with user-defined lap count and three predefined cars/routes.
     * @return a fully configured {@link Race} instance ready to begin
     */
    public static Race initializeRace() {
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

        // --- Car #1 route: A->B->C->D->A(2π)
        Location A0 = new Location("A", 850, 250, 0f);
        Location B  = new Location("B", 500, 430, (float)(Math.PI / 2));
        Location C  = new Location("C", 150, 250, (float)Math.PI);
        Location D  = new Location("D", 500, 70,  (float)(3 * Math.PI / 2));
        Location A2 = new Location("A", 850, 250, (float)(2 * Math.PI));
        Route route1 = new Route(Arrays.asList(A0, B, C, D, A2));

        // --- Car #2 route: B->C->D->A(2π)->B(2π+π/2)
        Location Bstart = new Location("B", 500, 430, (float)(Math.PI / 2));
        Location Bend   = new Location("B", 500, 430, (float)(2 * Math.PI + Math.PI / 2));
        Route route2 = new Route(Arrays.asList(
                Bstart,
                new Location("C", 150, 250, (float)Math.PI),
                new Location("D", 500, 70,  (float)(3 * Math.PI / 2)),
                new Location("A", 850, 250, (float)(2 * Math.PI)),
                Bend
        ));

        // --- Car #3 route: C->D->A(2π)->B(2π+π/2)->C(3π)
        Location Cstart = new Location("C", 150, 250, (float)Math.PI);
        Location Cend   = new Location("C", 150, 250, (float)(3 * Math.PI));
        Route route3 = new Route(Arrays.asList(
                Cstart,
                new Location("D", 500, 70,  (float)(3 * Math.PI / 2)),
                new Location("A", 850, 250, (float)(2 * Math.PI)),
                new Location("B", 500, 430, (float)(2 * Math.PI + Math.PI / 2)),
                Cend
        ));

        // --- Car #4 route: D->A(2π)->B(2π+π/2)->C(3π)->D(4π-π/2)
        Location Dstart = new Location("D", 500, 70, (float)(3 * Math.PI / 2));
        Location Dend   = new Location("D", 500, 70, (float)(4 * Math.PI - Math.PI / 2));
        Route route4 = new Route(Arrays.asList(
                Dstart,
                new Location("A", 850, 250, (float)(2 * Math.PI)),
                new Location("B", 500, 430, (float)(2 * Math.PI + Math.PI / 2)),
                new Location("C", 150, 250, (float)(3 * Math.PI)),
                Dend
        ));


        // Create engines.
        Engine engine1 = new Engine(200, 10);
        Engine engine2 = new Engine(220, 12);
        Engine engine3 = new Engine(210, 11);
        Engine engine4 = new Engine(230, 13);

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
        List<Wheel> wheels4 = Arrays.asList(
                new Wheel(1.0f, 1.0f),
                new Wheel(1.0f, 1.0f),
                new Wheel(1.0f, 1.0f),
                new Wheel(1.0f, 1.0f)
        );

        // Create three cars, each assigned a route.
        Car car1 = new Car(1, engine1, wheels1, route1);
        Car car2 = new Car(2, engine2, wheels2, route2);
        Car car3 = new Car(3, engine3, wheels3, route3);
        Car car4 = new Car(4, engine4, wheels4, route4);

        // Assign total laps (multi-lap logic).
        car1.setTotalLaps(laps);
        car2.setTotalLaps(laps);
        car3.setTotalLaps(laps);
        car4.setTotalLaps(laps);

        // Add cars to the race.
        race.addCar(car1);
        race.addCar(car2);
        race.addCar(car3);
        race.addCar(car4);

        return race;
    }
}
