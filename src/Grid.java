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

    public Grid(int size, int objectivesCount) {
        this.gridSize = size;
        this.fireGrid = new boolean[size][size];
        this.safeGrid = new boolean[size][size];
        this.barrierGrid = new boolean[size][size];
        this.objectives = new ArrayList<>();
        this.random = new Random();

        initializeBarriers();
        initializeObjectives(objectivesCount);
    }

    public int getGridSize() {
        return gridSize; // Return the grid size
    }

    private void initializeBarriers() {
        for (int i = 0; i < 5; i++) { // Exemple pour placer 5 barrières
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
        return safeGrid[x][y];
    }

    public void setSafeAt(int x, int y) {
        safeGrid[x][y] = true;
    }

    public boolean isBarrierAt(int x, int y) {
        return barrierGrid[x][y];
    }

    public List<int[]> getObjectives() {
        return objectives;
    }

    public void removeObjective(int x, int y) {
        objectives.removeIf(obj -> obj[0] == x && obj[1] == y);
    }

    public boolean hasObjectives() {
        return !objectives.isEmpty();
    }
}
