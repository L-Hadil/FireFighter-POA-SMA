public abstract class Agent {
    protected int x;
    protected int y;

    public Agent(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }



    public abstract void move();
    public abstract void moveHuman(int humanX, int humanY);
}
