import java.util.List;
import java.util.*;

public class FireAgent extends Agent {
    private final Grid grid;
    private final int objectivesCount; // Total number of objectives
    private int score; // Fire's score
    private int humanX, humanY; // Position of the human
    private boolean humanAppeared = false; // Flag to check if the human appeared

    public FireAgent(int startX, int startY, Grid grid, int objectivesCount) {
        super(startX, startY);
        this.grid = grid;
        this.objectivesCount = objectivesCount;
        this.score = 0;
    }

    @Override
    public void move() {
        if (humanAppeared) {
            moveFireTowardsHuman(); // Prioritize moving towards the human
        } else {
            moveFireTowardsNearestObjective(); // Otherwise, move towards the nearest objective
        }
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
            System.out.println("Fire blocked at position (" + x + ", " + y + "). Jumping to a random cell.");
            jumpToRandomCell();
        }
    }

    private void moveFireTowardsHuman() {
        // If human is within 2 cells, move towards it
        int distanceToHuman = Math.abs(humanX - x) + Math.abs(humanY - y);
        if (distanceToHuman <= 5) {
            Direction fireDirection = determineDirectionTowardsHuman();
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
                System.out.println("Fire blocked at position (" + x + ", " + y + "). Jumping to a random cell.");
                jumpToRandomCell();
            }
        } else {
            // If the human is not within range, proceed with the regular objective movement
            moveFireTowardsNearestObjective();
        }
    }

    private Direction determineDirectionTowardsHuman() {
        if (x < humanX) return Direction.RIGHT;
        if (x > humanX) return Direction.LEFT;
        if (y < humanY) return Direction.DOWN;
        if (y > humanY) return Direction.UP;
        return null; // Human reached
    }

    private int[] findNearestObjective() {
        List<int[]> objectives = grid.getObjectives();

        // If no objectives, return null
        if (objectives.isEmpty()) return null;

        // Directions for moving in 4 cardinal directions (up, down, left, right)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // Queue to hold (x, y) positions for BFS
        Queue<int[]> queue = new LinkedList<>();
        // Visited array to keep track of visited cells
        boolean[][] visited = new boolean[grid.getGridSize()][grid.getGridSize()];

        // Start BFS from the FireAgent's position
        queue.add(new int[]{x, y});
        visited[x][y] = true;

        // BFS loop
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentX = current[0];
            int currentY = current[1];

            // Check if this cell is an objective
            for (int[] objective : objectives) {
                if (objective[0] == currentX && objective[1] == currentY) {
                    System.out.println("Found nearest objective at (" + currentX + ", " + currentY + ")");
                    return new int[]{currentX, currentY};
                }
            }

            // Explore neighbors
            for (int[] dir : directions) {
                int nx = currentX + dir[0];
                int ny = currentY + dir[1];

                // Check if the neighbor is within bounds, not visited, and not a barrier/safe zone
                if (nx >= 0 && nx < grid.getGridSize() && ny >= 0 && ny < grid.getGridSize() &&
                        !visited[nx][ny] && !grid.isSafeAt(nx, ny) && !grid.isBarrierAt(nx, ny)) {
                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny});
                }
            }
        }

        // If no objective is reachable, return null
        System.out.println("No reachable objective found.");
        return null;
    }

    private boolean canMoveTo(int nextX, int nextY) {
        return grid.isInBounds(nextX, nextY) && !grid.isSafeAt(nextX, nextY) && !grid.isBarrierAt(nextX, nextY);
    }

    private void updatePosition(int nextX, int nextY) {
        x = nextX;
        y = nextY;
        grid.setFireAt(x, y, true);
        burnObjective();
        propagateFireToNeighbors();
    }

    private void jumpToRandomCell() {
        List<int[]> emptyCells = grid.getEmptyCells();
        if (emptyCells.isEmpty()) {
            System.out.println("No empty cells available for jumping.");
            return;
        }

        int[] randomCell = emptyCells.get((int) (Math.random() * emptyCells.size()));
        updatePosition(randomCell[0], randomCell[1]);
    }

    private void propagateFireToNeighbors() {
        int[][] neighbors = {{x - 1, y}, {x + 1, y}, {x, y - 1}, {x, y + 1}};

        for (int[] neighbor : neighbors) {
            int nx = neighbor[0];
            int ny = neighbor[1];

            if (grid.isInBounds(nx, ny) && isBurnableCell(nx, ny)) {
                grid.setFireAt(nx, ny, true);
                System.out.println("Fire propagated to cell (" + nx + ", " + ny + ")");
                burnObjective(nx, ny);
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
                System.out.println("Objective burned by propagation at (" + x + ", " + y + ")");
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

    public void setHumanPosition(int humanX, int humanY) {
        this.humanX = humanX;
        this.humanY = humanY;
        this.humanAppeared = true;
    }


    private Direction determineDirectionTowardsObjective(int[] target) {
        if (x < target[0]) return Direction.RIGHT;
        if (x > target[0]) return Direction.LEFT;
        if (y < target[1]) return Direction.DOWN;
        if (y > target[1]) return Direction.UP;
        return null; // Target reached
    }
}
