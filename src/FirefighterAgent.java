import java.util.List;

public class FirefighterAgent extends Agent {
    private final Grid grid;
    private final int objectivesCount; // Nombre d'objectifs total
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

            return;
        }

        int[] targetObjective = objectives.get(0); // Choisir le premier objectif restant
        Direction direction = determineDirection(targetObjective[0], targetObjective[1]);

        int nextX = x;
        int nextY = y;
        switch (direction) {
            case UP -> nextY = Math.max(0, y - 1);
            case DOWN -> nextY = Math.min(grid.getGridSize() - 1, y + 1);
            case LEFT -> nextX = Math.max(0, x - 1);
            case RIGHT -> nextX = Math.min(grid.getGridSize() - 1, x + 1);
        }



        updatePosition(nextX, nextY);
    }

    private void updatePosition(int nextX, int nextY) {
        x = nextX;
        y = nextY;

        // Éteindre le feu si le pompier est sur une case en feu
        if (grid.isFireAt(x, y)) {
            grid.setFireAt(x, y, false);
            System.out.println("Pompier : Feu éteint à la position (" + x + ", " + y + ")");
        }

        // Marquer la case comme sécurisée pour empêcher le feu de revenir dessus
        grid.setSafeAt(x, y);
        System.out.println("Pompier : Case sécurisée à la position (" + x + ", " + y + ")");

        // Sauver l'objectif s'il est atteint
        saveObjective();
    }



    private Direction determineDirection(int targetX, int targetY) {
        if (x < targetX) return Direction.RIGHT;
        if (x > targetX) return Direction.LEFT;
        if (y < targetY) return Direction.DOWN;
        return Direction.UP;
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
}
