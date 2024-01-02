package Widgets.Placements;

import Utility.Position;
import Utility.Size;
import Widgets.Widget;

public class PlacementCell {
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
}
