import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid {
    private final int gridSize;
    private final boolean[][] fireGrid;    // Grid for fire
    private final boolean[][] safeGrid;    // Grid for safe cells
    private final boolean[][] barrierGrid; // Grid for barriers
    private final List<int[]> objectives; // List of objectives
    private final Random random;

    private int winCaseX = -1, winCaseY = -1; // Coordinates of the win case
    private int[] humanPosition = null; // Coordinates of the human (if any)

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
        for (int i = 0; i < 20; i++) { // Example: Place 5 barriers
            int barrierX = random.nextInt(gridSize);
            int barrierY = random.nextInt(gridSize);
            barrierGrid[barrierX][barrierY] = true;
            System.out.println("Barrier placed at (" + barrierX + ", " + barrierY + ")");
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
            System.out.println("Objective placed at (" + objX + ", " + objY + ")");
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

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < gridSize && y >= 0 && y < gridSize;
    }

    public boolean isEmpty(int x, int y) {
        return !fireGrid[x][y] && !barrierGrid[x][y] && !safeGrid[x][y];
    }

    public List<int[]> getEmptyCells() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (!isFireAt(i, j) && !isBarrierAt(i, j) && !isSafeAt(i, j)) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        return emptyCells;
    }

    public List<int[]> getBarriers() {
        List<int[]> barriers = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (isBarrierAt(i, j)) {
                    barriers.add(new int[]{i, j});
                }
            }
        }
        return barriers;
    }

    // New method to check if a cell contains an objective
    public boolean isObjectiveAt(int x, int y) {
        for (int[] objective : objectives) {
            if (objective[0] == x && objective[1] == y) {
                return true;
            }
        }
        return false;
    }

    // Method to check if a cell is empty
    public boolean isCellEmpty(int x, int y) {
        return !isObjectiveAt(x, y) && !isBarrierAt(x, y) && !isFireAt(x, y) && !isSafeAt(x, y);
    }
