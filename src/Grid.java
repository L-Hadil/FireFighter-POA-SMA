import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid {
    private final int gridSize;
    private final boolean[][] fireGrid;    // Grille des incendies
    private final boolean[][] safeGrid;     // Grille des cases sécurisées
    private final boolean[][] barrierGrid;  // Grille des barrières
    private final List<int[]> objectives;   // Liste des objectifs
    private final Random random;
    private List<Agent> agents = new ArrayList<>();
    private final boolean[][] humanGrid;
    private boolean notifyAgents = false; // Flag to notify agents
    private int[] humanPosition = null;
    private boolean humanAppeared = false;




    public void resetNotification() {
        this.notifyAgents = false; // Réinitialise la notification après réaction
    }


    public void setHumanPosition(int x, int y) {
        this.humanPosition = new int[]{x, y};
        this.humanAppeared = true;
        this.notifyAgents = true; // Notifie tous les agents
        System.out.println("Human appeared at (" + x + ", " + y + ")");
    }


    public int[] getHumanPosition() {
        if (humanPosition == null) {
            System.out.println("Aucun humain n'a été défini dans la grille.");
            return null;
        }
        return humanPosition;
    }



    public Grid(int size, int objectivesCount) {
        this.gridSize = size;
        this.fireGrid = new boolean[size][size];
        this.safeGrid = new boolean[size][size];
        this.barrierGrid = new boolean[size][size];
        this.objectives = new ArrayList<>();
        this.random = new Random();
        this.humanGrid= new boolean[size][size];

        initializeBarriers();
        initializeObjectives(objectivesCount);
    }

    public int getGridSize() {
        return gridSize; // Return the grid size
    }

    private void initializeBarriers() {
        for (int i = 0; i < 5; i++) { // Example for placing 5 barriers
            int barrierX = random.nextInt(gridSize);
            int barrierY = random.nextInt(gridSize);
            barrierGrid[barrierX][barrierY] = true;
            System.out.println("Barrière placée à la position (" + barrierX + ", " + barrierY + ")");
        }
    }

    private void initializeObjectives(int count) {
        for (int i = 0; i < count; i++) {
            int objX = random.nextInt(gridSize);
            int objY = random.nextInt(gridSize);
            while (barrierGrid[objX][objY] || fireGrid[objX][objY]) {
                objX = random.nextInt(gridSize);
                objY = random.nextInt(gridSize);
            }
            objectives.add(new int[]{objX, objY});
            System.out.println("Objectif placé à la position (" + objX + ", " + objY + ")");
        }
    }

    public boolean isFireAt(int x, int y) {
        return fireGrid[x][y];
    }

    public void setFireAt(int x, int y, boolean onFire) {
        fireGrid[x][y] = onFire;
    }

    public boolean isSafeAt(int x, int y) {
        return safeGrid[x][y];  // Returns whether the cell is safe
    }

    public void setSafeAt(int x, int y) {
        safeGrid[x][y] = true;  // Mark the cell as safe
    }

    public boolean isBarrierAt(int x, int y) {
        return barrierGrid[x][y];  // Returns whether there is a barrier at the given cell
    }

    public List<int[]> getObjectives() {
        return objectives;  // Returns the list of objectives
    }

    public void removeObjective(int x, int y) {
        objectives.removeIf(obj -> obj[0] == x && obj[1] == y);  // Remove the objective at the given coordinates
    }

    public boolean hasObjectives() {
        return !objectives.isEmpty();  // Returns true if there are still objectives left

    }
    public boolean isEmpty(int x, int y) {
        return !isFireAt(x, y) && !isSafeAt(x, y) && !isBarrierAt(x, y);
    }
    public boolean isObjectiveAt(int x, int y) {
        for (int[] objective : objectives) {
            if (objective[0] == x && objective[1] == y) {
                return true;
            }
        }
        return false;
    }

    public boolean isAgentAt(int x, int y) {
        for (Agent agent : agents) {
            if (agent.getX() == x && agent.getY() == y) {
                return true;
            }
        }
        return false;
    }
    public boolean isHumanAt(int x, int y) {
        return humanGrid[x][y];
    }
    public boolean isHumanAppeared() {
        return humanAppeared;
    }


    public int[] calculateMoveTowardsHuman(int currentX, int currentY) {
        if (!humanAppeared || humanPosition == null) {
            System.out.println("Aucun humain présent dans la grille.");
            return null;
        }

        int nextX = currentX;
        int nextY = currentY;

        // Calcul du déplacement vers l'humain
        if (Math.abs(humanPosition[0] - currentX) > Math.abs(humanPosition[1] - currentY)) {
            nextX += (humanPosition[0] > currentX) ? 1 : -1; // Déplacement vertical
        } else {
            nextY += (humanPosition[1] > currentY) ? 1 : -1; // Déplacement horizontal
        }

        // Retourne la prochaine position
        return new int[]{nextX, nextY};
    }


    }




