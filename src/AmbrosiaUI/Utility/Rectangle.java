package AmbrosiaUI.Utility;

/**
 * A class for work with rectangles, no I won't use the java one
 */
public class Rectangle {
    private int x;
    private int y;
    private int width;
    private int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle(Position position, int width, int height){
        this.x = position.x;
        this.y = position.y;
        this.width = width;
        this.height = height;
    }
    public Rectangle(Position position, Size size){
        this.x = position.x;
        this.y = position.y;
        this.width = size.width;
        this.height = size.height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Position getPosition(){
        return new Position(x,y);
    }
    public Size getSize(){
        return new Size(width,height);
    }

    /**
     * Reduces the size of the rectangle by a given amount
     * @param margin The given amount
     * @return this
     */
    public Rectangle applyMargin(int margin){
        this.x += margin;
        this.y += margin;
        this.width -= 2*margin;
        this.height -= 2*margin;

        return this;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
