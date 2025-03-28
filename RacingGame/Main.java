/**
 * The Main class serves as the entry point for the Formula 1-themed racing game.
 * It initializes the race setup and launches the graphical user interface (GUI).
 *
 * Developed by: Lucas Martins
 */

public class Main {
    /**
     * The main method of the program.
     *
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        Race race = RaceSetup.initializeRace();
        GUI.launchGUI(race);
    }
}