import java.util.List;
import java.util.Random;

public class FireAgent extends Agent {
    private final Grid grid;
    private final int objectivesCount; // Total number of objectives
    private int score; // Fire's score
    private final Random random;

    public FireAgent(int startX, int startY, Grid grid, int objectivesCount) {
        super(startX, startY);
        this.grid = grid;
        this.objectivesCount = objectivesCount;
        this.score = 0;
        this.random = new Random();
    }

    @Override
    public void move() {
        // Si des objectifs sont encore disponibles, se déplacer vers le plus proche
        if (!grid.getObjectives().isEmpty()) {
            moveTowardsNearestObjective();
        } else {
            // Si aucun objectif, déplacer aléatoirement ou propager le feu
            propagateFire();
        }
    }

    private void moveTowardsNearestObjective() {
        int[] nearestObjective = findNearestObjective();

        if (nearestObjective != null) {
            moveToTarget(nearestObjective[0], nearestObjective[1]);
        } else {
            propagateFire(); // Si aucun objectif n'est atteignable, propager le feu
        }
    }

    private int[] findNearestObjective() {
        List<int[]> objectives = grid.getObjectives();
        int[] nearestObjective = null;
        int minDistance = Integer.MAX_VALUE;

        for (int[] objective : objectives) {
            int distance = Math.abs(x - objective[0]) + Math.abs(y - objective[1]); // Distance Manhattan
            if (distance < minDistance) {
                minDistance = distance;
                nearestObjective = objective;
            }
        }

        return nearestObjective;
    }

    private void moveToTarget(int targetX, int targetY) {
        Direction direction = determineDirection(targetX, targetY);

        int nextX = x;
        int nextY = y;

        switch (direction) {
            case UP -> nextY = Math.max(0, y - 1);
            case DOWN -> nextY = Math.min(grid.getGridSize() - 1, y + 1);
            case LEFT -> nextX = Math.max(0, x - 1);
            case RIGHT -> nextX = Math.min(grid.getGridSize() - 1, x + 1);
        }

        if (canMoveTo(nextX, nextY)) {
            updatePosition(nextX, nextY);
        } else {
            System.out.println("FireAgent blocked at (" + x + ", " + y + "). Trying random move.");
            jumpToRandomEmptyCell();
        }
    }

    private Direction determineDirection(int targetX, int targetY) {
        if (x < targetX) return Direction.RIGHT;
        if (x > targetX) return Direction.LEFT;
        if (y < targetY) return Direction.DOWN;
        if (y > targetY) return Direction.UP;
        return null; // Target already reached
    }

    private boolean canMoveTo(int nextX, int nextY) {
        return grid.isInBounds(nextX, nextY) &&
                !grid.isSafeAt(nextX, nextY) &&
                !grid.isBarrierAt(nextX, nextY);
    }

    private void updatePosition(int nextX, int nextY) {
        x = nextX;
        y = nextY;
        grid.setFireAt(x, y, true);
        burnObjective(x, y);
        propagateFireToNeighbors();
    }

    private void propagateFire() {
        // Déplacement aléatoire si aucun objectif n'est présent
        jumpToRandomEmptyCell();
        propagateFireToNeighbors();
    }

    private void propagateFireToNeighbors() {
        int[][] neighbors = {
                {x - 1, y}, {x + 1, y}, {x, y - 1}, {x, y + 1}
        };

        for (int[] neighbor : neighbors) {
            int nx = neighbor[0];
            int ny = neighbor[1];

            if (grid.isInBounds(nx, ny) && isBurnableCell(nx, ny)) {
                grid.setFireAt(nx, ny, true);
                burnObjective(nx, ny);
                System.out.println("Fire propagated to (" + nx + ", " + ny + ")");
            }
        }
    }

    private boolean isBurnableCell(int x, int y) {
        return !grid.isSafeAt(x, y) && !grid.isBarrierAt(x, y) && !grid.isFireAt(x, y);
    }

    private void burnObjective(int x, int y) {
        List<int[]> objectives = grid.getObjectives();
        objectives.removeIf(obj -> {
            if (obj[0] == x && obj[1] == y && score < objectivesCount) {
                score++;
                System.out.println("Objective burned at (" + x + ", " + y + ")");
                return true;
            }
            return false;
        });
    }

    private void jumpToRandomEmptyCell() {
        int gridSize = grid.getGridSize();
        int attempts = 10;

        while (attempts > 0) {
            int randomX = random.nextInt(gridSize);
            int randomY = random.nextInt(gridSize);

            if (canMoveTo(randomX, randomY)) {
                updatePosition(randomX, randomY);
                System.out.println("FireAgent jumped to random cell at (" + randomX + ", " + randomY + ")");
                return;
            }

            attempts--;
        }

        System.out.println("FireAgent unable to find an empty cell to jump.");
    }

    public int getScore() {
        return score;
    }
}
