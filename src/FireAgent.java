import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
            jumpToRandomEmptyCell();
        }
    }
    private int[] findNearestObjective() {
        List<int[]> objectives = grid.getObjectives();
        if (objectives.isEmpty()) return null;

        int gridSize = grid.getGridSize();
        boolean[][] visited = new boolean[gridSize][gridSize];
        Queue<int[]> queue = new LinkedList<>();

        // Start BFS from the current position of the fire agent
        queue.offer(new int[]{x, y, 0}); // {currentX, currentY, distance}
        visited[x][y] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int curX = current[0];
            int curY = current[1];
            int distance = current[2];

            // Check if this cell is an objective
            for (int[] objective : objectives) {
                if (curX == objective[0] && curY == objective[1]) {
                    return new int[]{curX, curY};
                }
            }

            // Explore neighbors (up, down, left, right)
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : directions) {
                int newX = curX + dir[0];
                int newY = curY + dir[1];

                if (isValidCell(newX, newY, visited)) {
                    queue.offer(new int[]{newX, newY, distance + 1});
                    visited[newX][newY] = true;
                }
            }
        }

       
        return null;
    }

   
    private boolean isValidCell(int x, int y, boolean[][] visited) {
        return x >= 0 && x < grid.getGridSize() &&
                y >= 0 && y < grid.getGridSize() &&
                !visited[x][y] &&
                !grid.isBarrierAt(x, y); // Ensure cell is not a barrier
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

        if(grid.isHumanAt(x,y)){
            System.out.println("L'humain a été brûlé à la position (" + x + ", " + y + ")");
            score=score+10;
        }

    }

    private void burnObjective() {
        burnObjective(this.x, this.y);
    }

    public int getScore() {
        return score;
    }

    private void jumpToRandomEmptyCell() {
        int gridSize = grid.getGridSize();
        int randomX, randomY;

        // Try a number of random positions until an empty cell is found
        for (int i = 0; i < 10; i++) {  // Limit the number of attempts to avoid infinite loops
            randomX = random.nextInt(gridSize);
            randomY = random.nextInt(gridSize);

            if (canMoveTo(randomX, randomY)) {
                updatePosition(randomX, randomY);
                System.out.println("Fire jumped to random empty cell at (" + randomX + ", " + randomY + ")");
                return;
            }
        }

        System.out.println("Unable to jump to a random empty cell after 10 attempts.");
    }

}
