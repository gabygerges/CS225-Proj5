import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Set;

public class RaceDisplay extends JPanel {
    // Track parameters (bigger ellipse with 40px thickness).
    public static final int TRACK_CENTER_X = 500;
    public static final int TRACK_CENTER_Y = 250;
    public static final int TRACK_A = 350;
    public static final int TRACK_B = 180;
    public static final int TRACK_HALF_WIDTH = 40;

    // Car dimensions (smaller rectangle).
    public static final int CAR_WIDTH = 8;
    public static final int CAR_HEIGHT = 18;

    private final Race race;

    // UI components.
    private JPanel racePanel;
    private JPanel controlPanel;
    private JTextArea narrator;
    private JTable scoreboard;
    private DefaultTableModel tableModel;
    private static RaceDisplay instance;
    private Timer raceTimer;

    public RaceDisplay(Race race) {
        instance = this;
        this.race = race;
        setLayout(new BorderLayout());

        // --- Top Control Buttons ---
        controlPanel = new JPanel();
        JButton startBtn = new JButton("Start");
        JButton pauseBtn = new JButton("Pause");
        JButton resetBtn = new JButton("Reset");

        startBtn.addActionListener(e -> {
            race.startRace();
            raceTimer.start();
        });
        pauseBtn.addActionListener(e -> {
            race.pauseRace();
            raceTimer.stop();
        });
        resetBtn.addActionListener(e -> {
            race.resetRace();
            updateScoreboard();
        });
        controlPanel.add(startBtn);
        controlPanel.add(pauseBtn);
        controlPanel.add(resetBtn);
        add(controlPanel, BorderLayout.NORTH);

        // --- Race Panel (draws the oval track, cars, obstacles) ---
        racePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRace(g);
            }
        };
        racePanel.setPreferredSize(new Dimension(1000, 600));
        racePanel.setBackground(Color.GREEN);

        // --- Scoreboard on the right ---
        String[] columns = {"Car ID", "Route", "Lap", "Best Lap", "Current Speed", "Total Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        scoreboard = new JTable(tableModel);
        JScrollPane scoreScrollPane = new JScrollPane(scoreboard);
        scoreScrollPane.setPreferredSize(new Dimension(340, 600));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, racePanel, scoreScrollPane);
        add(splitPane, BorderLayout.CENTER);

        // --- Narrator Log along the bottom ---
        narrator = new JTextArea(6, 40);
        narrator.setEditable(false);
        JScrollPane narratorScrollPane = new JScrollPane(narrator);
        add(narratorScrollPane, BorderLayout.SOUTH);

        // --- Timer updates the race every 0.1s ---
        raceTimer = new Timer(100, e -> {
            race.updateRaceStatus();
            updateScoreboard();
            racePanel.repaint();
        });

        // --- Key Bindings for Car #1 movement ---
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");

        am.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Car userCar = race.getUserCar();
                if (userCar != null) {
                    userCar.adjustLateralOffset(-5f);
                }
            }
        });
        am.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Car userCar = race.getUserCar();
                if (userCar != null) {
                    userCar.adjustLateralOffset(5f);
                }
            }
        });
    }

    // Logging helper for the narrator text area.
    public static void log(String message) {
        if (instance != null && instance.narrator != null) {
            instance.narrator.append(message + "\n");
            instance.narrator.setCaretPosition(instance.narrator.getDocument().getLength());
        }
    }

    // Main rendering routine for the race panel.
    private void drawRace(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = racePanel.getWidth();
        int height = racePanel.getHeight();

        // Grass background with vertical gradient.
        GradientPaint grass = new GradientPaint(
                0, 0, new Color(34, 139, 34),
                0, height, new Color(0, 100, 0)
        );
        g2.setPaint(grass);
        g2.fillRect(0, 0, width, height);

        // Draw the asphalt track ring.
        drawTrack(g2);

        // Draw checkpoint lines, obstacles, and cars.
        drawStops(g2);
        drawObstacles(g2);
        drawCars(g2);
    }

    // Draws the oval track ring (outer boundary + inner boundary).
    private void drawTrack(Graphics2D g2) {
        int centerX = TRACK_CENTER_X;
        int centerY = TRACK_CENTER_Y;
        int a = TRACK_A;
        int b = TRACK_B;
        int halfWidth = TRACK_HALF_WIDTH;

        // Outer ellipse bounding box:
        int outerA = a + halfWidth;
        int outerB = b + halfWidth;
        int outerX = centerX - outerA;
        int outerY = centerY - outerB;
        int outerW = 2 * outerA;
        int outerH = 2 * outerB;

        // Inner ellipse bounding box:
        int innerA = a - halfWidth;
        int innerB = b - halfWidth;
        int innerX = centerX - innerA;
        int innerY = centerY - innerB;
        int innerW = 2 * innerA;
        int innerH = 2 * innerB;

        // Asphalt color fill.
        g2.setColor(new Color(80, 80, 80));
        g2.fillOval(outerX, outerY, outerW, outerH);

        // Apply grass gradient inside to create a ring.
        GradientPaint grass = new GradientPaint(
                0, 0, new Color(34, 139, 34),
                0, getHeight(), new Color(0, 100, 0)
        );
        g2.setPaint(grass);
        g2.fillOval(innerX, innerY, innerW, innerH);

        // White outline for outer & inner boundaries.
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(outerX, outerY, outerW, outerH);
        g2.drawOval(innerX, innerY, innerW, innerH);
    }

    // Draws checkpoint lines across the track.
    private void drawStops(Graphics2D g2) {
        int halfWidth = TRACK_HALF_WIDTH;
        int centerX = TRACK_CENTER_X;
        int centerY = TRACK_CENTER_Y;
        int a = TRACK_A;
        int b = TRACK_B;

        // Gather all route locations (checkpoints) from each car's route.
        Set<Location> stops = new HashSet<>();
        for (Car car : race.getCars()) {
            stops.addAll(car.getRoute().getLocations());
        }

        g2.setStroke(new BasicStroke(3));
        for (Location loc : stops) {
            float theta = loc.getAngle();

            float midX = centerX + a * (float)Math.cos(theta);
            float midY = centerY + b * (float)Math.sin(theta);

            double tx = -a * Math.sin(theta);
            double ty = b * Math.cos(theta);
            double norm = Math.sqrt(tx * tx + ty * ty);
            if (norm < 0.0001) {
                norm = 0.0001;
            }
            double unitTx = tx / norm;
            double unitTy = ty / norm;
            double normalX = unitTy;
            double normalY = -unitTx;

            int x1 = (int) (midX + halfWidth * normalX);
            int y1 = (int) (midY + halfWidth * normalY);
            int x2 = (int) (midX - halfWidth * normalX);
            int y2 = (int) (midY - halfWidth * normalY);

            // Yellow line for the checkpoint.
            g2.setColor(Color.YELLOW);
            g2.drawLine(x1, y1, x2, y2);

            // Label in white near midpoint.
            g2.setColor(Color.WHITE);
            g2.drawString(loc.getName(), (x1 + x2)/2, (y1 + y2)/2);
        }
    }

    // Draws obstacles (with partial transparency).
    private void drawObstacles(Graphics2D g2) {
        for (Obstacle obs : race.getObstacles()) {
            int alpha = Math.min(255, obs.getLifetime() * 5);
            Color obsColor = new Color(
                    obs.getColor().getRed(),
                    obs.getColor().getGreen(),
                    obs.getColor().getBlue(),
                    alpha
            );
            g2.setColor(obsColor);

            int obsSize = 15;
            int x = (int)(obs.getX() - obsSize / 2);
            int y = (int)(obs.getY() - obsSize / 2);

            switch (obs.getShapeType()) {
                case "circle":
                    g2.fillOval(x, y, obsSize, obsSize);
                    break;
                case "square":
                    g2.fillRect(x, y, obsSize, obsSize);
                    break;
                case "triangle":
                    int[] xs = {x + obsSize/2, x, x + obsSize};
                    int[] ys = {y, y + obsSize, y + obsSize};
                    g2.fillPolygon(xs, ys, 3);
                    break;
                default:
                    g2.fillOval(x, y, obsSize, obsSize);
                    break;
            }
        }
    }

    // Draws each car as a small rectangle with a little shadow.
    private void drawCars(Graphics2D g2) {
        for (Car car : race.getCars()) {
            AffineTransform original = g2.getTransform();

            // Draw shadow offset.
            int shadowOffset = 4;
            g2.translate(car.getCurrentX() + CAR_WIDTH / 2.0,
                    car.getCurrentY() + CAR_HEIGHT / 2.0);
            g2.rotate(car.getCurrentAngle());
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(shadowOffset - CAR_WIDTH / 2,
                    shadowOffset - CAR_HEIGHT / 2,
                    CAR_WIDTH, CAR_HEIGHT);
            g2.setTransform(original);

            // Draw the actual car.
            original = g2.getTransform();
            g2.translate(car.getCurrentX() + CAR_WIDTH / 2.0,
                    car.getCurrentY() + CAR_HEIGHT / 2.0);
            g2.rotate(car.getCurrentAngle());

            g2.setColor(getColorForCar(car.getId()));
            g2.fillRect(-CAR_WIDTH / 2, -CAR_HEIGHT / 2, CAR_WIDTH, CAR_HEIGHT);

            // Car ID text in white.
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.drawString("Car " + car.getId(), -CAR_WIDTH / 2, 0);

            g2.setTransform(original);
        }
    }

    // Distinct color per car ID.
    private Color getColorForCar(int id) {
        switch (id) {
            case 1: return Color.RED;
            case 2: return Color.ORANGE;
            case 3: return Color.CYAN;
            default: return Color.MAGENTA;
        }
    }

    // Populates the scoreboard table with each car’s info.
    private void updateScoreboard() {
        tableModel.setRowCount(0);
        for (Car car : race.getCars()) {
            String routeStr = car.getRoute().getRouteNames();
            String lapStr = car.getCurrentLap() + "/" + car.getTotalLaps();
            float best = car.getBestLapTime();
            String bestLap = (best == Float.MAX_VALUE) ? "-" : String.format("%.2f", best);
            String speedStr = String.format("%.2f", car.getCurrentSpeed());
            String timeStr = String.format("%.2f", car.getTotalTime());
            String status = car.isFinished()
                    ? "Finished"
                    : (car.getCurrentSpeed() < 0.1f ? "Pitting" : "Racing");

            Object[] row = {
                    car.getId(),
                    routeStr,
                    lapStr,
                    bestLap,
                    speedStr,
                    timeStr,
                    status
            };
            tableModel.addRow(row);
        }
    }

    // Called by Race to announce a winner in a popup.
    public void announceWinner(Car winner) {
        JOptionPane.showMessageDialog(this,
                "Winner is Car " + winner.getId()
                        + " with time " + String.format("%.2f", winner.getTotalTime())
                        + " seconds!");
    }
}