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
    private boolean fireAgentTurn = true; // Boolean to alternate turns
    private final Image humanIcon;
    private final Image barrierIcon;
    private int humanX = -1; // Human's X position (-1 means not placed yet)
    private int humanY = -1; // Human's Y position
    private int rounds = 0; // Count rounds
    private boolean humanAppeared = false;


    public FirefighterGame() {
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE + 50));
        grid = new Grid(GRID_SIZE, OBJECTIVES_COUNT);

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

        fireIcon = new ImageIcon("assets/icons/img_3.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        firefighterIcon = new ImageIcon("assets/icons/img_1.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        humanIcon = new ImageIcon("assets/icons/img_8.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        barrierIcon = new ImageIcon("assets/icons/img_5.png").getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);

        /*
        // Create control panel
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start Game");
        JButton pauseButton = new JButton("Pause");

        // Add buttons to control panel
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);

        // Create an empty space above the game grid using a JPanel
        JPanel emptyZone = new JPanel();
        emptyZone.setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, 50)); // Empty zone height

        // Add the empty zone to the top of the panel
        add(emptyZone, BorderLayout.LINE_END);

        // Add the control panel to the top (after the empty zone)
        add(controlPanel, BorderLayout.LINE_END);

         */
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawFirefighter(g);
        drawFire(g);
        drawObjectives(g);
        drawScores(g);
        if (humanX != -1 && humanY != -1) {
            drawHuman(g);
        }
        if (gameOver) drawGameOverMessage(g);
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid.isFireAt(i, j)) {
                    g.setColor(new Color(255, 150, 150)); // Light red for burning cells
                } else if (grid.isSafeAt(i, j)) {
                    g.setColor(Color.CYAN);
                } else if (grid.isBarrierAt(i, j)) {
                    g.setColor(new Color(80, 60, 70)); // Dark gray for barriers
                    g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE); // Draw dark gray background
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
        if(humanAppeared){
            g.drawImage(humanIcon, humanX * CELL_SIZE, humanY * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);}
    }
    private void placeHumanInEmptyCell() {
        do {
            humanX = (int) (Math.random() * GRID_SIZE);
            humanY = (int) (Math.random() * GRID_SIZE);
        } while (!grid.isEmpty(humanX, humanY) ||
                (firefighterAgent.getX() == humanX && firefighterAgent.getY() == humanY) ||
                (fireAgent.getX() == humanX && fireAgent.getY() == humanY));
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

    private void drawScores(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Pompier Score: " + firefighterAgent.getScore(), 10, GRID_SIZE * CELL_SIZE + 20);
        g.drawString("Feu Score: " + fireAgent.getScore(), 150, GRID_SIZE * CELL_SIZE + 20);
    }

    private void drawGameOverMessage(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString(winnerMessage, 300, GRID_SIZE * CELL_SIZE + 20);
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
                    rounds++; // Increment round counter
                    if (rounds >= (5+(Math.random()*5))&& humanX == -1 && humanY == -1) { // Human appears after 5 and 10 rounds
                        placeHumanInEmptyCell();
                        humanAppeared = true;
                        grid.setHumanPosition(humanX, humanY);
                    }
                    if (fireAgentTurn) {
                        // Tour de l'agent feu
                        fireAgent.move();
                        System.out.println("--------------Fire Agent Turn");
                        System.out.println("Feu : Score actuel = " + fireAgent.getScore());
                    } else {
                        // Tour de l'agent pompier
                        System.out.println("//////////////Fire Fighter Agent Turn");
                        firefighterAgent.move();
                        System.out.println("Pompier : Score actuel = " + firefighterAgent.getScore());
                    }

// Vérifier si un humain est apparu
                    if (grid.isHumanAppeared()) {
                        System.out.println("Human appeared on the grid!");

                        grid.resetNotification(); // Réinitialiser l'état d'apparition
                    }

// Vérifier si le jeu est terminé après chaque action
                    checkGameOver();

// Redessiner la grille après chaque action
                    repaint();

// Inverser le tour pour la prochaine itération
                    fireAgentTurn = !fireAgentTurn;

                }
            }
        });
        timer.start();
    }
    public static void main(String[] args) {
        FirefighterGame game = new FirefighterGame();
        game.playGame();
    }
}
