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
    // Baheeja - Added start page, countdown, changed
    private JPanel startPage;
    private JPanel racePanel;
    private JPanel controlPanel;
    private JTextArea narrator;
    private JTable scoreboard;
    private DefaultTableModel tableModel;
    private static RaceDisplay instance;
    private Timer raceTimer;
    private JLabel countdownLabel;

    public RaceDisplay(Race race) {
        instance = this;
        this.race = race;
        setLayout(new BorderLayout());

        // --- Start Page ---
        startPage = new JPanel();
        startPage.setLayout(new BoxLayout(startPage, BoxLayout.Y_AXIS));
        startPage.setBackground(Color.BLUE);


        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1500, 400));

        CheckerBoard checkerBoard = new CheckerBoard();
        checkerBoard.setBounds(0, 0, 1500, 200);
        layeredPane.add(checkerBoard, Integer.valueOf(0));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBounds(0, 20, 1500, 200);

        JLabel title = new JLabel("Car Racing Game", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 60));
        title.setForeground(Color.RED);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel("Compete against other cars and win the race!");
        description.setFont(new Font("Times New Roman", Font.BOLD, 50));
        description.setForeground(Color.WHITE);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rules1 = new JLabel("How to play");
        rules1.setFont(new Font("Times New Roman", Font.BOLD, 40));
        rules1.setForeground(Color.WHITE);
        rules1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rules2 = new JLabel("Use the left and right arrow keys to steer.");
        rules2.setFont(new Font("Times New Roman", Font.BOLD, 30));
        rules2.setForeground(Color.WHITE);
        rules2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rules3 = new JLabel("Avoid the obsticles or else you lose speed!");
        rules3.setFont(new Font("Times New Roman", Font.BOLD, 30));
        rules3.setForeground(Color.WHITE);
        rules3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel play = new JLabel("Press play to begin the race.");
        play.setFont(new Font("Times New Roman", Font.BOLD, 20));
        play.setForeground(Color.WHITE);
        play.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        startPage.add(Box.createRigidArea(new Dimension(0, 120)));
        startPage.add(play);
        startPage.add(playButton);
        startPage.add(Box.createRigidArea(new Dimension(0, 90)));


        add(startPage, BorderLayout.CENTER);
    }

     /*   JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBounds(0, 0, 800, 600);

        JLabel title = new JLabel("<html><p style='margin-top: 70px;'> Car Racing Game</html>");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setForeground(Color.GREEN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel("<html><p style='margin-top: 70px;'> Compete against other cars and win the race!.</html>");
        description.setFont(new Font("Arial", Font.BOLD, 40));
        description.setForeground(Color.WHITE);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rules = new JLabel("<html><p style='margin-top: 50px;'Directions:\n Use the left and right arrow keys to steer<br>press the start button to begin the race.</html> ");
        rules.setFont(new Font("Arial", Font.BOLD, 30));
        rules.setForeground(Color.WHITE);
        rules.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton playButton = new JButton("Play");
        playButton.add(Box.createRigidArea(new Dimension(200, 40)));
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


        startPage.add(checkerBoard);

        startPage.add(title);
        startPage.add(checkerBoard);
        startPage.add(description);
        startPage.add(rules);
        startPage.add(playButton);

        add(startPage, BorderLayout.CENTER);

    }
*/
    // --- Getters for panels ---
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


// --- Countdown ---
    private void startCountdown() {
        if (countdownLabel == null) {
            countdownLabel = new JLabel("4", SwingConstants.CENTER);
            countdownLabel.setFont(new Font("Arial", Font.BOLD, 48));
            countdownLabel.setForeground(Color.WHITE);
            countdownLabel.setBackground(Color.DARK_GRAY);
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

    // --- Start Race ---
    private void startRace() {
        race.startRace();
        raceTimer = new Timer(100, e -> {
            race.updateRaceStatus();
            updateScoreboard();
            getRacePanel().repaint();
        });
        raceTimer.start();
    }

    // --- Logging helper for the narrator text area.
    public static void log(String message) {
        if (instance != null && instance.narrator != null) {
            instance.narrator.append(message + "\n");
            instance.narrator.setCaretPosition(instance.narrator.getDocument().getLength());
        }
    }

    // --- Main rendering routine for the race panel.
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

    // --- Draws the oval track ring (outer boundary + inner boundary).
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

    // --- Draws checkpoint lines across the track.
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

    // --- Draws obstacles (with partial transparency).
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

    // --- Draws each car as a small rectangle with a little shadow.
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

    // --- Distinct color per car ID.
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

    // --- Populates the scoreboard table with each car’s info.
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

    // --- Called by Race to announce a winner in a popup.
    public void announceWinner(Car winner) {
        JOptionPane.showMessageDialog(this,
                "Winner is Car " + winner.getId()
                        + " with time " + String.format("%.2f", winner.getTotalTime())
                        + " seconds!");
    }
}





/*import javax.swing.*;
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
*/