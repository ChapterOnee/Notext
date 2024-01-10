package Utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitValue {
    private int value;
    private Unit unit;
    public enum Unit {
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

    public int toPixels(Size parent_size, Direction direction){
        return switch (this.unit){
            case PIXELS -> this.value;
            case FRACTION -> switch (direction){
                case VERTICAL -> (parent_size.height / 100) * this.value;
                case HORIZONTAL -> (parent_size.width / 100) * this.value;
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
