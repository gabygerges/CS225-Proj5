import java.util.List;

public class Car {
    private final int id;
    private final Engine engine;
    private final List<Wheel> wheels;
    private final Route route;
    private float currentSpeed;
    private float totalTime;
    private boolean isFinished;
    
    // For movement interpolation along the oval.
    private Location startLocation;
    private Location endLocation;
    private float travelProgress;  // fraction in [0..1] for the current segment
    private static final float UPDATE_INTERVAL = 0.1f;
    private static final float MOVEMENT_FACTOR = 5.0f;
    private static final int PIT_STOP_DURATION = 20;

    // Current position for rendering.
    private float currentX;
    private float currentY;
    private float currentAngle;  // in radians

    // Pit-stop handling.
    private int pitStopTimer = 0;

    // Lateral offset for “lane” movement within the track width.
    private float lateralOffset = 0f;
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
        
        // The maximum offset so the car remains on the asphalt.
        this.maxLateralOffset = RaceDisplay.TRACK_HALF_WIDTH - (RaceDisplay.CAR_WIDTH / 2f);
        
        // Initialize from the route’s first checkpoint.
        this.startLocation = route.getLocations().get(0);
        this.endLocation = route.getNextLocation();
        this.travelProgress = 0f;
        
        this.currentX = startLocation.getX();
        this.currentY = startLocation.getY();
        this.currentAngle = startLocation.getAngle();
    }

    // Basic getters
    public int getId() { return id; }
    public Engine getEngine() { return engine; }
    public Route getRoute() { return route; }
    public float getTotalTime() { return totalTime; }
    public boolean isFinished() { return isFinished; }
    public float getCurrentSpeed() { return currentSpeed; }
    public float getCurrentX() { return currentX; }
    public float getCurrentY() { return currentY; }
    public float getCurrentAngle() { return currentAngle; }

    // Multi-lap getters/setters
    public int getCurrentLap() { return currentLap; }
    public int getTotalLaps() { return totalLaps; }
    public void setTotalLaps(int laps) { this.totalLaps = laps; }
    public float getBestLapTime() { return bestLapTime; }

    // Called by Race when it starts
    public void start() {
        // Begin at max speed
        updateSpeedToMax();
        RaceDisplay.log("Car " + id + " started.");
    }

    /**
     * Sets current speed to the engine's maximum speed.
     * This is called at:
     *  - Race start
     *  - After pit stop is finished
     *  - Passing a checkpoint (except on the final lap finish)
     */
    private void updateSpeedToMax() {
        currentSpeed = engine.getMaxSpeed();
    }

    // Lateral offset controls
    public void adjustLateralOffset(float delta) {
        lateralOffset += delta;
        if (lateralOffset > maxLateralOffset) {
            lateralOffset = maxLateralOffset;
        }
        if (lateralOffset < -maxLateralOffset) {
            lateralOffset = -maxLateralOffset;
        }
    }

    // Called each update cycle
    public void move() {
        if (isFinished) return;
        
        // Pit-stop check
        if (pitStopTimer > 0) {
            // While in pit, speed is forced to 0
            setCurrentSpeed(0f);
            pitStopTimer--;
            if (pitStopTimer == 0) {
                RaceDisplay.log("Car " + id + " is fixed and back in the race.");
                updateSpeedToMax();  // restore to full speed after pit
            }
            return;
        }
        
        // If speed is extremely low => pit
        if (currentSpeed < 0.1f) {
            pitStopTimer = PIT_STOP_DURATION;
            RaceDisplay.log("Car " + id + " has slowed too much and is going to the pits.");
            return;
        }
        
        // Simulate wheel wear
        for (Wheel wheel : wheels) {
            wheel.wearDown();
        }
        
        // Accumulate total race time
        totalTime += UPDATE_INTERVAL;
        
        // AI cars (id != 1) do small lateral nudges (not speed changes)
        if (id != 1) {
            float randomDelta = Randomizer.generateRandomLateralDelta();
            adjustLateralOffset(randomDelta);
        }
        
        // Update position with potential sub-steps
        updatePosition();
    }

    // The core movement & multi-lap logic
    private void updatePosition() {
        if (isFinished) return;

        float centerX = RaceDisplay.TRACK_CENTER_X;
        float centerY = RaceDisplay.TRACK_CENTER_Y;
        float a = RaceDisplay.TRACK_A;
        float b = RaceDisplay.TRACK_B;

        // Current segment angles
        float startAngle = startLocation.getAngle();
        float endAngle = (endLocation != null) ? endLocation.getAngle() : startAngle;
        
        // Ensure a positive segment angle
        float segmentAngle = endAngle - startAngle;
        if (segmentAngle < 0) {
            segmentAngle += 2f * (float)Math.PI;
        }

        // Approximate radius as average of a & b
        float effectiveRadius = 0.5f * (a + b);
        float arcLength = segmentAngle * effectiveRadius;
        if (arcLength < 0.0001f) {
            arcLength = 0.0001f;
        }

        // fraction of the current segment to travel in this update
        float distanceIncrement = currentSpeed * UPDATE_INTERVAL * MOVEMENT_FACTOR;
        float rawIncrement = distanceIncrement / arcLength;
        
        float remaining = rawIncrement;
        
        // Break it into sub-steps in case we cross multiple checkpoints in one update
        while (remaining > 0f && !isFinished) {
            float spaceLeft = 1f - travelProgress;
            float step = Math.min(spaceLeft, remaining);
            travelProgress += step;
            remaining -= step;

            // If we just reached or passed this checkpoint boundary
            if (travelProgress >= 1.0f) {
                // leftover fraction
                travelProgress -= 1.0f;
                // move to next checkpoint index
                route.advance();

                // If we just finished the route => we completed a lap
                if (route.isComplete()) {
                    float lapTime = totalTime - lapStartTime;
                    if (lapTime < bestLapTime) {
                        bestLapTime = lapTime;
                    }
                    lapStartTime = totalTime;

                    if (currentLap < totalLaps) {
                        // Start the next lap
                        currentLap++;
                        
                        // EXACT final checkpoint coords & angle
                        float finalX = (endLocation != null) ? endLocation.getX() : currentX;
                        float finalY = (endLocation != null) ? endLocation.getY() : currentY;
                        float finalAngle = (endLocation != null) ? endLocation.getAngle() : currentAngle;
                        
                        // Reset the route for the new lap
                        route.reset();
                        travelProgress = 0f;

                        // Overwrite route's first checkpoint so we truly start next lap
                        Location routeFirst = route.getLocations().get(0);
                        Location customStart = new Location(
                            routeFirst.getName(),
                            finalX,
                            finalY,
                            finalAngle
                        );
                        this.startLocation = customStart;
                        this.endLocation = route.getNextLocation();
                        
                        RaceDisplay.log("Car " + id + " begins Lap " 
                                        + currentLap + " of " + totalLaps + ".");
                        
                        // Stop leftover movement to avoid jumps
                        break;
                    } else {
                        // Completed all laps => finalize
                        travelProgress = 1f; // ensures final interpolation is at end
                        isFinished = true;
                        RaceDisplay.log("Car " + id + " has FINISHED the race!");
                        break;
                    }
                } else {
                    // Crossed an intermediate checkpoint => proceed
                    startLocation = endLocation;
                    endLocation = route.getNextLocation();

                    // Restore speed to max whenever crossing a checkpoint
                    updateSpeedToMax();

                    // Recompute for next segment
                    startAngle = startLocation.getAngle();
                    endAngle = (endLocation != null) ? endLocation.getAngle() : startAngle;
                    segmentAngle = endAngle - startAngle;
                    if (segmentAngle < 0) {
                        segmentAngle += 2f * (float)Math.PI;
                    }
                    arcLength = segmentAngle * effectiveRadius;
                    if (arcLength < 0.0001f) {
                        arcLength = 0.0001f;
                    }
                }
            }
        }

        // Finally, interpolate angle for rendering
        float sAngle = startLocation.getAngle();
        float eAngle = (endLocation != null) ? endLocation.getAngle() : sAngle;
        float segAngle = eAngle - sAngle;
        if (segAngle < 0) {
            segAngle += 2f * (float)Math.PI;
        }
        currentAngle = sAngle + travelProgress * segAngle;
        currentAngle = normalizeAngle(currentAngle);

        // Compute base ellipse position
        float baseX = centerX + a * (float)Math.cos(currentAngle);
        float baseY = centerY + b * (float)Math.sin(currentAngle);

        // Tangent & normal for lateral offset
        double tx = -a * Math.sin(currentAngle);
        double ty = b * Math.cos(currentAngle);
        double norm = Math.sqrt(tx * tx + ty * ty);
        if (norm < 0.0001f) {
            norm = 0.0001f;
        }
        double unitTx = tx / norm;
        double unitTy = ty / norm;
        double rightN_x = unitTy;
        double rightN_y = -unitTx;
        
        // Apply lateral offset
        currentX = baseX + lateralOffset * (float)rightN_x;
        currentY = baseY + lateralOffset * (float)rightN_y;
    }

    // Resets everything for a new race
    public void reset() {
        totalTime = 0f;
        isFinished = false;
        pitStopTimer = 0;
        route.reset();
        travelProgress = 0f;
        lateralOffset = 0f;

        currentLap = 1;
        bestLapTime = Float.MAX_VALUE;
        lapStartTime = 0f;
        
        // Re-init from the route’s first checkpoint
        startLocation = route.getLocations().get(0);
        endLocation = route.getNextLocation();
        currentX = startLocation.getX();
        currentY = startLocation.getY();
        currentAngle = startLocation.getAngle();
        
        RaceDisplay.log("Car " + id + " has been reset.");
    }

    // Disallow negative speed
    public void setCurrentSpeed(float newSpeed) {
        currentSpeed = Math.max(newSpeed, 0);
    }

    // Keep angle in [0..2π)
    private float normalizeAngle(float angle) {
        while (angle < 0f) {
            angle += 2f * (float)Math.PI;
        }
        while (angle >= 2f * (float)Math.PI) {
            angle -= 2f * (float)Math.PI;
        }
        return angle;
    }
}
