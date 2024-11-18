import java.util.*;

public class FireAgent extends Agent {
    private final Grid grid;
    private final int objectivesCount; // Total number of objectives
    private int score; // Fire's score

    public FireAgent(int startX, int startY, Grid grid, int objectivesCount) {
        super(startX, startY, grid); // Passe Grid au parent
        this.grid = grid;
        this.objectivesCount = objectivesCount;
        this.score = 0;
    }


    @Override
    public void move() {
        // Si l'humain est apparu, prioriser l'humain immédiatement
        if (grid.isHumanAppeared()) {
            System.out.println("FireAgent prioritizing the human.");
            moveFireTowardsHuman(); // Déplacement exclusif vers l'humain
            return; // Évite tout autre comportement
        }

        // Sinon, continuer avec le comportement habituel
        moveFireTowardsNearestObjective();
    }




    private void moveFireTowardsHuman() {
        int[] humanPosition = grid.getHumanPosition(); // Récupère la position de l'humain
        if (humanPosition == null) return; // Sécurité si la position de l'humain n'est pas définie

        // Trouver le chemin vers l'humain
        List<int[]> path = findPathAStar(humanPosition);
        if (path != null && !path.isEmpty()) {
            int[] nextStep = path.get(0); // Étape suivante vers l'humain
            updatePosition(nextStep[0], nextStep[1]);

            // Vérifie si l'agent feu atteint l'humain
            if (x == humanPosition[0] && y == humanPosition[1]) {
                System.out.println("FireAgent reached the human! FireAgent wins!");
                grid.setWinner("FireAgent");
            }
        }
    }

    private void moveFireTowardsNearestObjective() {
        if (grid.getObjectives().isEmpty()) return;

        int[] targetObjective = findNearestObjective();
        if (targetObjective == null) return;

        // Trouver le chemin vers l'objectif le plus proche
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

    private Direction determineDirectionTowardsObjective(int[] target) {
        if (x < target[0]) return Direction.RIGHT;
        if (x > target[0]) return Direction.LEFT;
        if (y < target[1]) return Direction.DOWN;
        if (y > target[1]) return Direction.UP;
        return null; // Target reached
    }

    private int[] findNearestObjective() {
        List<int[]> objectives = grid.getObjectives();
        if (objectives.isEmpty()) return null;

        int[] nearestObjective = null;
        int minDistance = Integer.MAX_VALUE;

        for (int[] objective : objectives) {
            int distance = Math.abs(x - objective[0]) + Math.abs(y - objective[1]);
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
                    continue; // Ignore les noeuds déjà évalués
                }

                double tentativeG = current.g + movementCost(current.x, current.y, neighbor[0], neighbor[1]);

                Node neighborNode = new Node(neighbor[0], neighbor[1], current, tentativeG,
                        heuristic(neighbor[0], neighbor[1], target));

                if (openSet.stream().noneMatch(n -> n.equals(neighborNode) && tentativeG >= n.g)) {
                    openSet.add(neighborNode);
                }
            }
        }

        return null; // Aucun chemin trouvé
    }

    private List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node.parent != null) {
            path.add(0, new int[]{node.x, node.y}); // Ajoute au début de la liste
            node = node.parent;
        }
        return path;
    }

    private List<int[]> getNeighbors(int cx, int cy) {
        List<int[]> neighbors = new ArrayList<>();
        int[][] directions = {
                {0, -1}, {0, 1}, {-1, 0}, {1, 0}, // Directions cardinales: HAUT, BAS, GAUCHE, DROITE
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Directions diagonales
        };

        for (int[] dir : directions) {
            int nx = cx + dir[0];
            int ny = cy + dir[1];

            if (nx >= 0 && nx < grid.getGridSize() && ny >= 0 && ny < grid.getGridSize() &&
                    !grid.isBarrierAt(nx, ny)) { // Ne considère que les cellules non-barrières
                neighbors.add(new int[]{nx, ny});
            }
        }
        return neighbors;
    }

    private double movementCost(int x1, int y1, int x2, int y2) {
        return (x1 != x2 && y1 != y2) ? Math.sqrt(2) : 1.0; // Coût pour les déplacements diagonaux ou droits
    }

    private double heuristic(int x, int y, int[] target) {
        return Math.sqrt(Math.pow(x - target[0], 2) + Math.pow(y - target[1], 2)); // Distance Euclidienne
    }

    private void updatePosition(int nextX, int nextY) {
        x = nextX;
        y = nextY;
        grid.setFireAt(x, y, true);

        // Vérifie si la case atteinte est un objectif
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

    private boolean canMoveTo(int nextX, int nextY) {
        return grid.isInBounds(nextX, nextY) && !grid.isSafeAt(nextX, nextY) && !grid.isBarrierAt(nextX, nextY);
    }

    private void burnObjective() {
        burnObjective(this.x, this.y);
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

    private void propagateFireToNeighbors() {
        int[][] neighbors = {{x - 1, y}, {x + 1, y}, {x, y - 1}, {x, y + 1}};

        for (int[] neighbor : neighbors) {
            int nx = neighbor[0];
            int ny = neighbor[1];

            if (grid.isInBounds(nx, ny) && isBurnableCell(nx, ny)) {
                grid.setFireAt(nx, ny, true);
                burnObjective(nx, ny);
            }
        }
    }

    private boolean isBurnableCell(int x, int y) {
        return !grid.isSafeAt(x, y) && !grid.isBarrierAt(x, y);
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
