import java.util.List;

public class Route {
    private final List<Location> locations;
    private int currentIndex;

    public Route(List<Location> locations) {
        this.locations = locations;
        this.currentIndex = 0;
    }

    public List<Location> getLocations() {
        return locations;
    }

    // Returns the next location in the route, if available.
    public Location getNextLocation() {
        if (currentIndex + 1 < locations.size()) {
            return locations.get(currentIndex + 1);
        }
        return null;
    }

    // Advances to the next checkpoint.
    public void advance() {
        currentIndex++;
    }

    // Checks if we have reached the final checkpoint.
    public boolean isComplete() {
        return currentIndex >= locations.size() - 1;
    }

    // Returns route names (e.g., "A -> B -> C -> D -> E").
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

    // Resets to the first checkpoint.
    public void reset() {
        currentIndex = 0;
    }
}
