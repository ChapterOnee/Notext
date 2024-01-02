package Utility;

public class EventStatus {
    private Position mousePosition = new Position(0,0);
    private boolean mouseDown = false;

    public EventStatus() {
    }

    public Position getMousePosition() {
        return mousePosition;
    }

    public void setMousePosition(Position mousePosition) {
        this.mousePosition = mousePosition;
    }

    public boolean isMouseDown() {
        return mouseDown;
    }

    public void setMouseDown(boolean mouseDown) {
        this.mouseDown = mouseDown;
    }
}
