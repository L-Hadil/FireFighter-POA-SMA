import java.util.Random;

public class Grid {
    private char[][] grid;
    private int rows;
    private int cols;
    private Random random = new Random();

    // Constructeur pour initialiser la grille avec des dimensions données
    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new char[rows][cols];

        // Initialisation de la grille avec des cases vides
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';  // '.' représente une case vide
            }
        }

        // Ajouter des feux et des bâtiments de manière aléatoire
        addRandomFires(5);  // Ajouter 5 feux au départ
        addRandomBuildings(3);  // Ajouter 3 bâtiments
    }

    // Méthode pour récupérer l'état d'une case spécifique
    public char getState(int row, int col) {
        return grid[row][col];
    }

    // Méthode pour ajouter des feux aléatoires dans la grille
    public void addRandomFires(int numberOfFires) {
        for (int i = 0; i < numberOfFires; i++) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);
            grid[x][y] = 'F';  // 'F' pour feu
        }
    }

    // Méthode pour ajouter des bâtiments aléatoires dans la grille
    public void addRandomBuildings(int numberOfBuildings) {
        for (int i = 0; i < numberOfBuildings; i++) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);
            grid[x][y] = 'B';  // 'B' pour bâtiment
        }
    }

    // Méthode pour propager le feu
    public void propagateFire() {
        char[][] newGrid = new char[rows][cols];  // Une copie temporaire de la grille pour mettre à jour
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newGrid[i][j] = grid[i][j];  // Copier l'état actuel
            }
        }

        // Parcourir la grille et propager le feu aux cases adjacentes
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'F') {  // Si la case est en feu
                    spreadFire(i - 1, j, newGrid);  // Haut
                    spreadFire(i + 1, j, newGrid);  // Bas
                    spreadFire(i, j - 1, newGrid);  // Gauche
                    spreadFire(i, j + 1, newGrid);  // Droite
                }
            }
        }

        // Mettre à jour la grille avec les nouveaux feux
        grid = newGrid;
    }

    // Méthode pour propager le feu sur une case spécifique
    private void spreadFire(int x, int y, char[][] newGrid) {
        if (x >= 0 && x < rows && y >= 0 && y < cols) {
            if (grid[x][y] == '.' || grid[x][y] == 'B') {  // Le feu peut se propager sur une case vide ou un bâtiment
                newGrid[x][y] = 'F';  // Mettre le feu à cette case
            }
        }
    }
}
