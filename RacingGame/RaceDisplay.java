/**
 * RaceDisplay is the main graphical panel for the Car Racing Game.
 * It manages the overall game interface, including the start screen,
 * race visuals, scoreboard, control buttons (start/pause/reset).
 * It also handles car input, renders the track and obstacles, and coordinates
 * timing for race updates and countdowns.
 * Developed by Baheeja Muntasser, Abraham Arocha
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Set;

/**
 * RaceDisplay is the main panel for the car racing game.
 * It provides the UI and drawing logic for the race, start screen, scoreboard, and game narration.
 */
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
    private JPanel startPage;
    private JPanel racePanel;
    private JPanel controlPanel;
    private JTextArea narrator;
    private JTable scoreboard;
    private DefaultTableModel tableModel;
    private static RaceDisplay instance;
    private Timer raceTimer;
    private JLabel countdownLabel;

    /**
     * Constructs the RaceDisplay panel and initializes the UI layout and game controls.
     * @param race the Race instance that holds all race data and logic
     */
    public RaceDisplay(Race race) {
        instance = this;
        this.race = race;
        setLayout(new BorderLayout());

        // --- Start Page ---
        startPage = new JPanel();
        startPage.setLayout(new BoxLayout(startPage, BoxLayout.Y_AXIS));
        startPage.setBackground(Color.BLUE);

        //layered panel to have title on checkerboard
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1500, 400));

        CheckerBoard checkerBoard = new CheckerBoard();
        checkerBoard.setBounds(0, 0, 1500, 200);
        layeredPane.add(checkerBoard, Integer.valueOf(0));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBounds(0, 20, 1500, 200);

        //title of game on startpage
        JLabel title = new JLabel("Car Racing Game", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 60));
        title.setForeground(Color.RED);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        //desccription of game on start page
        JLabel description = new JLabel("Compete against other cars and win the race!");
        description.setFont(new Font("Times New Roman", Font.BOLD, 50));
        description.setForeground(Color.WHITE);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        //rules of game on start page
        JLabel rules1 = new JLabel("How to play");
        rules1.setFont(new Font("Times New Roman", Font.BOLD, 40));
        rules1.setForeground(Color.WHITE);
        rules1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rules2 = new JLabel("Use the left and right arrow keys to steer.");
        rules2.setFont(new Font("Times New Roman", Font.BOLD, 30));
        rules2.setForeground(Color.WHITE);
        rules2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rules3 = new JLabel("Avoid the oil spills, potholes, and debris or else you lose speed!");
        rules3.setFont(new Font("Times New Roman", Font.BOLD, 30));
        rules3.setForeground(Color.WHITE);
        rules3.setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel rules4 = new JLabel("Hit the green spots to gain a speed boost!");
        rules4.setFont(new Font("Times New Roman", Font.BOLD, 30));
        rules4.setForeground(Color.WHITE);
        rules4.setAlignmentX(Component.CENTER_ALIGNMENT);


        // play directions on startpage
        JLabel play = new JLabel("Press play to begin the race.");
        play.setFont(new Font("Times New Roman", Font.BOLD, 20));
        play.setForeground(Color.WHITE);
        play.setAlignmentX(Component.CENTER_ALIGNMENT);

        //play button
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 30));
        playButton.setBackground(Color.GREEN);
        playButton.setForeground(Color.BLACK);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(e -> {
            remove(startPage);
            add(getRacePanel(), BorderLayout.CENTER);
            add(getControlPanel(), BorderLayout.NORTH);
            add(getScoreboardPanel(), BorderLayout.EAST);
            add(getNarratorPanel(), BorderLayout.SOUTH);
            revalidate();
            repaint();
        });



        startPage.add(layeredPane);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 40)));
        titlePanel.add(title);
        layeredPane.add(titlePanel, Integer.valueOf(1));
        startPage.add(Box.createRigidArea(new Dimension(0, 100)));
        startPage.add(description);
        startPage.add(Box.createRigidArea(new Dimension(0, 100)));
        startPage.add(rules1);
        startPage.add(Box.createRigidArea(new Dimension(0, 10)));
        startPage.add(rules2);
        startPage.add(rules3);
        startPage.add(rules4);
        startPage.add(Box.createRigidArea(new Dimension(0, 120)));
        startPage.add(play);
        startPage.add(playButton);
        startPage.add(Box.createRigidArea(new Dimension(0, 53)));


        add(startPage, BorderLayout.CENTER);

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

    // --- Getters for panels ---

    /**
     * Returns the race panel which displays the race.
     * @return JPanel containing the race visuals
     */
    private JPanel getRacePanel() {
        if (racePanel == null) {
            racePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    drawRace(g);
                }
            };
            racePanel.setPreferredSize(new Dimension(1370, 1000));
            racePanel.setBackground(Color.BLUE);
        }
        return racePanel;
    }

    /**
     * Returns the control panel containing Start, Pause, and Reset buttons.
     * @return JPanel with control buttons
     */
    private JPanel getControlPanel() {
        if (controlPanel == null) {
            controlPanel = new JPanel();
            controlPanel.setBackground(Color.DARK_GRAY);
            controlPanel.setLayout(new FlowLayout());

            JButton startBtn = new JButton("Start");
            startBtn.setFont(new Font("Arial", Font.BOLD, 14));
            startBtn.setBackground(Color.GREEN);
            startBtn.setForeground(Color.BLACK);

            JButton pauseBtn = new JButton("Pause");
            pauseBtn.setFont(new Font("Arial", Font.BOLD, 14));
            pauseBtn.setBackground(Color.YELLOW);
            pauseBtn.setForeground(Color.BLACK);

            JButton resetBtn = new JButton("Reset");
            resetBtn.setFont(new Font("Arial", Font.BOLD, 14));
            resetBtn.setBackground(Color.RED);
            resetBtn.setForeground(Color.BLACK);

            startBtn.addActionListener(e -> {
                startCountdown();
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
        }
        return controlPanel;
    }

    /**
     * Returns a scrollable panel with the race scoreboard.
     * @return JScrollPane for scoreboard table
     */
    private JScrollPane getScoreboardPanel() {
        if (scoreboard == null) {
            String[] columns = {"Car ID", "Route", "Lap", "Speed", "Total Time", "Status"};
            tableModel = new DefaultTableModel(columns, 0);
            scoreboard = new JTable(tableModel);
            scoreboard.setFont(new Font("Arial", Font.BOLD, 12));
            scoreboard.setBackground(Color.LIGHT_GRAY);
            scoreboard.setForeground(Color.BLACK);
            return new JScrollPane(scoreboard);
        }
        return new JScrollPane(scoreboard);
    }

    /**
     * Returns the narrator panel used to display race messages.
     * @return JScrollPane for narrator text area
     */
    private JScrollPane getNarratorPanel() {
        if (narrator == null) {
            narrator = new JTextArea(6, 40);
            narrator.setFont(new Font("Arial", Font.BOLD, 12));
            narrator.setEditable(false);
            narrator.setBackground(Color.DARK_GRAY);
            narrator.setForeground(Color.WHITE);
            return new JScrollPane(narrator);
        }
        return new JScrollPane(narrator);
    }

    /**
     * Inner panel class that draws a checkerboard background for the start screen.
     */
    private class CheckerBoard extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int squareSize = 60;
            for (int row = 0; row < getHeight() / squareSize; row++) {
                for (int col = 0; col < getWidth() / squareSize; col++) {
                    if ((row + col) % 2 == 0) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    g.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
                }
            }
        }
    }


    /**
     * Starts a 3-second countdown before initiating the race.
     */
    private void startCountdown() {
        if (countdownLabel == null) {
            countdownLabel = new JLabel("4", SwingConstants.CENTER);
            countdownLabel.setFont(new Font("Arial", Font.BOLD, 48));
            countdownLabel.setForeground(Color.BLACK);
            countdownLabel.setBackground(Color.RED);
            countdownLabel.setOpaque(true);
            //countdownLabel.setBounds(450, 250, 500, 250);
            getRacePanel().add(countdownLabel);
            getRacePanel().repaint();
        }

        Timer countdownTimer = new Timer(1000, e -> {
            int count = Integer.parseInt(countdownLabel.getText());
            if (count > 1) {
                countdownLabel.setText(String.valueOf(count - 1));
            } else {
                ((Timer) e.getSource()).stop();
                countdownLabel.setVisible(false);
                getRacePanel().repaint();
                startRace();
            }
        });
        countdownTimer.start();
    }

    /**
     * Starts the actual race and schedules periodic UI updates.
     */
    private void startRace() {
        race.startRace();
        raceTimer = new Timer(100, e -> {
            race.updateRaceStatus();
            updateScoreboard();
            getRacePanel().repaint();
        });
        raceTimer.start();
    }

    /**
     * Appends a message to the narrator area.
     * @param message the string to log
     */
    public static void log(String message) {
        if (instance != null && instance.narrator != null) {
            instance.narrator.append(message + "\n");
            instance.narrator.setCaretPosition(instance.narrator.getDocument().getLength());
        }
    }

    /**
     * Draws the overall race including track, obstacles, checkpoints, and cars.
     * @param g the Graphics object used to draw
     */
    private void drawRace(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getRacePanel().getWidth();
        int height = getRacePanel().getHeight();

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

    /**
     * Draws the track with grass and road ring using ellipses.
     * @param g2 Graphics2D used to draw shapes
     */
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

    /**
     * Draws yellow checkpoint lines at key track locations.
     * @param g2 Graphics2D used to draw lines
     */
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

            float midX = centerX + a * (float) Math.cos(theta);
            float midY = centerY + b * (float) Math.sin(theta);

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
            g2.drawString(loc.getName(), (x1 + x2) / 2, (y1 + y2) / 2);
        }
    }

    /**
     * Renders all obstacles on the track based on shape and transparency.
     * @param g2 Graphics2D used for obstacle drawing
     */
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
            int x = (int) (obs.getX() - obsSize / 2);
            int y = (int) (obs.getY() - obsSize / 2);

            switch (obs.getShapeType()) {
                case "circle":
                    g2.fillOval(x, y, obsSize, obsSize);
                    break;
                case "square":
                    g2.fillRect(x, y, obsSize, obsSize);
                    break;
                case "triangle":
                    int[] xs = {x + obsSize / 2, x, x + obsSize};
                    int[] ys = {y, y + obsSize, y + obsSize};
                    g2.fillPolygon(xs, ys, 3);
                    break;
                default:
                    g2.fillOval(x, y, obsSize, obsSize);
                    break;
            }
        }
    }

    /**
     * Renders cars on the track with proper rotation and shadow.
     * @param g2 Graphics2D used for car rendering
     */
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

    /**
     * Returns a distinct color for each car by ID.
     * @param id the unique ID of the car
     * @return Color object corresponding to the car ID
     */
    private Color getColorForCar(int id) {
        switch (id) {
            case 1:
                return Color.RED;
            case 2:
                return Color.ORANGE;
            case 3:
                return Color.CYAN;
            default:
                return Color.MAGENTA;
        }
    }

    /**
     * Updates the scoreboard with current data for each car.
     */
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

    /**
     * Displays a popup window announcing the race winner.
     * @param winner the car that finished the race first
     */
    public void announceWinner(Car winner) {
        JOptionPane.showMessageDialog(this,
                "Winner is Car " + winner.getId()
                        + " with time " + String.format("%.2f", winner.getTotalTime())
                        + " seconds!");
    }
}
