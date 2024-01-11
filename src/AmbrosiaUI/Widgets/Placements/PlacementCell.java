package AmbrosiaUI.Widgets.Placements;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Widgets.Widget;

public abstract class PlacementCell {
    protected Widget boundElement;

    protected Size lastCalculatedSize = new Size(0,0);
    protected Position lastCalculatedPosition = new Position(0,0);

    public Widget getBoundElement() {
        return boundElement;
    }

    public Size getLastCalculatedSize() {
        return lastCalculatedSize;
    }

    public Position getLastCalculatedPosition() {
        return lastCalculatedPosition;
    }

    public void setLastCalculatedSize(Size lastCalculatedSize) {
        this.lastCalculatedSize = lastCalculatedSize;
    }

    public void setLastCalculatedPosition(Position lastCalculatedPosition) {
        this.lastCalculatedPosition = lastCalculatedPosition;
    }
    public void setLastCalculatedPosition(int x, int y) {
        lastCalculatedPosition.x = x;
        lastCalculatedPosition.y = y;
    }
    public void setLastCalculatedSize(int width, int height) {
        lastCalculatedSize.width = width;
        lastCalculatedSize.height = height;
    }

}
