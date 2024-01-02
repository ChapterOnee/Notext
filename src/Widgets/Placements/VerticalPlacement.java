package Widgets.Placements;

import Utility.Position;
import Utility.Size;
import Utility.UnitValue;
import Widgets.Widget;
import Widgets.Theme;

public class VerticalPlacement extends Placement{
    private static class VerticalPlacementCell extends PlacementCell{
        UnitValue height;

        public VerticalPlacementCell(Widget bound_element, UnitValue height) {
            this.boundElement = bound_element;
            this.height = height;
        }


        public UnitValue getHeight() {
            return height;
        }
    }

    public VerticalPlacement(Theme theme) {
        this.rootPosition = new Position(0,0);
        this.rootSize = new Size(0,0);
        this.theme = theme;
    }

    @Override
    public void resize(Size new_size) {
        this.setRootSize(new_size);
        this.recalculate();
        super.resize(new_size);
    }

    public void add(Widget w, UnitValue unit){
        w.setPlacement(this);
        w.setTheme(this.theme);
        w.setPlacementIndex(this.children.size());
        this.children.add(new VerticalPlacementCell(w, unit));
        this.recalculate();
    }

    private void recalculate(){
        int filling = 0;
        int taken_up_scape = 0;
        VerticalPlacementCell cell;

        for(PlacementCell cel: children){
            cell = ((VerticalPlacementCell) cel);

            if(cell.getHeight().getUnit() == UnitValue.Unit.AUTO){
                filling += 1;
            }
            else{
                taken_up_scape += cell.getHeight().toPixels(this.getRootSize(), UnitValue.Direction.VERTICAL);
            }
        }

        int current_y = 0;
        for(PlacementCell cel: children){
            cell = ((VerticalPlacementCell) cel);
            cell.setLastCalculatedPosition(new Position(0,current_y));

            int calculated_height = cell.getHeight().toPixels(this.getRootSize(), UnitValue.Direction.VERTICAL);

            if(cell.getHeight().getUnit() == UnitValue.Unit.AUTO){
                calculated_height = (this.getRootSize().height-taken_up_scape)/filling;
            }

            cell.setLastCalculatedSize(new Size(this.getRootSize().width, calculated_height));

            //System.out.println(new Position(0,current_y) + " " + new Size(this.getRootSize().width, calculated_height));

            current_y += calculated_height;
        }
    }
}
