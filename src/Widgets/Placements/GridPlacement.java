package Widgets.Placements;

import Utility.UnitValue;
import Widgets.Widget;

import java.util.ArrayList;

public class GridPlacement extends Placement{
    private ArrayList<UnitValue> rowTemplate;
    private ArrayList<UnitValue> columnTemplate;

    private class GridPlacementCell extends PlacementCell{
        private final int column;
        private final int row;
        private final int columnSpan;
        private final int rowSpan;

        public GridPlacementCell(Widget boundElement, int column, int row, int columnSpan, int rowSpan) {
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

    public void setRowTemplateFromString(String template){
        rowTemplate = UnitValue.valuesFromTemplateString(template);
    }
    public void setColumnTemplateFromString(String template){
        columnTemplate = UnitValue.valuesFromTemplateString(template);
    }
}
