import java.util.List;

public class FireAgent extends Agent {
    private final Grid grid;
    private final int objectivesCount; // Total number of objectives
    private int score; // Fire's score

    public FireAgent(int startX, int startY, Grid grid, int objectivesCount) {
        super(startX, startY);
        this.grid = grid;
        this.objectivesCount = objectivesCount;
        this.score = 0;
    }

    @Override
    public void move() {
        moveFireTowardsNearestObjective();
    }

    private void moveFireTowardsNearestObjective() {
        if (grid.getObjectives().isEmpty()) return;


        int[] targetObjective = findNearestObjective();
        if (targetObjective == null) return;


        Direction fireDirection = determineDirectionTowardsObjective(targetObjective);
        int nextX = x, nextY = y;


        switch (fireDirection) {
            case UP -> nextY = Math.max(0, y - 1);
            case DOWN -> nextY = Math.min(grid.getGridSize() - 1, y + 1);
            case LEFT -> nextX = Math.max(0, x - 1);
            case RIGHT -> nextX = Math.min(grid.getGridSize() - 1, x + 1);
        }


        if (canMoveTo(nextX, nextY)) {
            updatePosition(nextX, nextY);
        } else {
            System.out.println("Fire blocked at position (" + x + ", " + y + ") by all directions.");
        }
    }

    private int[] findNearestObjective() {
        List<int[]> objectives = grid.getObjectives();
        int[] nearestObjective = null;
        int minDistance = Integer.MAX_VALUE;

        for (int[] objective : objectives) {
            int distance = Math.abs(x - objective[0]) + Math.abs(y - objective[1]); // Manhattan distance
            if (distance < minDistance) {
                minDistance = distance;
                nearestObjective = objective;
            }
        }

        return nearestObjective;
    }

    private Direction determineDirectionTowardsObjective(int[] target) {
        if (x < target[0]) return Direction.RIGHT;
        if (x > target[0]) return Direction.LEFT;
        if (y < target[1]) return Direction.DOWN;
        if (y > target[1]) return Direction.UP;
        return null; // Target reached
    }

    private boolean canMoveTo(int nextX, int nextY) {
        return !grid.isSafeAt(nextX, nextY) && !grid.isBarrierAt(nextX, nextY);
    }

    private void updatePosition(int nextX, int nextY) {
        x = nextX;
        y = nextY;
        grid.setFireAt(x, y, true);
        burnObjective();
        propagateFireToNeighbors();
    }

    private Direction determineDirectionTowardsObjectives() {
        List<int[]> objectives = grid.getObjectives();
        if (!objectives.isEmpty()) {
            int[] target = objectives.get(0);
            if (x < target[0]) return Direction.RIGHT;
            if (x > target[0]) return Direction.LEFT;
            if (y < target[1]) return Direction.DOWN;
            if (y > target[1]) return Direction.UP;
        }
        return Direction.values()[(int) (Math.random() * Direction.values().length)];
    }

    // Method to propagate fire to neighboring cells
    private void propagateFireToNeighbors() {
        int[][] neighbors = {{x - 1, y}, {x + 1, y}, {x, y - 1}, {x, y + 1}};

        for (int[] neighbor : neighbors) {
            int nx = neighbor[0];
            int ny = neighbor[1];

            if (nx >= 0 && nx < grid.getGridSize() && ny >= 0 && ny < grid.getGridSize()) {
                if (isBurnableCell(nx, ny)) {
                    grid.setFireAt(nx, ny, true);
                    System.out.println("Propagation du feu à la case (" + nx + ", " + ny + ")");
                    burnObjective(nx, ny);
                } else if (grid.isBarrierAt(nx, ny)) {
                    System.out.println("Propagation bloquée par une barrière à (" + nx + ", " + ny + ")");
                } else if (grid.isSafeAt(nx, ny)) {
                    System.out.println("Propagation bloquée par une case sécurisée à (" + nx + ", " + ny + ")");
                }
            }
        }
    }

    private boolean isBurnableCell(int x, int y) {
        return !grid.isSafeAt(x, y) && !grid.isBarrierAt(x, y);
    }

    private void burnObjective(int x, int y) {
        List<int[]> objectives = grid.getObjectives();
        objectives.removeIf(obj -> {
            if (obj[0] == x && obj[1] == y && score < objectivesCount) {
                score++;
                System.out.println("Objectif brûlé par propagation à la position (" + x + ", " + y + ")");
                return true;
            }
            return false;
        });
    }

    private void burnObjective() {
        burnObjective(this.x, this.y);
    }

    public int getScore() {
        return score;
    }
}
