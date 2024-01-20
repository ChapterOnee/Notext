package AmbrosiaUI.Widgets.Placements;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.Widget;
import AmbrosiaUI.Widgets.Theme;

import java.util.ArrayList;
import java.util.Collections;

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

    private int columns = 1;
    private int minColumnWidth = -1;

    public VerticalPlacement(Theme theme) {
        this.rootPosition = new Position(0,0);
        this.rootSize = new Size(0,0);
        this.theme = theme;
        this.itemMargin = 0;
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
        w.setParent(this.parent);
        this.children.add(new VerticalPlacementCell(w, unit));
    }

    public int getMinimalHeight(){
        ArrayList<Integer> column_heights = new ArrayList<>();
        for(int i = 0;i < columns;i++){
            column_heights.add(0);
        }

        VerticalPlacementCell cell;
        int temp;
        for(int i = 0;i < children.size();i++){
            int column = i%columns;

            cell = ((VerticalPlacementCell) children.get(i));

            if(cell.getHeight().getUnit() != UnitValue.Unit.AUTO){
                temp = cell.getHeight().toPixels(this.getRootSize(), cell.getBoundElement(), UnitValue.Direction.VERTICAL);
                column_heights.set(column, column_heights.get(column)+temp);
            }
        }

        Collections.sort(column_heights);

        return column_heights.get(columns-1);
    }
    private void recalculate(){
        if(minColumnWidth > 0){
            columns = Math.max(this.getRootSize().width/minColumnWidth,1);
        }

        int filling = 0;
        int taken_up_scape = 0;
        VerticalPlacementCell cell;

        for(PlacementCell cel: children){
            cell = ((VerticalPlacementCell) cel);

            if(cell.getHeight().getUnit() == UnitValue.Unit.AUTO){
                filling += 1;
            }
            else{
                taken_up_scape += cell.getHeight().toPixels(this.getRootSize(), cell.getBoundElement(), UnitValue.Direction.VERTICAL);
            }
        }

        int column_width = this.getRootSize().width / columns;

        ArrayList<Integer> column_heights = new ArrayList<>();
        for(int i = 0;i < columns;i++){
            column_heights.add(0);
        }

        for(int i = 0;i < children.size();i++){
            int column = i%columns;

            if (column > column_heights.size()){
                break;
            }

            int current_y = column_heights.get(column);

            //System.out.println(current_y);

            cell = ((VerticalPlacementCell) children.get(i));
            cell.setLastCalculatedPosition(new Position(itemMargin+column_width*column,current_y+itemMargin));

            int calculated_height = cell.getHeight().toPixels(this.getRootSize(), cell.getBoundElement(),UnitValue.Direction.VERTICAL);

            if(cell.getHeight().getUnit() == UnitValue.Unit.AUTO){
                calculated_height = (this.getRootSize().height-taken_up_scape)/filling;
            }

            cell.setLastCalculatedSize(new Size(column_width-itemMargin*2, calculated_height- itemMargin *2));

            //System.out.println(new Position(0,current_y) + " " + new Size(this.getRootSize().width, calculated_height));

            column_heights.set(column, column_heights.get(column)+calculated_height);
        }
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getMinColumnWidth() {
        return minColumnWidth;
    }

    public void setMinColumnWidth(int minColumnWidth) {
        this.minColumnWidth = minColumnWidth;
    }
}
