import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public Race() {
        cars = new ArrayList<>();
        obstacles = new ArrayList<>();
        isRunning = false;
    }

    public void setRaceDisplay(RaceDisplay display) {
        this.raceDisplay = display;
    }

    public List<Car> getCars() {
        return cars;
    }
    
    public List<Obstacle> getObstacles() {
        return obstacles;
    }
    
    public Car getUserCar() {
        // The user-controlled car is ID=1.
        for (Car car : cars) {
            if (car.getId() == 1) {
                return car;
            }
        }
        return null;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getTotalLaps() {
        return totalLaps;
    }

    public void setTotalLaps(int totalLaps) {
        this.totalLaps = Math.max(1, totalLaps);
    }
    
    // Starts the race.
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
    
    public void pauseRace() {
        if (isRunning) {
            isRunning = false;
            RaceDisplay.log("Race is paused.");
        }
    }

    // Called periodically (by a Swing Timer) to update the race.
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

    // Logs final results and finds the winner.
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

    // Resets everything (cars, obstacles, times).
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
