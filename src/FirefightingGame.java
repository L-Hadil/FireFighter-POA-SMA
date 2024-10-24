import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

import javax.swing.*;
import java.awt.*;

public class GameGrid extends JFrame {
    private static final int GRID_SIZE = 15; // Grid size
    public static final JPanel[][] gridPanels = new JPanel[GRID_SIZE][GRID_SIZE]; // Store each cell of the grid
    private JLabel scoreLabel1; // Label for FireMaker score
    private JLabel scoreLabel2; // Label for FireFighter score
    private FireMaker fireMaker; // FireMaker agent instance
    private FireFighter fireFighter; // FireFighter agent instance
    private int round = 1; // Round counter

    public GameGrid() {
        setTitle("Game Grid");
        setSize(1080, 920);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JPanel panel = new JPanel();
                int state = defineCellState(); // Define cell state
                setCellColor(panel, state); // Update cell color
                gridPanels[row][col] = panel; // Store panel
                gridPanel.add(panel); // Add panel to gridPanel
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new FlowLayout());

        scoreLabel1 = new JLabel("Score FireMaker: 0");
        scoreLabel2 = new JLabel("Score FireFighter: 0");
        scorePanel.add(scoreLabel1);
        scorePanel.add(scoreLabel2);

        add(scorePanel, BorderLayout.NORTH);
        setVisible(true);
    }

    // Method to define cell state randomly
    private int defineCellState() {
        double random = Math.random();
        if (random < 0.4) {
            return 1; // FOREST
        } else if (random < 0.8) {
            return 2; // BUILDING
        } else {
            return 0; // EMPTY
        }
    }

    // Method to set cell color based on its state
    private void setCellColor(JPanel panel, int state) {
        switch (state) {
            case 0: panel.setBackground(Color.LIGHT_GRAY); break; // Empty
            case 1: panel.setBackground(Color.GREEN); break; // Forest
            case 2: panel.setBackground(Color.DARK_GRAY); break; // Building
        }
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add borders
    }

    // Method to update scores
    public void updateScore(int fireMakerScore, int fireFighterScore) {
        scoreLabel1.setText("Score FireMaker: " + fireMakerScore);
        scoreLabel2.setText("Score FireFighter: " + fireFighterScore);
    }

    // Method to increase round counter
    public void nextRound() {
        round++;
        System.out.println("Round " + round + " starts!");
        // Update the scores in the GUI after each round
        updateScore(fireMaker.getScore(), fireFighter.getScore());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameGrid::new);
        jade.core.Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {
            // Create instances of FireMaker and FireFighter
            FireMaker fireMaker = new FireMaker(gridPanels, 4, 4);
            FireFighter fireFighter = new FireFighter(gridPanels, 9, 9); // Starting position for FireFighter
            mainContainer.acceptNewAgent("FireMaker", fireMaker).start();
            mainContainer.acceptNewAgent("FireFighter", fireFighter).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
