import java.util.List;

public class FirefighterAgent extends Agent {
    private final Grid grid;
    private final int objectivesCount;
    private List<int[]> objectives;
    private int score;

    public FirefighterAgent(int startX, int startY, Grid grid, int objectivesCount) {
        super(startX, startY);
        this.grid = grid;
        this.objectives = grid.getObjectives();
        this.objectivesCount = objectivesCount;
        this.score = 0;
    }

    @Override
    public void move() {
        // Vérifie si l'humain est apparu
        if (grid.isHumanAppeared()) {
            System.out.println("FirefighterAgent prioritizing the human.");
            moveTowardsHuman(); // Déplace vers l'humain si détecté
            return;
        }

        // Sinon, suit le comportement habituel pour atteindre les objectifs
        moveTowardsNearestObjective();
    }

    private void moveTowardsHuman() {
        int[] humanPosition = grid.getHumanPosition(); // Récupère la position de l'humain
        if (humanPosition == null) return;

        moveToTarget(humanPosition[0], humanPosition[1]);

        // Vérifie si l'agent atteint l'humain
        if (x == humanPosition[0] && y == humanPosition[1]) {
            System.out.println("FirefighterAgent reached the human! FirefighterAgent wins!");
            grid.setWinner("FirefighterAgent");
        }
    }

    private void moveTowardsNearestObjective() {
        if (objectives.isEmpty()) {
            return;
        }

        int[] targetObjective = findNearestObjective();
        if (targetObjective != null) {
            moveToTarget(targetObjective[0], targetObjective[1]);
        }
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
            case UP_LEFT -> {
                nextX = Math.max(0, x - 1);
                nextY = Math.max(0, y - 1);
            }
            case UP_RIGHT -> {
                nextX = Math.min(grid.getGridSize() - 1, x + 1);
                nextY = Math.max(0, y - 1);
            }
            case DOWN_LEFT -> {
                nextX = Math.max(0, x - 1);
                nextY = Math.min(grid.getGridSize() - 1, y + 1);
            }
            case DOWN_RIGHT -> {
                nextX = Math.min(grid.getGridSize() - 1, x + 1);
                nextY = Math.min(grid.getGridSize() - 1, y + 1);
            }
        }

        if (canMoveTo(nextX, nextY)) {
            updatePosition(nextX, nextY);
        } else {
            System.out.println("FirefighterAgent blocked at position (" + x + ", " + y + ").");
        }
    }

    private Direction determineDirection(int targetX, int targetY) {
        if (x < targetX && y < targetY) return Direction.DOWN_RIGHT;
        if (x < targetX && y > targetY) return Direction.UP_RIGHT;
        if (x > targetX && y < targetY) return Direction.DOWN_LEFT;
        if (x > targetX && y > targetY) return Direction.UP_LEFT;
        if (x < targetX) return Direction.RIGHT;
        if (x > targetX) return Direction.LEFT;
        if (y < targetY) return Direction.DOWN;
        return Direction.UP;
    }

    private int[] findNearestObjective() {
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

    private void updatePosition(int nextX, int nextY) {
        x = nextX;
        y = nextY;

        if (grid.isFireAt(x, y)) {
            grid.setFireAt(x, y, false);
            System.out.println("FirefighterAgent extinguished fire at (" + x + ", " + y + ")");
        }

        grid.setSafeAt(x, y);
        System.out.println("FirefighterAgent secured cell at (" + x + ", " + y + ")");

        saveObjective();
    }

    private void saveObjective() {
        objectives.removeIf(obj -> {
            if (obj[0] == x && obj[1] == y && score < objectivesCount) {
                score++;
                System.out.println("FirefighterAgent saved objective at (" + x + ", " + y + ")");
                return true;
            }
            return false;
        });
    }

    private boolean canMoveTo(int nextX, int nextY) {
        return true;
    }

    public int getScore() {
        return score;
    }
}
