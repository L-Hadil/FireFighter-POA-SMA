import java.util.Random;

public class Grid {
    private char[][] grid;
    private int rows;
    private int cols;
    private Random random = new Random();

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new char[rows][cols];


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';  // '.' représente une case vide
            }
        }
        addRandomBuildings(35);
    }


    public char getState(int row, int col) {
        return grid[row][col];
    }


    public void setState(int row, int col, char state) {
        grid[row][col] = state;
    }

    public void addRandomBuildings(int numberOfBuildings) {
        for (int i = 0; i < numberOfBuildings; i++) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);
            grid[x][y] = 'B';  // 'B' pour bâtiment
        }
    }


}