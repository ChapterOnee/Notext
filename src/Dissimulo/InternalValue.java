package Dissimulo;

/**
 * A value with a type
 */
public class InternalValue{
    public enum ValueType{
        INT,
        ID,
        BOOL,
        STRING,
        DOUBLE,
        OBJECT,
        FUNCTION,
        NONE,
        REFERENCE
    }

    private ValueType type;
    private String value;

    private boolean returned;

    public InternalValue(ValueType type, String value) {
        this.type = type;
        this.value = value;
    }

    public InternalValue(ValueType type, String value, boolean returned) {
        this.type = type;
        this.value = value;
        this.returned = returned;
    }

    public InternalValue(ValueType type) {
        this.type = type;
        this.value = switch (type){
            case INT -> "0";
            case DOUBLE -> "0.0";
            case BOOL -> "false";
            default -> "NONE";
        };
    }

    public InternalValue(ValueType type, boolean returned) {
        this.type = type;
        this.value = switch (type){
            case INT -> "0";
            case DOUBLE -> "0.0";
            case BOOL -> "false";
            default -> "NONE";
        };
        this.returned = returned;
    }

    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    @Override
    public String toString() {
        return value;
    }

    public String fullString(){
        return this.value + "(" + this.type + ")";
    }
}
