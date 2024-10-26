/*import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeductiveFirefighterGame extends JPanel {
    private final int GRID_SIZE = 10; // Taille de la grille (10x10)
    private final int CELL_SIZE = 50; // Taille de chaque cellule (50x50 pixels)
    private final int OBJECTIVES_COUNT = 2; // Nombre de cases d'objectif

    private enum Direction {UP, DOWN, LEFT, RIGHT} // Directions possibles pour chaque agent

    private boolean[][] fireGrid; // Grille des incendies (cases déjà brûlées)
    private boolean[][] safeGrid; // Grille des cases sécurisées par le pompier
    private boolean[][] barrierGrid; // Grille des barrières
    private int firefighterX, firefighterY; // Position du pompier
    private Direction firefighterDirection = Direction.UP; // Direction initiale du pompier
    private int firefighterScore = 0, fireScore = 0; // Scores
    private List<int[]> objectives; // Liste des objectifs (cases spécifiques)
    private Random random = new Random();
    private boolean gameOver = false;
    private String winnerMessage = "";
    private int[] firePosition; // Position actuelle du feu
    private Direction fireDirection = Direction.UP; // Direction initiale du feu

    public DeductiveFirefighterGame() {
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE + 50)); // Espace pour l'interface
        fireGrid = new boolean[GRID_SIZE][GRID_SIZE]; // Initialiser la grille des incendies
        safeGrid = new boolean[GRID_SIZE][GRID_SIZE]; // Initialiser la grille de sécurité du pompier
        barrierGrid = new boolean[GRID_SIZE][GRID_SIZE]; // Initialiser la grille des barrières
        objectives = new ArrayList<>(); // Initialiser les objectifs
        initializeGame();
    }

    private void initializeGame() {
        // Initialiser la position initiale du feu
        int fireX = random.nextInt(GRID_SIZE);
        int fireY = random.nextInt(GRID_SIZE);
        fireGrid[fireX][fireY] = true;
        firePosition = new int[]{fireX, fireY};
        System.out.println("Incendie initial à la position (" + fireX + ", " + fireY + ")");

        // Initialiser des barrières aléatoires (bâtiments)
        for (int i = 0; i < 5; i++) {
            int barrierX = random.nextInt(GRID_SIZE);
            int barrierY = random.nextInt(GRID_SIZE);
            barrierGrid[barrierX][barrierY] = true;
            System.out.println("Barrière placée à la position (" + barrierX + ", " + barrierY + ")");
        }

        // Initialiser les cases d'objectif
        for (int i = 0; i < OBJECTIVES_COUNT; i++) {
            int objX = random.nextInt(GRID_SIZE);
            int objY = random.nextInt(GRID_SIZE);
            while (barrierGrid[objX][objY] || fireGrid[objX][objY]) {
                objX = random.nextInt(GRID_SIZE);
                objY = random.nextInt(GRID_SIZE);
            }
            objectives.add(new int[]{objX, objY});
            System.out.println("Objectif placé à la position (" + objX + ", " + objY + ")");
        }

        // Initialiser le pompier au centre de la grille
        firefighterX = GRID_SIZE / 2;
        firefighterY = GRID_SIZE / 2;
        System.out.println("Pompier initialisé à la position (" + firefighterX + ", " + firefighterY + ")");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessiner la grille
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (fireGrid[i][j]) {
                    g.setColor(new Color(255, 150, 150)); // Rouge clair pour les cases déjà brûlées
                } else if (safeGrid[i][j]) {
                    g.setColor(Color.CYAN); // Cyan pour les cases sécurisées
                } else if (barrierGrid[i][j]) {
                    g.setColor(Color.GRAY); // Gris pour les barrières
                } else {
                    g.setColor(Color.WHITE); // Blanc pour les cellules vides
                }
                g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Dessiner les cases d'objectif (vert)
        g.setColor(Color.GREEN);
        for (int[] obj : objectives) {
            g.fillOval(obj[0] * CELL_SIZE, obj[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // Dessiner le pompier (bleu)
        g.setColor(Color.BLUE);
        g.fillOval(firefighterX * CELL_SIZE, firefighterY * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Dessiner la position actuelle du feu comme un cercle rouge vif
        g.setColor(Color.RED);
        g.fillOval(firePosition[0] * CELL_SIZE, firePosition[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Affichage des informations
        g.setColor(Color.BLACK);
        g.drawString("Pompier Score: " + firefighterScore, 10, GRID_SIZE * CELL_SIZE + 20);
        g.drawString("Feu Score: " + fireScore, 150, GRID_SIZE * CELL_SIZE + 20);

        // Afficher le message de fin si le jeu est terminé
        if (gameOver) {
            g.drawString(winnerMessage, 300, GRID_SIZE * CELL_SIZE + 20);
        }
    }

    // Méthode pour déplacer le pompier vers l'objectif
    public void moveFirefighter() {
        if (objectives.isEmpty()) return;

        int[] targetObjective = objectives.get(0); // Par exemple, prendre le premier objectif restant
        firefighterDirection = determineDirection(firefighterX, firefighterY, targetObjective[0], targetObjective[1]);

        switch (firefighterDirection) {
            case UP -> firefighterY = Math.max(0, firefighterY - 1);
            case DOWN -> firefighterY = Math.min(GRID_SIZE - 1, firefighterY + 1);
            case LEFT -> firefighterX = Math.max(0, firefighterX - 1);
            case RIGHT -> firefighterX = Math.min(GRID_SIZE - 1, firefighterX + 1);
        }

        System.out.println("Pompier se déplace " + firefighterDirection + " vers l'objectif (" + firefighterX + ", " + firefighterY + ")");

        // Extinction du feu s'il est présent sur la case actuelle du pompier
        if (fireGrid[firefighterX][firefighterY]) {
            fireGrid[firefighterX][firefighterY] = false;
            System.out.println("Feu éteint par le pompier à la position (" + firefighterX + ", " + firefighterY + ")");
        }

        // Marquer la case comme sécurisée pour empêcher le feu de revenir dessus
        safeGrid[firefighterX][firefighterY] = true;
        saveObjective();
    }

    public void moveFireTowardsObjective() {
        if (objectives.isEmpty()) return;

        int[] targetObjective = objectives.get(0);
        fireDirection = determineDirection(firePosition[0], firePosition[1], targetObjective[0], targetObjective[1]);

        int nextX = firePosition[0], nextY = firePosition[1];
        boolean moved = false;

        // Essayer de se déplacer dans la direction déterminée
        switch (fireDirection) {
            case UP -> nextY = Math.max(0, firePosition[1] - 1);
            case DOWN -> nextY = Math.min(GRID_SIZE - 1, firePosition[1] + 1);
            case LEFT -> nextX = Math.max(0, firePosition[0] - 1);
            case RIGHT -> nextX = Math.min(GRID_SIZE - 1, firePosition[0] + 1);
        }

        // Vérifier si la prochaine case n'est pas sécurisée avant de déplacer le feu
        if (!safeGrid[nextX][nextY] && !barrierGrid[nextX][nextY]) {
            firePosition[0] = nextX;
            firePosition[1] = nextY;
            fireGrid[firePosition[0]][firePosition[1]] = true; // Marquer la position du feu comme brûlée
            System.out.println("Feu se déplace " + fireDirection + " vers (" + firePosition[0] + ", " + firePosition[1] + ")");
            moved = true;
            burnObjective(); // Appel direct pour brûler un objectif si présent

            propagateFireToNeighbors(firePosition[0], firePosition[1]);
        }

        // Si le feu ne peut pas se déplacer, essayer toutes les directions
        if (!moved) {
            Direction[] directions = Direction.values();
            for (Direction dir : directions) {
                int tempX = firePosition[0];
                int tempY = firePosition[1];

                switch (dir) {
                    case UP -> tempY = Math.max(0, firePosition[1] - 1);
                    case DOWN -> tempY = Math.min(GRID_SIZE - 1, firePosition[1] + 1);
                    case LEFT -> tempX = Math.max(0, firePosition[0] - 1);
                    case RIGHT -> tempX = Math.min(GRID_SIZE - 1, firePosition[0] + 1);
                }

                if (!safeGrid[tempX][tempY] && !barrierGrid[tempX][tempY]) {
                    firePosition[0] = tempX;
                    firePosition[1] = tempY;
                    fireGrid[firePosition[0]][firePosition[1]] = true;
                    System.out.println("Feu se déplace vers (" + firePosition[0] + ", " + firePosition[1] + ")");
                    burnObjective(); // Appel direct pour brûler un objectif si présent
                    propagateFireToNeighbors(firePosition[0], firePosition[1]);
                    return;
                }
            }

            System.out.println("Feu bloqué à la position (" + firePosition[0] + ", " + firePosition[1] + ") par toutes les directions.");
        }
    }


    // Méthode pour propager le feu aux cases voisines
    private void propagateFireToNeighbors(int x, int y) {
        int[][] neighbors = {{x - 1, y}, {x + 1, y}, {x, y - 1}, {x, y + 1}};

        for (int[] neighbor : neighbors) {
            int nx = neighbor[0];
            int ny = neighbor[1];

            // Vérifier que le voisin est dans les limites et n'est pas sécurisé ou une barrière
            if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE &&
                    !safeGrid[nx][ny] && !barrierGrid[nx][ny]) {
                fireGrid[nx][ny] = true; // Marquer la case comme brûlée
                System.out.println("Propagation du feu à la case (" + nx + ", " + ny + ")");

                // Incrémenter le score du feu si le voisin est un objectif
                for (int i = 0; i < objectives.size(); i++) {
                    int[] obj = objectives.get(i);
                    if (obj[0] == nx && obj[1] == ny) {
                        fireScore++;
                        objectives.remove(i); // Retirer immédiatement l'objectif brûlé
                        System.out.println("Objectif brûlé par propagation à la position (" + nx + ", " + ny + ")");
                        break; // Sortir de la boucle une fois l'objectif retiré
                    }
                }
            }
        }
    }



    // Déterminer la direction pour atteindre un objectif
    private Direction determineDirection(int startX, int startY, int targetX, int targetY) {
        if (startX < targetX) return Direction.RIGHT;
        if (startX > targetX) return Direction.LEFT;
        if (startY < targetY) return Direction.DOWN;
        return Direction.UP;
    }

    // Sauver l'objectif si atteint par le pompier
    private void saveObjective() {
        // Vérifie si la case actuelle est un objectif
        objectives.removeIf(obj -> {
            if (obj[0] == firefighterX && obj[1] == firefighterY) {
                firefighterScore++; // Incrémente le score uniquement lorsque l'objectif est retiré
                System.out.println("Objectif sauvé à la position (" + firefighterX + ", " + firefighterY + ")");
                return true;
            }
            return false;
        });
    }

    // Brûler l'objectif si atteint par le feu
    private void burnObjective() {
        // Vérifie si la case actuelle est un objectif
        objectives.removeIf(obj -> {
            if (obj[0] == firePosition[0] && obj[1] == firePosition[1]) {
                fireScore++; // Incrémente le score uniquement lorsque l'objectif est retiré
                System.out.println("Objectif brûlé à la position (" + firePosition[0] + ", " + firePosition[1] + ")");
                return true;
            }
            return false;
        });
    }



    // Vérifier si le jeu est terminé
    // Vérifier si le jeu est terminé
    private void checkGameOver() {
        if (objectives.isEmpty()) {
            gameOver = true;
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
        JFrame frame = new JFrame("Deductive Firefighter Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    moveFireTowardsObjective(); // Déplacer le feu vers l'objectif
                    moveFirefighter(); // Déplacer le pompier
                    checkGameOver(); // Vérifier si le jeu est terminé
                    repaint(); // Redessiner la grille après chaque tour
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        DeductiveFirefighterGame game = new DeductiveFirefighterGame();
        game.playGame();
    }
}
*/