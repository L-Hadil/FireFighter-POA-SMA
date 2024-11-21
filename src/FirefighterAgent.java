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
        // Si l'humain est apparu, prioriser l'humain immédiatement
        if (grid.isHumanAppeared()) {
            System.out.println("FireFighterAgent prioritizing the human.");
            moveTowardsHuman(); // Déplacement exclusif vers l'humain
            return; // Évite tout autre comportement
        }

        // Sinon, continuer avec le comportement habituel
        moveTowardsNearestObjective();
    }
    public void moveTowardsNearestObjective() {
        // Trouver l'objectif le plus proche (en utilisant la distance Manhattan)
        int[] closestObjective = findClosestObjective();
        if (closestObjective == null) {
            System.out.println("Aucun objectif disponible.");
            return;
        }

        // Déterminer la direction vers l'objectif le plus proche
        int nextX = x;
        int nextY = y;

        if (Math.abs(closestObjective[0] - x) > Math.abs(closestObjective[1] - y)) {
            nextX += (closestObjective[0] > x) ? 1 : -1;
        } else {
            nextY += (closestObjective[1] > y) ? 1 : -1;
        }

        // Vérifier si le mouvement est valide
        if (canMoveTo(nextX, nextY)) {
            updatePosition(nextX, nextY);
            System.out.println("FireFighterAgent moved towards nearest objective: (" + nextX + ", " + nextY + ")");
        } else {
            System.out.println("Movement blocked towards nearest objective at (" + nextX + ", " + nextY + ").");
        }
    }

    public void moveTowardsHuman() {
        int[] nextMove = grid.calculateMoveTowardsHuman(x, y);

        if (nextMove == null) {
            System.out.println("Déplacement impossible : Aucun humain détecté.");
            return;
        }

        int nextX = nextMove[0];
        int nextY = nextMove[1];

        if (canMoveTo(nextX, nextY)) {
            updatePosition(nextX, nextY);
            System.out.println("FireFighterAgent moved towards human: (" + nextX + ", " + nextY + ")");
        } else {
            System.out.println("FireFighterAgent blocked at (" + nextX + ", " + nextY + ").");
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
