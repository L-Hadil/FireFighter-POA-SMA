import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import javax.swing.*;
import java.awt.Color;

public class FireFighter extends Agent {
    private int x; // Position X
    private int y; // Position Y
    private int score; // Score of the agent
    private JPanel[][] gridPanels; // Reference to the grid

    public FireFighter(JPanel[][] gridPanels, int startX, int startY) {
        this.gridPanels = gridPanels;
        this.x = startX;
        this.y = startY;
        this.score = 0; // Initialize score to 0
    }

    @Override
    protected void setup() {
        System.out.println("FireFighter started at position: (" + x + ", " + y + ")");
        addBehaviour(new FireFighterBehaviour());
    }

    private class FireFighterBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            System.out.println("FireFighter is performing an action...");
            performAction(); // Call the method to propagate the fire
            block(5000); // Delay for 1 second before the next action
        }
    }

    public void moveRandomly() {
        int direction = (int) (Math.random() * 4) + 1; // Generates 1, 2, 3, or 4
        switch (direction) {
            case 1: moveUp(); break;
            case 2: moveDown(); break;
            case 3: moveRight(); break;
            case 4: moveLeft(); break;
        }
    }

    // Movement in each direction
    public void moveUp() {
        if (x > 0) updatePosition(x - 1, y); // Move up
    }

    public void moveDown() {
        if (x < gridPanels.length - 1) updatePosition(x + 1, y); // Move down
    }

    public void moveRight() {
        if (y < gridPanels[0].length - 1) updatePosition(x, y + 1); // Move right
    }

    public void moveLeft() {
        if (y > 0) updatePosition(x, y - 1); // Move left
    }


    private void updatePosition(int newX, int newY) {
        Color currentCellColor = gridPanels[newX][newY].getBackground();

        if (currentCellColor == Color.GREEN || currentCellColor == Color.DARK_GRAY) {
            gridPanels[x][y].setBackground(new Color(0, 0, 139)); // Dark blue for firefighter presence
            x = newX;
            y = newY;
            gridPanels[x][y].setBackground(Color.BLUE);

            // Update score based on cell type
            if (currentCellColor == Color.GREEN) {
                score += 1; // +1 for forest
            } else if (currentCellColor == Color.DARK_GRAY) {
                score += 2; // +2 for building
            }
        } else if (currentCellColor == new Color(139, 0, 0)) {
            score += 3; // +3 for burned cell
            System.out.println("Firefighter moved to a burned cell and earned +3 points!");
        } else {
            System.out.println("Cannot move to an empty cell.");
        }
    }

    public void performAction() {
        moveRandomly();
        System.out.println("FireFighter moved to: (" + x + "," + y + ")");
        GameGrid gameGrid = (GameGrid) SwingUtilities.getAncestorOfClass(GameGrid.class, gridPanels[x][y]);
        if (gameGrid != null) {
            gameGrid.updateScore(0, score); // Update GUI with FireFighter's score
        }
    }

    public int getScore() {
        return score;
    }
}
