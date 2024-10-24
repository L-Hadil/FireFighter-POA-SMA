import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import javax.swing.*;
import java.awt.Color;

public class FireMaker extends Agent {
    private int x; // Position X
    private int y; // Position Y
    private int score; // Score of the agent
    private JPanel[][] gridPanels; // Reference to the grid

    public FireMaker(JPanel[][] gridPanels, int startX, int startY) {
        this.gridPanels = gridPanels;
        this.x = startX;
        this.y = startY;
        this.score = 0; // Initialize score to 0
    }

    @Override
    protected void setup() {
        System.out.println("FireMaker started at position: (" + x + ", " + y + ")");
        addBehaviour(new FireMakerBehaviour());
    }

    private class FireMakerBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            System.out.println("FireMaker is performing an action...");
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
            gridPanels[x][y].setBackground(new Color(139, 0, 0)); // Dark red for fire propagation
            x = newX;
            y = newY;
            gridPanels[x][y].setBackground(Color.RED);

            // Update score based on cell type
            if (currentCellColor == Color.GREEN) {
                score += 1; // +1 for forest
            } else if (currentCellColor == Color.DARK_GRAY) {
                score += 2; // +2 for building
            }
        } else {
            System.out.println("Cannot move to an empty cell.");
        }
    }

    public void performAction() {
        moveRandomly();
        System.out.println("FireMaker moved to: (" + x + "," + y + ")");
        GameGrid gameGrid = (GameGrid) SwingUtilities.getAncestorOfClass(GameGrid.class, gridPanels[x][y]);
        if (gameGrid != null) {
            gameGrid.updateScore(score, 0); // Update GUI with FireMaker's score
        }
    }

    public int getScore() {
        return score;
    }
}


/*
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import javax.swing.*;
import java.awt.Color;

public class FireMaker extends Agent {
    private int x; // Position X de l'agent sur la grille
    private int y; // Position Y de l'agent sur la grille
    private JPanel[][] gridPanels; // Référence à la grille
    private Color[][] originalColors; // Store original colors of cells
    private int score; // Score de l'agent

    public FireMaker(JPanel[][] gridPanels, int startX, int startY) {
        this.gridPanels = gridPanels; // Référence à la grille
        this.x = startX;
        this.y = startY;
        this.originalColors = new Color[gridPanels.length][gridPanels[0].length];
        this.score = 0; // Initialize score

        // Initialize the original colors
        for (int i = 0; i < gridPanels.length; i++) {
            for (int j = 0; j < gridPanels[0].length; j++) {
                originalColors[i][j] = gridPanels[i][j].getBackground();
            }
        }
    }

    @Override
    protected void setup() {
        System.out.println("Agent FireMaker démarré à la position: (" + x + ", " + y + ")");
        addBehaviour(new FireMakerBehaviour());
    }

    private class FireMakerBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            System.out.println("FireMaker is performing an action...");
            performAction(); // Call the method to move and potentially set fire
            block(1000); // Delay for 1 second before the next action
            System.out.println("IM HERE...");
        }
    }

    // Random movement using Math.random()
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

    // Method to update the agent's position and handle fire propagation
    private void updatePosition(int newX, int newY) {
        Color currentCellColor = gridPanels[newX][newY].getBackground();

        // Revert previous position to its original color
        gridPanels[x][y].setBackground(originalColors[x][y]);

        // Update the agent's position
        x = newX;
        y = newY;

        // Handle fire propagation and scoring
        if (currentCellColor == Color.GREEN) { // Forest
            gridPanels[x][y].setBackground(new Color(139, 0, 0)); // Dark red for fire
            score += 1; // +1 point for setting fire in the forest
            System.out.println("Fire set in forest! Score: " + score);
        } else if (currentCellColor == Color.DARK_GRAY) { // Building
            gridPanels[x][y].setBackground(new Color(139, 0, 0)); // Dark red for fire
            score += 2; // +2 points for setting fire in the building
            System.out.println("Fire set in building! Score: " + score);
        } else if (currentCellColor == new Color(139,0,0)) {
            score += 0; // +2 points for setting fire in the building
            System.out.println("Fire set in building! Score: " + score);
        } else {
            // If it's an empty cell, set it to red (indicating the fire agent is there)
            gridPanels[x][y].setBackground(Color.RED);
        }
    }

    // This method will be called to perform the agent's action in each step
    public void performAction() {
        // Move the agent randomly (calls one of the directional methods)
        moveRandomly();
        System.out.println("Fire agent moved to: (" + x + "," + y + ")");
    }
}

 */
