package AmbrosiaUI.Utility;

/**
 * Border settings
 */
public class GraphicsBorderModifier {
    private boolean top;
    private boolean right;
    private boolean bottom;
    private boolean left;

    public GraphicsBorderModifier(boolean top, boolean right, boolean bottom, boolean left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isBottom() {
        return bottom;
    }

    public void setBottom(boolean bottom) {
        this.bottom = bottom;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }
}
