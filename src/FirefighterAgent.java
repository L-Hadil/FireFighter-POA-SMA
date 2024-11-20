import java.util.List;

public class FirefighterAgent extends Agent {
    private final Grid grid;
    private final int objectivesCount; // Total number of objectives
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
        if (objectives.isEmpty()) {
            return; // No objectives left to save
        }

        // Find the closest objective using Manhattan distance
        int[] closestObjective = findClosestObjective();

        // Determine the direction to move towards the closest objective
        Direction direction = determineDirection(closestObjective[0], closestObjective[1]);

        int nextX = x;
        int nextY = y;

        // Try moving in random directions until a valid move is found
        boolean moved = false;
        while (!moved) {
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

            // Check if the new position is valid and not blocked
            if (canMoveTo(nextX, nextY)) {
                updatePosition(nextX, nextY);
                moved = true; // Successfully moved, break the loop
            } else {
                // If blocked, choose a new random direction to try again
                direction = Direction.values()[(int) (Math.random() * Direction.values().length)];
                System.out.println("Firefighter blocked at position (" + x + ", " + y + "). Trying a new direction...");
            }
        }
    }

    private int[] findClosestObjective() {
        int[] closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (int[] obj : objectives) {
            int distance = Math.abs(x - obj[0]) + Math.abs(y - obj[1]); // Manhattan distance
            if (distance < minDistance) {
                minDistance = distance;
                closest = obj;
            }
        }

        return closest; // Nearest objective
    }

    private void updatePosition(int nextX, int nextY) {
        x = nextX;
        y = nextY;

        // Extinguish fire if the firefighter is on a fire cell
        if (grid.isFireAt(x, y)) {
            grid.setFireAt(x, y, false);
            System.out.println("Firefighter: Fire extinguished at position (" + x + ", " + y + ")");
        }

        // Mark the cell as safe
        grid.setSafeAt(x, y);
        System.out.println("Firefighter: Position secured at (" + x + ", " + y + ")");

        // Save the objective if reached
        saveObjective();
    }

    private void saveObjective() {
        objectives.removeIf(obj -> {
            if (obj[0] == x && obj[1] == y && score < objectivesCount) {
                score++;
                System.out.println("Firefighter: Objective saved at position (" + x + ", " + y + ")");
                return true;
            }
            return false;
        });
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

    private boolean canMoveTo(int nextX, int nextY) {
        return (!grid.isAgentAt(nextX, nextY)&&!grid.isBarrierAt(nextX,nextY)); // Ensure the cell isn't occupied by another agent
    }

    public int getScore() {
        return score;
    }
}
