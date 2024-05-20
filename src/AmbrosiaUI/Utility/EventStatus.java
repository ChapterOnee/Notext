package AmbrosiaUI.Utility;

/**
 *  Status of events hold only mouse information, mostly deprecated in the latest versions
 */
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
