import java.util.List;

public class Car {
    private final int id;
    private final Engine engine;
    private final List<Wheel> wheels;
    private final Route route;
    private float currentSpeed;
    private float totalTime;
    private boolean isFinished;

    // For oval movement (angular interpolation).
    private Location startLocation;
    private Location endLocation;
    private float travelProgress;            // fraction from 0 to 1 of the current segment
    private static final float UPDATE_INTERVAL = 0.1f;
    private static final float MOVEMENT_FACTOR = 5.0f;
    private static final int PIT_STOP_DURATION = 20;

    // Current (x,y) coordinates for drawing.
    private float currentX;
    private float currentY;

    // Current angle (in radians) along the track for rotation.
    private float currentAngle;

    // Pit stop timer.
    private int pitStopTimer = 0;

    // Lateral offset (in pixels) for left/right movement within the track.
    private float lateralOffset = 0f;
    // The maximum offset so the car stays on the asphalt.
    private final float maxLateralOffset;

    // Multi-lap logic.
    private int totalLaps = 1;
    private int currentLap = 1;
    private float bestLapTime = Float.MAX_VALUE;
    private float lapStartTime = 0f;

    public Car(int id, Engine engine, List<Wheel> wheels, Route route) {
        this.id = id;
        this.engine = engine;
        this.wheels = wheels;
        this.route = route;
        this.totalTime = 0f;
        this.isFinished = false;

        // Compute max lateral offset from track thickness minus half the car’s width.
        this.maxLateralOffset = RaceDisplay.TRACK_HALF_WIDTH - (RaceDisplay.CAR_WIDTH / 2f);

        // Initialize from the first checkpoint.
        this.startLocation = route.getLocations().get(0);
        this.endLocation = route.getNextLocation();
        this.travelProgress = 0f;

        this.currentX = startLocation.getX();
        this.currentY = startLocation.getY();
        this.currentAngle = startLocation.getAngle();
    }

    public int getId() {
        return id;
    }

    public Engine getEngine() {
        return engine;
    }

