import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GUI {
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