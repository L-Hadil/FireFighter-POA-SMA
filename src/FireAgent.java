import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import java.util.Random;

public class FireAgent extends Agent {
    private static final int GRID_SIZE = 10;
    private Grid grid;
    private int fireX, fireY;
    private Random random = new Random();
    private int score = 0;

    public FireAgent(Grid grid) {
        this.grid = grid;


        fireX = random.nextInt(GRID_SIZE);
        fireY = random.nextInt(GRID_SIZE);
        while (grid.getState(fireX, fireY) != 'B') {  // Ensure it's a building
            fireX = random.nextInt(GRID_SIZE);
            fireY = random.nextInt(GRID_SIZE);
        }

        grid.setState(fireX, fireY, 'F');  // Set initial fire position
        System.out.println("FireAgent starting at: (" + fireX + ", " + fireY + ")");
    }

    public int getScore() {
        return score;  // Method to get the current score
    }

    @Override
    protected void setup() {
        // Adding a behaviour to handle movement
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {

            }
        });
    }

    // Move the fire agent up
    public void moveUp() {
        if (fireX > 0) {
            attemptFire(fireX - 1, fireY);
        }
    }

    // Move the fire agent down
    public void moveDown() {
        if (fireX < GRID_SIZE - 1) {
            attemptFire(fireX + 1, fireY);
        }
    }

    // Move the fire agent left
    public void moveLeft() {
        if (fireY > 0) {
            attemptFire(fireX, fireY - 1);
        }
    }

    // Move the fire agent right
    public void moveRight() {
        if (fireY < GRID_SIZE - 1) {
            attemptFire(fireX, fireY + 1);
        }
    }

    // Attempt to set fire to a building
    private void attemptFire(int newX, int newY) {
        // If the new position is a building
        if (grid.getState(newX, newY) == 'B') {
            grid.setState(newX, newY, 'R');
            score++;
            System.out.println("Fire set at: (" + newX + ", " + newY + ") | Score: " + score);
        }

        // Move freely on red and empty cells
        if (grid.getState(newX, newY) == 'R') {
            grid.setState(fireX, fireY, 'R');
            fireX = newX;
            fireY = newY;
            grid.setState(fireX, fireY, 'F');
        } else if (grid.getState(newX, newY) == '.') {
            grid.setState(fireX, fireY, '.');
            fireX = newX;
            fireY = newY;
            grid.setState(fireX, fireY, 'F');
        }

    }

}
