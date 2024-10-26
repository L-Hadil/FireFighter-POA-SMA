import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FirefighterGame extends JPanel {
    private static final int GRID_SIZE = 10; // Taille de la grille (10x10)
    private static final int OBJECTIVES_COUNT = 2; // Nombre de cases d'objectif
    private static final int CELL_SIZE = 50; // Taille de chaque cellule
    private final Grid grid; // Instance de la classe Grid
    private final FirefighterAgent firefighterAgent; // Instance de FirefighterAgent
    private final FireAgent fireAgent; // Instance de FireAgent
    private boolean gameOver = false; // État du jeu
    private String winnerMessage = ""; // Message de fin de jeu

    public FirefighterGame() {
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE + 50));
        grid = new Grid(GRID_SIZE, OBJECTIVES_COUNT);

        // Initialisation des agents avec OBJECTIVES_COUNT
        fireAgent = new FireAgent((int) (Math.random() * GRID_SIZE), (int) (Math.random() * GRID_SIZE), grid, OBJECTIVES_COUNT);
        firefighterAgent = new FirefighterAgent(GRID_SIZE / 2, GRID_SIZE / 2, grid, OBJECTIVES_COUNT);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawFirefighter(g);
        drawFire(g);
        drawObjectives(g);
        drawScores(g);
        if (gameOver) drawGameOverMessage(g);
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid.isFireAt(i, j)) {
                    g.setColor(new Color(255, 150, 150)); // Rouge clair pour les cases brûlées
                } else if (grid.isSafeAt(i, j)) {
                    g.setColor(Color.CYAN); // Cyan pour les cases sécurisées
                } else if (grid.isBarrierAt(i, j)) {
                    g.setColor(Color.GRAY); // Gris pour les barrières
                } else {
                    g.setColor(Color.WHITE); // Blanc pour les cellules vides
                }
                g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void drawFirefighter(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(firefighterAgent.getX() * CELL_SIZE, firefighterAgent.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    private void drawFire(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(fireAgent.getX() * CELL_SIZE, fireAgent.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    private void drawObjectives(Graphics g) {
        g.setColor(Color.GREEN);
        for (int[] obj : grid.getObjectives()) {
            g.fillOval(obj[0] * CELL_SIZE, obj[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
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
        // Vérification si tous les objectifs sont épuisés
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
            System.out.println(winnerMessage);
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
                    fireAgent.move(); // Déplacer le feu
                    firefighterAgent.move(); // Déplacer le pompier
                    checkGameOver(); // Vérifier si le jeu est terminé
                    repaint(); // Redessiner la grille après chaque tour
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
