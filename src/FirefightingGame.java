import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;  // Import for displaying score
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.geometry.Pos;

public class FirefightingGame extends Application {
    private static final int GRID_SIZE = 10;  // Size of the grid (10x10)
    private static final int TILE_SIZE = 60;  // Size of each tile in pixels
    private Grid cityGrid;
    private GridPane gridPane;
    private FireAgent fireAgent;  // Fire agent reference
    private Label scoreLabel;  // Label to display the score

    @Override
    public void start(Stage primaryStage) {
        // Initialize the game grid
        cityGrid = new Grid(GRID_SIZE, GRID_SIZE);

        // Initialize the fire agent
        fireAgent = new FireAgent(cityGrid);


        gridPane = new GridPane();
        updateGrid();


        Button upButton = new Button("Up");
        Button downButton = new Button("Down");
        Button leftButton = new Button("Left");
        Button rightButton = new Button("Right");


        upButton.setOnAction(e -> {
            fireAgent.moveUp();
            updateGrid();
            updateScore();
        });

        downButton.setOnAction(e -> {
            fireAgent.moveDown();
            updateGrid();
            updateScore();
        });

        leftButton.setOnAction(e -> {
            fireAgent.moveLeft();
            updateGrid();
            updateScore();
        });

        rightButton.setOnAction(e -> {
            fireAgent.moveRight();
            updateGrid();
            updateScore();
        });


        HBox controlButtons = new HBox(10, upButton, downButton, leftButton, rightButton);
        controlButtons.setAlignment(Pos.CENTER);


        BorderPane layout = new BorderPane();
        layout.setCenter(gridPane);
        layout.setBottom(controlButtons);


        scoreLabel = new Label("Score: 0");
        layout.setTop(scoreLabel);


        Scene scene = new Scene(layout, GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE + 100);


        primaryStage.setTitle("Firefighting Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void updateGrid() {
        gridPane.getChildren().clear();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                char state = cityGrid.getState(i, j);
                Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);


                switch (state) {
                    case 'F':  // Fire
                        rect.setFill(Color.RED);
                        break;
                    case 'R':
                        rect.setFill(Color.DARKRED);
                        break;
                    case 'B':  // Building
                        rect.setFill(Color.GRAY);
                        break;
                    default:  // Empty
                        rect.setFill(Color.WHITE);
                        break;
                }
                rect.setStroke(Color.BLACK);
                gridPane.add(rect, j, i);
            }
        }
    }

    // Method to update the score display
    private void updateScore() {
        scoreLabel.setText("Score: " + fireAgent.getScore());
    }
    private void buttonAction() {
        fireAgent.moveRight();
        updateGrid();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
