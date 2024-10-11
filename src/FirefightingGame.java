import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.application.Application;

public class FirefightingGame extends Application {
    private static final int GRID_SIZE = 20;  // Taille de la grille
    private static final int TILE_SIZE = 50;  // Taille d'une case

    private Grid cityGrid;
    private GridPane gridPane;

    @Override
    public void start(Stage primaryStage) {
        // Initialiser la grille du jeu
        cityGrid = new Grid(GRID_SIZE, GRID_SIZE);

        // Créer la grille graphique (interface)
        gridPane = new GridPane();
        updateGrid();  // Afficher la grille initiale

        // Ajouter la grille dans une scène
        Scene scene = new Scene(gridPane, GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE);

        // Configurer et afficher la fenêtre principale
        primaryStage.setTitle("Simulation des Pompiers");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Utiliser un Timeline pour gérer les mises à jour régulières sans threads
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            cityGrid.propagateFire();  // Propagation du feu dans la grille
            updateGrid();  // Mettre à jour l'interface graphique
        }));
        timeline.setCycleCount(5);  // Nombre de tours (par exemple, 5 tours)
        timeline.play();  // Démarrer l'animation
    }

    // Méthode pour mettre à jour l'affichage de la grille
    private void updateGrid() {
        gridPane.getChildren().clear();  // Effacer les anciennes cases
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                char state = cityGrid.getState(i, j);  // Récupérer l'état de la case
                Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);

                // Définir la couleur en fonction de l'état de la case
                switch (state) {
                    case 'F':  // Case en feu
                        rect.setFill(Color.RED);
                        break;
                    case 'B':  // Bâtiment
                        rect.setFill(Color.GRAY);
                        break;
                    default:  // Case vide
                        rect.setFill(Color.WHITE);
                        break;
                }
                rect.setStroke(Color.BLACK);  // Bordure noire pour chaque case
                gridPane.add(rect, j, i);  // Ajouter la case à la grille
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
