package AmbrosiaUI.Utility;

import AmbrosiaUI.Widgets.Widget;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitValue {
    private int value;
    private Unit unit;
    public enum Unit {
        FIT, // Fit to minimal size
        PIXELS, // Value in pixels
        FRACTION, // Percentage of parent size
        AUTO // Automatically set to fill remaining space
    }

    public enum Direction{
        VERTICAL,
        HORIZONTAL
    }
    public UnitValue(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }
    public UnitValue(int value, String unit) {
        this.value = value;
        this.unit = unitFromString(unit);
    }

    public UnitValue(Unit unit) {
        this.value = 0;
        this.unit = unit;
    }

    public UnitValue(String unit) {
        this.value = 0;
        this.unit = unitFromString(unit);
    }

    public Unit unitFromString(String unit){
        return switch (unit){
            case "fit" -> Unit.FIT;
            case "fraction" -> Unit.FRACTION;
            case "auto" -> Unit.AUTO;
            default -> Unit.PIXELS;
        };
    }

    public int toPixels(Size parent_size, Widget widget, Direction direction){
        return switch (this.unit){
            case PIXELS -> this.value;
            case FRACTION -> switch (direction){
                case VERTICAL -> (parent_size.height / 100) * this.value;
                case HORIZONTAL -> (parent_size.width / 100) * this.value;
            };
            case FIT -> switch (direction){
                case VERTICAL -> widget.getMinHeight();
                case HORIZONTAL -> widget.getMinWidth();
            };
            case AUTO -> 0;
        };
    }

    /*
        Template format:
            10px, 10%, 10px
     */
    public static ArrayList<UnitValue> valuesFromTemplateString(String template){
        ArrayList<UnitValue> output_values = new ArrayList<>();

        Pattern pat = Pattern.compile("(([0-9]+)(px|%))|(auto)");
        Matcher mat = pat.matcher(template);

        while(mat.find()){
            if(mat.group(1) == null){
                output_values.add(new UnitValue(0,Unit.AUTO));
                continue;
            }

            switch (mat.group(3)) {
                case "px" -> {
                    output_values.add(new UnitValue(Integer.parseInt(mat.group(2)), Unit.PIXELS));
                }
                case "%" -> {
                    output_values.add(new UnitValue(Integer.parseInt(mat.group(2)), Unit.FRACTION));
                }
            }
        }

        //System.out.println(output_values);
        return output_values;
    }

    @Override
    public String toString() {
        return switch (unit){
            case PIXELS -> value + "px";
            case FRACTION -> value + "%";
            case FIT -> "min";
            case AUTO -> "auto";
        };
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
