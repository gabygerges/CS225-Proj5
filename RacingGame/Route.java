/**
 * The Route class represents a sequential path made up of Location checkpoints
 * that a car must follow during a race.
 * It maintains an internal index to track the car's current position on the route and provides
 * methods to retrieve the next location, advance along the path, reset, and describe the full route.
 *
 * Developed by: Abraham Arocha
 */

import java.util.List;

public class Route {
    private final List<Location> locations;
    private int currentIndex;

    /**
     * Constructs a route with the given ordered list of locations.
     * @param locations list of {@link Location} objects defining the route
     */
    public Route(List<Location> locations) {
        this.locations = locations;
        this.currentIndex = 0;
    }

    /**
     * Returns the list of all locations in the route.
     * @return list of {@link Location} objects
     */
    public List<Location> getLocations() {
        return locations;
    }

    /**
     * Retrieves the next location in the route based on the current index.
     * @return the next {@link Location}, or {@code null} if at the end
     */
    public Location getNextLocation() {
        if (currentIndex + 1 < locations.size()) {
            return locations.get(currentIndex + 1);
        }
        return null;
    }

    /**
     * Advances the current checkpoint index to the next location.
     */
    public void advance() {
        currentIndex++;
    }

    /**
     * Checks if the route is complete (i.e., final checkpoint reached or passed).
     * @return true if the route is complete, false otherwise
     */
    public boolean isComplete() {
        return currentIndex >= locations.size() - 1;
    }

    /**
     * Returns a string representation of the route in the format:
     * "A -> B -> C -> D -> A".
     * @return string of route location names in sequence
     */
    public String getRouteNames() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < locations.size(); i++) {
            sb.append(locations.get(i).getName());
            if (i < locations.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    /**
     * Resets the route to the beginning by setting the checkpoint index to zero.
     */
    public void reset() {
        currentIndex = 0;
    }
}
