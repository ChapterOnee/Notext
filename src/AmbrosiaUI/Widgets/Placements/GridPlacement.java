package AmbrosiaUI.Widgets.Placements;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.Theme;
import AmbrosiaUI.Widgets.Widget;

import java.util.ArrayList;

public class GridPlacement extends Placement{
    private ArrayList<UnitValue> rowTemplate = new ArrayList<>();
    private ArrayList<UnitValue> columnTemplate = new ArrayList<>();

    private ArrayList<Rectangle> calculatedRows = new ArrayList<>();
    private ArrayList<Rectangle> calculatedColumns = new ArrayList<>();

    private class GridPlacementCell extends PlacementCell{
        private final int column;
        private final int row;
        private final int columnSpan;
        private final int rowSpan;

        public GridPlacementCell(Widget boundElement, int row, int column, int rowSpan, int columnSpan) {
            this.boundElement = boundElement;
            this.column = column;
            this.row = row;
            this.columnSpan = columnSpan;
            this.rowSpan = rowSpan;
        }

        public int getColumn() {
            return column;
        }

        public int getRow() {
            return row;
        }

        public int getColumnSpan() {
            return columnSpan;
        }

        public int getRowSpan() {
            return rowSpan;
        }
    }

    public GridPlacement(Theme Theme) {
        this.rootPosition = new Position(0,0);
        this.rootSize = new Size(0,0);
        this.theme = Theme;
    }

    public void setRowTemplateFromString(String template){
        rowTemplate = UnitValue.valuesFromTemplateString(template);
    }
    public void setColumnTemplateFromString(String template){
        columnTemplate = UnitValue.valuesFromTemplateString(template);
    }

    public void recalculateRowAndColumnSizes() {
        calculatedColumns.clear();
        calculatedRows.clear();

        int filling = 0;
        int taken_up_scape = 0;


        //
        //  Calculate rows
        //
        for(UnitValue value: rowTemplate){
            if(value.getUnit() == UnitValue.Unit.AUTO){
                filling += 1;
            }
            taken_up_scape += value.toPixels(this.getRootSize(), null, UnitValue.Direction.VERTICAL);
        }

        int current_y = 0;
        for(UnitValue value: rowTemplate){
            int calculatedHeight = value.toPixels(this.getRootSize(), null, UnitValue.Direction.VERTICAL);

            if(value.getUnit() == UnitValue.Unit.AUTO){
                calculatedHeight = (this.getRootSize().height-taken_up_scape)/filling;
            }

            calculatedRows.add(new Rectangle(0,current_y,0,calculatedHeight));
            current_y += calculatedHeight;
        }

        filling = 0;
        taken_up_scape = 0;

        //
        //  Calculate columns
        //
        for(UnitValue value: columnTemplate){
            if(value.getUnit() == UnitValue.Unit.AUTO){
                filling += 1;
            }
            taken_up_scape += value.toPixels(this.getRootSize(), null, UnitValue.Direction.HORIZONTAL);
        }

        int current_x = 0;
        for(UnitValue value: columnTemplate){
            int calculatedWidth = value.toPixels(this.getRootSize(),null, UnitValue.Direction.HORIZONTAL);

            if(value.getUnit() == UnitValue.Unit.AUTO){
                calculatedWidth = (this.getRootSize().width-taken_up_scape)/filling;
            }

            calculatedColumns.add(new Rectangle(current_x,0,calculatedWidth,0));
            current_x += calculatedWidth;
        }

    }

    @Override
    public void resize(Size new_size) {
        this.setRootSize(new_size);
        this.recalculate();
        super.resize(new_size);
    }

    public void add(Widget w, int row, int column, int rowspan, int columnspan){
        w.setPlacement(this);
        w.setTheme(this.theme);
        w.setPlacementIndex(this.children.size());
        w.setParent(this.parent);
        this.children.add(new GridPlacementCell(w, row, column, rowspan, columnspan));
        this.recalculate();
    }

    private void recalculate(){
        this.recalculateRowAndColumnSizes();

        GridPlacementCell cell;

        for(PlacementCell cel: children){
            cell = (GridPlacementCell) cel;

            int width = 0;
            int height = 0;

            for(int i = cell.getRow();i < cell.getRow()+cell.getRowSpan();i++){
                height += calculatedRows.get(i).getHeight();
            }
            for(int i = cell.getColumn();i < cell.getColumn()+cell.getColumnSpan();i++){
                width += calculatedColumns.get(i).getWidth();
            }

            cell.setLastCalculatedPosition(
                    calculatedColumns.get(cell.getColumn()).getX(),
                    calculatedRows.get(cell.getRow()).getY()
            );
            cell.setLastCalculatedSize(
                    width,
                    height
            );
        }
    }
}
