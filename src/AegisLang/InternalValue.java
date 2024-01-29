package AegisLang;

public class InternalValue{
    public enum ValueType{
        INT,
        STRING,
        DOUBLE,
        OBJECT,
        FUNCTION,
        NONE
    }

    private ValueType type;
    private String value;

    public InternalValue(ValueType type, String value) {
        this.type = type;
        this.value = value;
    }

    public InternalValue(ValueType type) {
        this.type = type;
        this.value = switch (type){
            case INT -> "0";
            case STRING, FUNCTION, OBJECT -> "";
            case NONE -> "";
            case DOUBLE -> "0.0";
        };
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
}
