package Utility;

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
