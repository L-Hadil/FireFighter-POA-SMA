import java.util.*;

public class FirefighterAgent extends Agent {
    private final Grid grid;
    private final int objectivesCount;
    private List<int[]> objectives;
    private int score;

    public FirefighterAgent(int startX, int startY, Grid grid, int objectivesCount) {
        super(startX, startY, grid); // Passe Grid au parent
        this.grid = grid;
        this.objectives = grid.getObjectives();
        this.objectivesCount = objectivesCount;
        this.score = 0;
        // Réinitialise si la position initiale est invalide
        if (!isStartingCellEmpty(startX, startY)) {
            resetStartingPosition();
        }
    }


    // Méthode pour vérifier si la cellule de départ est vide
    private boolean isStartingCellEmpty(int x, int y) {
        return grid.isInBounds(x, y) && !grid.isFireAt(x, y) && !grid.isBarrierAt(x, y);
    }

    // Méthode pour réinitialiser la position de départ à une cellule valide et vide
    private void resetStartingPosition() {
        for (int i = 0; i < grid.getGridSize(); i++) {
            for (int j = 0; j < grid.getGridSize(); j++) {
                if (isStartingCellEmpty(i, j)) {
                    // Met à jour la position de l'agent avec une nouvelle cellule valide
                    this.x = i;
                    this.y = j;
                    System.out.println("Position reset to: (" + i + ", " + j + ")");
                    return; // Stop dès qu'une position valide est trouvée
                }
            }
        }
        // Si aucune position valide n'est trouvée, gérer cette situation ici
        System.out.println("No valid position available! Please check the grid configuration.");
    }
    @Override
    public void move() {
        // Si l'humain est apparu, prioriser l'humain immédiatement
        if (grid.isHumanAppeared()) {
            System.out.println("FirefighterAgent prioritizing the human.");
            moveTowardsHuman(); // Déplacement exclusif vers l'humain
            return; // Évite tout autre comportement
        }

        // Sinon, continuer avec le comportement habituel
        moveTowardsNearestObjective();
    }






    private void moveTowardsHuman() {
        int[] humanPosition = grid.getHumanPosition(); // Récupère la position de l'humain
        if (humanPosition == null) return; // Sécurité si la position de l'humain n'est pas définie

        // Trouver le chemin vers l'humain
        List<int[]> path = findPathAStar(humanPosition);
        if (path != null && !path.isEmpty()) {
            int[] nextStep = path.get(0); // Étape suivante vers l'humain
            updatePosition(nextStep[0], nextStep[1]);

            // Vérifie si l'agent pompier atteint l'humain
            if (x == humanPosition[0] && y == humanPosition[1]) {
                System.out.println("FirefighterAgent reached the human! FirefighterAgent wins!");
                grid.setWinner("FirefighterAgent");
            }
        }
    }

    private void moveTowardsNearestObjective() {
        if (objectives.isEmpty()) {
            return;
        }

        int[] targetObjective = findNearestObjective();
        if (targetObjective == null) {
            return;
        }

        // Trouver le chemin vers l'objectif le plus proche
        List<int[]> path = findPathAStar(targetObjective);
        if (path != null && !path.isEmpty()) {
            int[] nextStep = path.get(0); // Étape suivante vers l'objectif
            updatePosition(nextStep[0], nextStep[1]);
        }
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
