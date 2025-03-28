/**
 * The Race class manages the core logic of a car racing game.
 *
 * It keeps track of all cars, race state (start, pause, reset), obstacles,
 * and calculates race results. This class also coordinates race timing,
 * collision detection, and communicates updates to the UI via {@link RaceDisplay}.
 *
 * Responsibilities:
 *   Maintains a list of cars and obstacles<
 *   Handles race timing and lap tracking
 *   Spawns and removes obstacles during the race<
 *   Detects collisions and applies obstacle effects<
 *   Calculates and announces the race winner
 *
 *   Developed by: Abraham Arocha
 */

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a car race that manages multiple cars, tracks progress,
 * handles obstacles, and updates the UI through the RaceDisplay.
 */
public class Race {
    private final List<Car> cars;
    private boolean isRunning;
    private LocalTime startTime;
    private LocalTime endTime;
    private RaceDisplay raceDisplay;

    // List to manage obstacles.
    private final List<Obstacle> obstacles;

    // Total laps for the race.
    private int totalLaps = 1;

/** Constructs an empty race. **/
    public Race() {
        cars = new ArrayList<>();
        obstacles = new ArrayList<>();
        isRunning = false;
    }

    /**
     * Associates a RaceDisplay with this race for repainting and logging.
     * @param display the RaceDisplay panel
     */
    public void setRaceDisplay(RaceDisplay display) {
        this.raceDisplay = display;
    }

    /**
     * Returns the list of cars in the race.
     * @return list of Car objects
     */
    public List<Car> getCars() {
        return cars;
    }

    /**
     * Returns the list of active obstacles on the track.
     * @return list of Obstacle objects
     */
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    /**
     * Returns the user-controlled car, assumed to be Car with ID = 1.
     * @return the user's Car instance or null if not found
     */
    public Car getUserCar() {
        // The user-controlled car is ID=1.
        for (Car car : cars) {
            if (car.getId() == 1) {
                return car;
            }
        }
        return null;
    }

    /**
     * Adds a car to the race.
     * @param car the Car to add
     */
    public void addCar(Car car) {
        cars.add(car);
    }

    /**
     * Returns whether the race is currently running.
     * @return true if running, false if paused or not started
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Returns the total number of laps in the race.
     * @return total laps
     */
    public int getTotalLaps() {
        return totalLaps;
    }

    /**
     * Sets the total number of laps (minimum of 1).
     * @param totalLaps number of laps
     */
    public void setTotalLaps(int totalLaps) {
        this.totalLaps = Math.max(1, totalLaps);
    }

    /**
     * Starts the race if it is not already running.
     */
    public void startRace() {
        if (!isRunning) {
            isRunning = true;
            startTime = LocalTime.now();
            RaceDisplay.log("Race has started with " + totalLaps + " laps!");
            for (Car car : cars) {
                car.start();
            }
        }
    }

    /**
     * Pauses the race if it is currently running.
     */
    public void pauseRace() {
        if (isRunning) {
            isRunning = false;
            RaceDisplay.log("Race is paused.");
        }
    }

    /**
     * Updates race state, spawns and processes obstacles, and moves cars.
     * Ends the race when all cars are finished.
     */
    public void updateRaceStatus() {
        if (!isRunning)
            return;

        // 5% chance per update to spawn a new obstacle.
        if (Math.random() < 0.05) {
            obstacles.add(Randomizer.generateRandomObstacle());
        }

        // Update obstacles: degrade lifetime, remove expired.
        for (Iterator<Obstacle> it = obstacles.iterator(); it.hasNext();) {
            Obstacle obs = it.next();
            obs.decreaseLifetime();
            if (obs.getLifetime() <= 0) {
                it.remove();
            }
        }

        // Check collisions with obstacles.
        for (Car car : cars) {
            for (Iterator<Obstacle> it = obstacles.iterator(); it.hasNext();) {
                Obstacle obs = it.next();
                float dx = car.getCurrentX() - obs.getX();
                float dy = car.getCurrentY() - obs.getY();
                float distance = (float) Math.sqrt(dx*dx + dy*dy);
                if (distance < 15) {
                    obs.applyToCar(car);
                    it.remove();
                }
            }
        }

        // Move cars; see if any remains unfinished.
        boolean allFinished = true;
        for (Car car : cars) {
            if (!car.isFinished()) {
                car.move();
                allFinished = false;
            }
        }

        if (raceDisplay != null) {
            raceDisplay.repaint();
        }

        // If all are finished, end the race.
        if (allFinished) {
            isRunning = false;
            endTime = LocalTime.now();
            calculateResults();
        }
    }

    /**
     * Logs race results and identifies the winner.
     */
    public void calculateResults() {
        Duration duration = Duration.between(startTime, endTime);
        RaceDisplay.log("Race Duration: " + duration.getSeconds() + " seconds");

        Car winner = null;
        float bestTime = Float.MAX_VALUE;
        for (Car car : cars) {
            float totalT = car.getTotalTime();
            float bestLap = car.getBestLapTime();
            String bestLapStr = (bestLap == Float.MAX_VALUE) ? "-" : String.format("%.2f", bestLap);

            RaceDisplay.log("Car " + car.getId()
                    + " - Route: " + car.getRoute().getRouteNames()
                    + " - Total Time: " + String.format("%.2f", totalT) + "s"
                    + " - Best Lap: " + bestLapStr + "s");

            if (totalT < bestTime) {
                bestTime = totalT;
                winner = car;
            }
        }
        if (winner != null) {
            RaceDisplay.log("Winner is Car " + winner.getId()
                    + " with total time " + String.format("%.2f", bestTime) + " seconds!");
            if (raceDisplay != null) {
                raceDisplay.announceWinner(winner);
            }
        }
    }

    /**
     * Resets the race state including cars, obstacles, and timers.
     */
    public void resetRace() {
        isRunning = false;
        for (Car car : cars) {
            car.reset();
        }
        obstacles.clear();
        startTime = null;
        endTime = null;
        RaceDisplay.log("Race has been reset.");
        if (raceDisplay != null) {
            raceDisplay.repaint();
        }
    }
}