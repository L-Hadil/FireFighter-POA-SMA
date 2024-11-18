

public abstract class Agent {
    protected int x;
    protected int y;

    protected Grid grid;

    public Agent(int startX, int startY, Grid grid) {
        this.x = startX;
        this.y = startY;
        this.grid = grid;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public abstract void move();









    protected boolean checkAndHandleHuman(Grid grid) {
        if (grid.isHumanAppeared() && grid.shouldNotifyAgents()) {
            System.out.println(this.getClass().getSimpleName() + " detected human appearance!");
            return true; // Force la priorité à l'humain
        }
        return false;
    }


// Méthode abstraite à implémenter dans les sous-classes



}