    public Route getRoute() {
        return route;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    // Multi-lap getters/setters.
    public int getCurrentLap() {
        return currentLap;
    }

    public int getTotalLaps() {
        return totalLaps;
    }

    public void setTotalLaps(int laps) {
        this.totalLaps = laps;
    }

    public float getBestLapTime() {
        return bestLapTime;
    }

    public void start() {
        updateSpeed();
        RaceDisplay.log("Car " + id + " started.");
    }

    // Sets speed randomly between ~50% and 100% of max.
    public void updateSpeed() {
        float randomSpeed = Randomizer.generateRandomSpeed(engine.getMaxSpeed());
        currentSpeed = Math.min(randomSpeed, engine.getMaxSpeed());
    }

    // Adjusts the lateral offset, clamping within ±maxLateralOffset.
    public void adjustLateralOffset(float delta) {
        lateralOffset += delta;
        if (lateralOffset > maxLateralOffset) {
            lateralOffset = maxLateralOffset;
        }
        if (lateralOffset < -maxLateralOffset) {
            lateralOffset = -maxLateralOffset;
        }
    }

    // Main movement method called each update cycle.
    public void move() {
        if (isFinished) {
            return;
        }

        // Handle pit stop.
        if (pitStopTimer > 0) {
            pitStopTimer--;
            if (pitStopTimer == 0) {
                RaceDisplay.log("Car " + id + " is fixed and back in the race.");
                updateSpeed();
            }
            return;
        }

        // If speed is super low, we pit.
        if (currentSpeed < 0.1f) {
            pitStopTimer = PIT_STOP_DURATION;
            RaceDisplay.log("Car " + id + " has slowed too much and is going to the pits.");
            return;
        }

        // Simulate wheel wear every update.
        for (Wheel wheel : wheels) {
            wheel.wearDown();
        }

        // Update total time in race.
        totalTime += UPDATE_INTERVAL;

        // AI cars (ID != 1) nudge laterally.
        if (this.id != 1) {
            float randomDelta = Randomizer.generateRandomLateralDelta();
            adjustLateralOffset(randomDelta);
        }

        // Update position on the track (with sub-steps to avoid big teleports).
        updatePosition();
    }

    private void updatePosition() {
        if (isFinished) {
            return;
        }

        float centerX = RaceDisplay.TRACK_CENTER_X;
        float centerY = RaceDisplay.TRACK_CENTER_Y;
        float a = RaceDisplay.TRACK_A;
        float b = RaceDisplay.TRACK_B;

        float startAngle = startLocation.getAngle();
        float endAngle = (endLocation != null) ? endLocation.getAngle() : startAngle;

        // Segment angle, ensure positive.
        float segmentAngle = endAngle - startAngle;
        if (segmentAngle < 0) {
            segmentAngle += 2 * (float) Math.PI;
        }

        float effectiveRadius = (a + b) / 2;
        float arcLength = segmentAngle * effectiveRadius;
        if (arcLength < 0.0001f) {
            arcLength = 0.0001f;
        }
        // The fraction we want to advance on this segment in one update:
        float rawIncrement = (currentSpeed * UPDATE_INTERVAL * MOVEMENT_FACTOR) / arcLength;

        // Break that increment into sub-chunks so we never skip multiple segments in one go.
        float remainingIncrement = rawIncrement;
        while (remainingIncrement > 0f && !isFinished) {
            float spaceLeft = 1.0f - travelProgress;
            float step = Math.min(spaceLeft, remainingIncrement);
            travelProgress += step;
            remainingIncrement -= step;

            // If we just crossed the segment boundary:
            if (travelProgress >= 1.0f && !route.isComplete()) {
                // Adjust leftover fraction.
                travelProgress -= 1.0f;

                route.advance();
                startLocation = endLocation;
                endLocation = route.getNextLocation();

                // Optionally update speed at each checkpoint crossing.
                updateSpeed();

                // Recompute angles for the new segment.
                startAngle = startLocation.getAngle();
                endAngle = (endLocation != null) ? endLocation.getAngle() : startAngle;
                segmentAngle = endAngle - startAngle;
                if (segmentAngle < 0) {
                    segmentAngle += 2 * (float) Math.PI;
                }
                arcLength = segmentAngle * effectiveRadius;
                if (arcLength < 0.0001f) {
                    arcLength = 0.0001f;
                }
            }
            // If we've passed the final checkpoint, we might finish or start a new lap.
            if (route.isComplete() && travelProgress >= 1.0f) {
                float lapDuration = totalTime - lapStartTime;
                if (lapDuration < bestLapTime) {
                    bestLapTime = lapDuration;
                }
                lapStartTime = totalTime;

                if (currentLap < totalLaps) {
                    // Start a new lap.
                    currentLap++;
                    route.reset();
                    travelProgress = 0f;
                    startLocation = route.getLocations().get(0);
                    endLocation = route.getNextLocation();
                    RaceDisplay.log("Car " + id + " begins Lap " + currentLap
                            + " of " + totalLaps + ".");
                } else {
                    // Finished all laps.
                    travelProgress = 1.0f;
                    isFinished = true;
                    RaceDisplay.log("Car " + id + " has FINISHED the race!");
                }
            }
        }

        // Now interpolate final angle based on the updated travelProgress.
        startAngle = startLocation.getAngle();
        endAngle = (endLocation != null) ? endLocation.getAngle() : startAngle;
        segmentAngle = endAngle - startAngle;
        if (segmentAngle < 0) {
            segmentAngle += 2 * (float) Math.PI;
        }
        currentAngle = startAngle + travelProgress * segmentAngle;

        // Compute base ellipse centerline position.
        float baseX = centerX + a * (float)Math.cos(currentAngle);
        float baseY = centerY + b * (float)Math.sin(currentAngle);

        // Compute tangent and right normal for lateral offset.
        double tx = -a * Math.sin(currentAngle);
        double ty = b * Math.cos(currentAngle);
        double norm = Math.sqrt(tx * tx + ty * ty);
        if (norm < 0.0001) {
            norm = 0.0001;
        }
        double unitTx = tx / norm;
        double unitTy = ty / norm;
        double rightNormalX = unitTy;
        double rightNormalY = -unitTx;

        // Apply lateral offset.
        currentX = baseX + lateralOffset * (float)rightNormalX;
        currentY = baseY + lateralOffset * (float)rightNormalY;
    }

    // Resets the car when the race is reset.
    public void reset() {
        totalTime = 0;
        isFinished = false;
        pitStopTimer = 0;
        route.reset();
        startLocation = route.getLocations().get(0);
        endLocation = route.getNextLocation();
        travelProgress = 0;
        lateralOffset = 0f;

        // Reset laps.
        currentLap = 1;
        bestLapTime = Float.MAX_VALUE;
        lapStartTime = 0f;

        // Reposition to the route's first location.
        currentX = startLocation.getX();
        currentY = startLocation.getY();
        currentAngle = startLocation.getAngle();

        RaceDisplay.log("Car " + id + " has been reset.");
    }

    // Disallow negative speed.
    public void setCurrentSpeed(float newSpeed) {
        currentSpeed = Math.max(newSpeed, 0);
    }
}