import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class FirefighterGame extends JPanel {
    private static final int GRID_SIZE = 10;
    private static final int OBJECTIVES_COUNT = 9;
    private static final int CELL_SIZE = 50;
    private final Grid grid;
    private final FirefighterAgent firefighterAgent;
    private final FireAgent fireAgent;
    private boolean gameOver = false;
    private String winnerMessage = "";
    private final Image fireIcon;
    private final Image firefighterIcon;
    private final Image barrierIcon;
    private boolean fireAgentTurn = true; // Boolean to alternate turns
    private final Image humanIcon;
    private int roundsCounter = 0;
    private final int roundsUntilHumanAppears;
    private int humanX = -1;
    private int humanY = -1;
    private boolean humanAppeared = false;
    private int roundsSinceHumanMoved = 0;

    public FirefighterGame() {
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE + 50));
        grid = new Grid(GRID_SIZE, OBJECTIVES_COUNT);

        roundsUntilHumanAppears = 1 + (int) (Math.random() * 9);

        // Ensure FireAgent starts in an empty cell
        int fireX, fireY;
        do {
            fireX = (int) (Math.random() * GRID_SIZE);
            fireY = (int) (Math.random() * GRID_SIZE);
        } while (grid.isObjectiveAt(fireX, fireY) || grid.isBarrierAt(fireX, fireY));

        fireAgent = new FireAgent(fireX, fireY, grid, OBJECTIVES_COUNT);

        // Ensure FirefighterAgent starts in an empty cell
        int firefighterX, firefighterY;
        do {
            firefighterX = (int) (Math.random() * GRID_SIZE / 3);
            firefighterY = (int) (Math.random() * GRID_SIZE);
        } while ((firefighterX == fireX && firefighterY == fireY) ||
                grid.isObjectiveAt(firefighterX, firefighterY) ||
                grid.isBarrierAt(firefighterX, firefighterY));

        firefighterAgent = new FirefighterAgent(firefighterX, firefighterY, grid, OBJECTIVES_COUNT);

        // Load icons
        fireIcon = new ImageIcon("assets/icons/img_3.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        firefighterIcon = new ImageIcon("assets/icons/img_1.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        barrierIcon = new ImageIcon("assets/icons/img_5.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        humanIcon = new ImageIcon("assets/icons/img_8.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawFirefighter(g);
        drawFire(g);
        drawObjectives(g);
        drawBarriers(g);
        drawScores(g);
        drawHuman(g);
        if (gameOver) drawGameOverMessage(g);
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid.isFireAt(i, j)) {
                    g.setColor(new Color(255, 150, 150)); // Light red for burning cells
                } else if (grid.isSafeAt(i, j)) {
                    g.setColor(Color.CYAN); // Cyan for safe cells
                } else {
                    g.setColor(Color.WHITE); // White for empty cells
                }
                g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void drawFirefighter(Graphics g) {
        g.drawImage(firefighterIcon, firefighterAgent.getX() * CELL_SIZE, firefighterAgent.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
    }

    private void drawFire(Graphics g) {
        g.drawImage(fireIcon, fireAgent.getX() * CELL_SIZE, fireAgent.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
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

    private void drawBarriers(Graphics g) {
        for (int[] obj : grid.getBarriers()) {
            g.drawImage(barrierIcon, obj[0] * CELL_SIZE, obj[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        }
    }

    private void drawScores(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Pompier Score: " + firefighterAgent.getScore(), 10, GRID_SIZE * CELL_SIZE + 20);
        g.drawString("Feu Score: " + fireAgent.getScore(), 150, GRID_SIZE * CELL_SIZE + 20);
    }

    private void drawGameOverMessage(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString(winnerMessage, 300, GRID_SIZE * CELL_SIZE + 20);
    }

    private void drawHuman(Graphics g) {
        if (humanAppeared) {
            g.drawImage(humanIcon, humanX * CELL_SIZE, humanY * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        }
    }

    private void checkGameOver() {
        if (!grid.hasObjectives()) { // No objectives left
            gameOver = true;

            int firefighterScore = firefighterAgent.getScore();
            int fireScore = fireAgent.getScore();

            if (firefighterScore > fireScore) {
                winnerMessage = "Victory for the Firefighter! Protected objectives: " + firefighterScore;
            } else if (fireScore > firefighterScore) {
                winnerMessage = "Victory for the Fire! Burned objectives: " + fireScore;
            } else {
                winnerMessage = "It's a tie! Scores are equal: " + firefighterScore;
            }

            JOptionPane.showMessageDialog(this, winnerMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }

        if ((firefighterAgent.getX() == humanX && firefighterAgent.getY() == humanY)) {
            gameOver = true;
            winnerMessage = "Victory for the Firefighter! Reached the human!";
            JOptionPane.showMessageDialog(this, winnerMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        } else if ((fireAgent.getX() == humanX && fireAgent.getY() == humanY)) {
            gameOver = true;
            winnerMessage = "Victory for the Fire! Reached the human!";
            JOptionPane.showMessageDialog(this, winnerMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void playGame() {
        JFrame frame = new JFrame("Firefighter Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    roundsCounter++;
                    roundsSinceHumanMoved++;

                    if (!humanAppeared && roundsCounter >= roundsUntilHumanAppears) {
                        placeHumanInEmptyCell();
                        humanAppeared = true;
                        grid.setHumanPosition(humanX, humanY);
                    }

                    if (grid.isHumanAppeared()) {
                        firefighterAgent.move();
                        fireAgent.move();
                        grid.resetNotification();
                        checkGameOver();
                        repaint();
                        return;
                    }

                    if (!gameOver) {
                        if (fireAgentTurn) {
                            fireAgent.move();
                        } else {
                            firefighterAgent.move();
                        }
                        checkGameOver();
                        repaint();
                        fireAgentTurn = !fireAgentTurn;
                    }
                }
            }
        });
        timer.start();
    }

    private void placeHumanInEmptyCell() {
        do {
            humanX = (int) (Math.random() * GRID_SIZE);
            humanY = (int) (Math.random() * GRID_SIZE);
        } while (grid.isObjectiveAt(humanX, humanY) ||
                grid.isBarrierAt(humanX, humanY) ||
                grid.isFireAt(humanX, humanY) || grid.isSafeAt(humanX, humanY) ||
                (humanX == fireAgent.getX() && humanY == fireAgent.getY()) ||
                (humanX == firefighterAgent.getX() && humanY == firefighterAgent.getY()));
    }

    public static void main(String[] args) {
        FirefighterGame game = new FirefighterGame();
        game.playGame();
    }
}
