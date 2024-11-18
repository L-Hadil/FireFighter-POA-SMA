import java.util.List;

public class FireAgent extends Agent {
    private final Grid grid;
    private final int objectivesCount;
    private Direction fireDirection;
    private int score;

    public FireAgent(int startX, int startY, Grid grid, int objectivesCount) {
        super(startX, startY);
        this.grid = grid;
        this.objectivesCount = objectivesCount;
        this.fireDirection = Direction.UP;
        this.score = 0;
    }

    @Override
    public void move() {
        moveFireTowardsObjective();
    }

    private void moveFireTowardsObjective() {
        if (grid.getObjectives().isEmpty()) return;

        int[] targetObjective = grid.getObjectives().get(0);
        fireDirection = determineDirectionTowardsObjectives();

        int nextX = x, nextY = y;
        boolean moved = false;


        switch (fireDirection) {
            case UP -> nextY = Math.max(0, y - 1);
            case DOWN -> nextY = Math.min(grid.getGridSize() - 1, y + 1);
            case LEFT -> nextX = Math.max(0, x - 1);
            case RIGHT -> nextX = Math.min(grid.getGridSize() - 1, x + 1);
        }

        if (canMoveTo(nextX, nextY)) {
            updatePosition(nextX, nextY);
            moved = true;
        } else {

            for (Direction dir : Direction.values()) {
                nextX = x;
                nextY = y;

                switch (dir) {
                    case UP -> nextY = Math.max(0, y - 1);
                    case DOWN -> nextY = Math.min(grid.getGridSize() - 1, y + 1);
                    case LEFT -> nextX = Math.max(0, x - 1);
                    case RIGHT -> nextX = Math.min(grid.getGridSize() - 1, x + 1);
                }

                if (canMoveTo(nextX, nextY)) {
                    updatePosition(nextX, nextY);
                    moved = true;
                    break;
                }
            }

            if (!moved) {
                System.out.println("Feu bloqué à la position (" + x + ", " + y + ") par toutes les directions.");
            }
        }
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
