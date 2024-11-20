import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class FirefighterGame extends JPanel {
    private static final int GRID_SIZE = 10;
    public static final int OBJECTIVES_COUNT = 9;
    private static final int CELL_SIZE = 50;

    private final Grid grid;
    private FirefighterAgent firefighterAgent;
    private FireAgent fireAgent;
    private boolean gameOver = false;
    private String winnerMessage = "";
    private Timer timer;
    private final Image fireIcon;
    private final Image firefighterIcon;
    private final Image humanIcon;
    private final Image barrierIcon;
    private boolean fireAgentTurn = true;
    private int humanX = -1;
    private int humanY = -1;
    private int rounds = 0;
    private boolean humanAppeared = false;

    private JLabel firefighterScoreLabel;
    private JLabel fireScoreLabel;

    public FirefighterGame() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE + 100));

        grid = new Grid(GRID_SIZE, OBJECTIVES_COUNT);
        initializeAgents();

        fireIcon = new ImageIcon("assets/icons/img_3.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        firefighterIcon = new ImageIcon("assets/icons/img_1.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        humanIcon = new ImageIcon("assets/icons/img_8.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        barrierIcon = new ImageIcon("assets/icons/img_5.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);

        // Panneau inférieur pour les scores et les boutons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.GRAY); // Fond gris pour le panneau

        // Ajouter les scores
        firefighterScoreLabel = new JLabel("Pompier Score: 0");
        fireScoreLabel = new JLabel("Feu Score: 0");

        bottomPanel.add(firefighterScoreLabel);
        bottomPanel.add(Box.createHorizontalStrut(20)); // Espace entre les scores
        bottomPanel.add(fireScoreLabel);

        // Ajouter les boutons
        JButton startButton =  new JButton("Start", new ImageIcon("assets/icons/icons8-démarrer-30.png"));

        JButton restartButton = new JButton("Restart", new ImageIcon("assets/icons/icons8-redémarrer-30.png"));

        bottomPanel.add(startButton);
        bottomPanel.add(restartButton);

        // Ajouter le panneau inférieur
        add(bottomPanel, BorderLayout.SOUTH);

        // Ajouter les actions des boutons
        startButton.addActionListener(e -> startGame());
        restartButton.addActionListener(e -> restartGame());

        // Initialiser le Timer
        timer = new Timer(1000, e -> {
            if (!gameOver) {
                playTurn();
            }
        });
    }

    private void initializeAgents() {
        int fireX, fireY;
        do {
            fireX = (int) (Math.random() * GRID_SIZE);
            fireY = (int) (Math.random() * GRID_SIZE);
        } while (grid.isObjectiveAt(fireX, fireY) || grid.isBarrierAt(fireX, fireY));
        fireAgent = new FireAgent(fireX, fireY, grid, OBJECTIVES_COUNT);

        int firefighterX, firefighterY;
        do {
            firefighterX = (int) (Math.random() * GRID_SIZE);
            firefighterY = (int) (Math.random() * GRID_SIZE);
        } while ((firefighterX == fireX && firefighterY == fireY) ||
                grid.isObjectiveAt(firefighterX, firefighterY) ||
                grid.isBarrierAt(firefighterX, firefighterY));
        firefighterAgent = new FirefighterAgent(firefighterX, firefighterY, grid, OBJECTIVES_COUNT);
    }

    private void startGame() {
        timer.start();
    }
    private void checkGameOver() {
        if (!grid.hasObjectives()) {
            gameOver = true;
            int firefighterScore = firefighterAgent.getScore();
            int fireScore = fireAgent.getScore();

            if (firefighterScore > fireScore) {
                winnerMessage = "Victoire du pompier ! Objectifs protégés : " + firefighterScore;
            } else if (fireScore > firefighterScore) {
                winnerMessage = "Victoire du feu ! Objectifs brûlés : " + fireScore;
            } else {
                winnerMessage = "Match nul ! Les scores sont égaux : " + firefighterScore;
            }

            JOptionPane.showMessageDialog(this, winnerMessage, "Résultat de la Partie", JOptionPane.INFORMATION_MESSAGE);

        }
        // Optional: Check if a special condition (e.g., reaching the human) ends the game
        if ((firefighterAgent.getX() == humanX && firefighterAgent.getY() == humanY)) {
            winnerMessage = "Pompier a sauvé l'humain !";
            humanX =-1;
            humanY = -1;
            gameOver = true;
            JOptionPane.showMessageDialog(this, winnerMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        } else if ((fireAgent.getX() == humanX && fireAgent.getY() == humanY)) {
            winnerMessage = "Feu a capturé l'humain !";
            humanX =-1;
            humanY = -1;
            gameOver = true;
            JOptionPane.showMessageDialog(this, winnerMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }


    }

    private void playTurn() {
        rounds++;
        if (fireAgentTurn) {
            fireAgent.move();
        } else {
            firefighterAgent.move();
        }
        fireAgentTurn = !fireAgentTurn;

        // Mettre à jour les scores
        updateScores();

        checkGameOver();
        repaint();
    }

    private void restartGame() {
        timer.stop();
        gameOver = false;
        rounds = 0;
        fireAgentTurn = true;
        winnerMessage = "";
        humanX = -1;
        humanY = -1;
        humanAppeared = false;
        grid.reset();
        initializeAgents();

        // Réinitialiser les scores
        updateScores();
        timer.start(); // Redémarrer le timer pour que les agents bougent à nouveau

        repaint();
    }

    private void updateScores() {
        firefighterScoreLabel.setText("Pompier Score: " + firefighterAgent.getScore());
        fireScoreLabel.setText("Feu Score: " + fireAgent.getScore());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawFirefighter(g);
        drawFire(g);
        drawObjectives(g);
        if (humanX != -1 && humanY != -1) {
            drawHuman(g);
        }
        if (gameOver) drawGameOverMessage(g);
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid.isFireAt(i, j)) {
                    g.setColor(new Color(255, 150, 150));
                } else if (grid.isSafeAt(i, j)) {
                    g.setColor(Color.CYAN);
                } else if (grid.isBarrierAt(i, j)) {
                    g.setColor(new Color(80, 60, 70));
                    g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.drawImage(barrierIcon, i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
                } else {
                    g.setColor(Color.WHITE);
                }
                if (grid.isFireAt(i, j) || grid.isSafeAt(i, j) || !grid.isBarrierAt(i, j)) {
                    g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private void drawFirefighter(Graphics g) {
        g.drawImage(firefighterIcon, firefighterAgent.getX() * CELL_SIZE, firefighterAgent.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
    }

    private void drawFire(Graphics g) {
        g.drawImage(fireIcon, fireAgent.getX() * CELL_SIZE, fireAgent.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
    }

    private void drawHuman(Graphics g) {
        if (humanAppeared) {
            g.drawImage(humanIcon, humanX * CELL_SIZE, humanY * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        }
    }

    private void drawObjectives(Graphics g) {
        try {
            Image greenIcon = ImageIO.read(new File("assets/icons/img_4.png"));
            for (int[] obj : grid.getObjectives()) {
                g.drawImage(greenIcon, obj[0] * CELL_SIZE, obj[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawGameOverMessage(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString(winnerMessage, 300, GRID_SIZE * CELL_SIZE + 20);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Firefighter Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FirefighterGame game = new FirefighterGame();
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
    }
}

