package Widgets.Placements;

import Utility.Size;
import Utility.Position;
import Widgets.Widget;

public class AbsolutePlacement extends Placement {
    private static class AbsolutePlacementCell extends PlacementCell {

        private final Position position;
        private final Size size;
        public AbsolutePlacementCell(Widget bound_element, Position position, Size size) {
            this.boundElement = bound_element;
            this.position = position;
            this.size = size;
        }

        public Position getPosition() {
            return position;
        }

        public Size getSize() {
            return size;
        }
    }

    public AbsolutePlacement(Position root_position) {
        this.rootPosition = root_position;
    }
    public void add(Widget w, Position pos, Size size){
        w.setPlacement(this);
        w.setTheme(this.theme);
        w.setPlacementIndex(this.children.size());
        this.children.add(new AbsolutePlacementCell(w, pos, size));
    }

    public Position getPosition(int index){
        return ((AbsolutePlacementCell) this.children.get(index)).getPosition().getOffset(this.getRootPosition());
    }

    @Override
    public int getWidth(int index) {
        return ((AbsolutePlacementCell) this.children.get(index)).getSize().width;
    }

    @Override
    public int getHeight(int index) {
        return ((AbsolutePlacementCell) this.children.get(index)).getSize().height;
    }

}
