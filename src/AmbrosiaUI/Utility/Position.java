package AmbrosiaUI.Utility;

/**
 * Universally used for positions
 */
public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a distance to another position
     * @param pos The other position
     * @return distance
     */
    public double getDistanceTo(Position pos){
        double ac = Math.abs(pos.y - y);
        double cb = Math.abs(pos.x - x);

        return Math.hypot(ac, cb);
    }

    /**
     * Returns a new positions which x and y of are multiplied by the argument
     * @param mult Multiplier of x & y
     * @return New position
     */
    public Position getMultiplied(double mult){
        return new Position(
                (int) (x*mult),
                (int) (y*mult)
        );
    }

    /**
     * Returns a new positions which x and y of are divided by the argument
     * @param mult Divider
     * @return New position
     */
    public Position getDivided(double mult){
        return new Position(
                (int) (x/mult),
                (int) (y/mult)
        );
    }

    /**
     * @return A stringified version of the position
     */
    public String encode(){
        return x + "x" + y;
    }

    public Position getOffset(Position pos){
        return new Position(pos.x+x,pos.y+y);
    }

    /**
     * Returns a new position offset by the x & y
     * @param x
     * @param y
     * @return The offset position
     */
    public Position getOffset(int x, int y){
        return new Position(this.x+x,this.y+y);
    }

    public boolean inRectangle(Position position, Size size){
        return x > position.x && x < position.x + size.width &&
                y > position.y && y < position.y + size.height;
    }
    /**
     * Checks if a position is in a rectangle
     * @param rect The rectangle
     * @return true/false -> is/isn't
     */
    public boolean inRectangle(Rectangle rect){
        return this.inRectangle(rect.getPosition(),rect.getSize());
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
