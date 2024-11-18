import java.util.*;

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
        // Check if the starting cell is empty, if not reset position
        if (!isStartingCellEmpty(startX, startY)) {
            System.out.println("Starting cell (" + startX + ", " + startY + ") is not empty. Resetting position.");
            resetStartingPosition();  // Reset to a valid position
        }
    }



// Method to check if the starting cell is empty
private boolean isStartingCellEmpty(int x, int y) {
    return grid.isInBounds(x, y) && !grid.isFireAt(x, y) && !grid.isBarrierAt(x, y);
}

// Method to reset the starting position to a valid, empty cell
private void resetStartingPosition() {
    for (int i = 0; i < grid.getGridSize(); i++) {
        for (int j = 0; j < grid.getGridSize(); j++) {
            if (isStartingCellEmpty(i, j)) {
                // Update the agent's position to the new valid cell
                this.x = i;
                this.y = j;
                System.out.println("Position reset to: (" + i + ", " + j + ")");
                return; // Stop once a valid position is found
            }
        }
    }
    // If no valid position found, you can handle the situation here
    System.out.println("No valid position available! Please check the grid configuration.");
}
    @Override
    public void move() {
        if (objectives.isEmpty()) {
            return;
        }

        int[] targetObjective = findNearestObjective();
        if (targetObjective == null) {
            return;
        }

        List<int[]> path = findPathAStar(targetObjective);
        if (path != null && !path.isEmpty()) {
            int[] nextStep = path.get(0); // Get the next step in the path
            updatePosition(nextStep[0], nextStep[1]);
        }
    }

    private int[] findNearestObjective() {
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

    private List<int[]> findPathAStar(int[] target) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Set<Node> closedSet = new HashSet<>();
        Node startNode = new Node(x, y, null, 0, heuristic(x, y, target));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.x == target[0] && current.y == target[1]) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (int[] neighbor : getNeighbors(current.x, current.y)) {
                if (closedSet.contains(new Node(neighbor[0], neighbor[1], null, 0, 0))) {
                    continue; // Ignore already evaluated nodes
                }

                double tentativeG = current.g + movementCost(current.x, current.y, neighbor[0], neighbor[1]);

                Node neighborNode = new Node(neighbor[0], neighbor[1], current, tentativeG,
                        heuristic(neighbor[0], neighbor[1], target));

                if (openSet.stream().noneMatch(n -> n.equals(neighborNode) && tentativeG >= n.g)) {
                    openSet.add(neighborNode);
                }
            }
        }

        return null; // No path found
    }

    private List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node.parent != null) {
            path.add(0, new int[]{node.x, node.y}); // Add to the start of the list
            node = node.parent;
        }
        return path;
    }

    private List<int[]> getNeighbors(int cx, int cy) {
        List<int[]> neighbors = new ArrayList<>();
        int[][] directions = {
                {0, -1}, {0, 1}, {-1, 0}, {1, 0}, // Cardinal directions: UP, DOWN, LEFT, RIGHT
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonal directions
        };

        for (int[] dir : directions) {
            int nx = cx + dir[0];
            int ny = cy + dir[1];

            if (nx >= 0 && nx < grid.getGridSize() && ny >= 0 && ny < grid.getGridSize() &&
                    !grid.isBarrierAt(nx, ny)) { // Only consider non-barrier cells
                neighbors.add(new int[]{nx, ny});
            }
        }
        return neighbors;
    }

    private double movementCost(int x1, int y1, int x2, int y2) {
        return (x1 != x2 && y1 != y2) ? Math.sqrt(2) : 1.0; // Cost for diagonal movement vs straight
    }

    private double heuristic(int x, int y, int[] target) {
        // Euclidean distance as heuristic for diagonal movement
        return Math.sqrt(Math.pow(x - target[0], 2) + Math.pow(y - target[1], 2));
    }

    private void updatePosition(int nextX, int nextY) {
        x = nextX;
        y = nextY;

        if (grid.isFireAt(x, y)) {
            grid.setFireAt(x, y, false);
            System.out.println("Firefighter: Extinguished fire at (" + x + ", " + y + ")");
        }

        grid.setSafeAt(x, y);
        System.out.println("Firefighter: Secured cell at (" + x + ", " + y + ")");

        saveObjective();
    }

    private void saveObjective() {
        objectives.removeIf(obj -> {
            if (obj[0] == x && obj[1] == y && score < objectivesCount) {
                score++;
                return true;
            }
            return false;
        });
    }

    public int getScore() {
        return score;
    }

    private static class Node {
        int x, y;
        double g, f;
        Node parent;

        Node(int x, int y, Node parent, double g, double h) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.g = g;
            this.f = g + h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
