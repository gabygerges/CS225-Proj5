import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Responsible for launching the graphical user interface (GUI)
 * of the Oval Car Racing Simulator.
 * This class creates a JFrame and embeds the {@link RaceDisplay}
 * component, which renders the ongoing race simulation.
 *
 * Developed by: Lucas Martins
 */

public class GUI {
    /**
     * Launches the GUI window and starts the race display.
     * This method ensures that the UI is created on the Event Dispatch Thread
     *
     * @param race the {@link Race} object that holds all race logic and data
     */
    public static void launchGUI(Race race) {
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
