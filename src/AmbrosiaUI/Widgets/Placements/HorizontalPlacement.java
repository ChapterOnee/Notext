package AmbrosiaUI.Widgets.Placements;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.Widget;
import AmbrosiaUI.Widgets.Theme;

public class HorizontalPlacement extends Placement{
    private static class HorizontalPlacementCell extends PlacementCell{
        UnitValue width;

        public HorizontalPlacementCell(Widget bound_element, UnitValue width) {
            this.boundElement = bound_element;
            this.width = width;
        }


        public UnitValue getWidth() {
            return width;
        }
    }

    public HorizontalPlacement(Theme Theme) {
        this.rootPosition = new Position(0,0);
        this.rootSize = new Size(0,0);
        this.theme = Theme;
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
        this.children.add(new HorizontalPlacementCell(w, unit));
        this.recalculate();
    }

    private void recalculate(){
        int filling = 0;
        int taken_up_scape = 0;
        HorizontalPlacementCell cell;

        for(PlacementCell cel: children){
            cell = ((HorizontalPlacementCell) cel);

            if(cell.getWidth().getUnit() == UnitValue.Unit.AUTO){
                filling += 1;
            }
            else{
                taken_up_scape += cell.getWidth().toPixels(this.getRootSize(), UnitValue.Direction.HORIZONTAL);
            }
        }

        int current_x = 0;
        for(PlacementCell cel: children){
            cell = ((HorizontalPlacementCell) cel);
            cell.setLastCalculatedPosition(new Position(current_x ,0));

            int calculated_width = cell.getWidth().toPixels(this.getRootSize(), UnitValue.Direction.HORIZONTAL);

            if(cell.getWidth().getUnit() == UnitValue.Unit.AUTO){
                calculated_width = (this.getRootSize().width-taken_up_scape)/filling;
            }

            cell.setLastCalculatedSize(new Size(calculated_width,this.getRootSize().height));

            //System.out.println(new Position(0,current_x) + " " + new Size(this.getRootSize().width, calculated_width));

            current_x += calculated_width;
        }
    }
}

