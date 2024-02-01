package AegisLang;

import java.util.Arrays;
import java.util.HashMap;

public class InternalValue{
    public enum ValueType{
        INT,
        ID,
        BOOL,
        STRING,
        DOUBLE,
        OBJECT,
        FUNCTION,
        NONE
    }

    private ValueType type;
    private String value;

    private HashMap<String, InternalValue> subValues = new HashMap<>();
    private HashMap<String, InterpreterFunction> subFunctions = new HashMap<>();

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

    public String getName(){
        return this.value.split("\\.")[0];
    }

    protected String[] getPath(String pth){
        String[] result = pth.replace(getName() + ".", "").replace(getName(), "").strip().split("\\.");

        if(result[0].isEmpty()){
            return new String[0];
        }

        return result;
    }

    public boolean hasPath(){
        return getPath(this.value).length > 0;
    }

    public InternalValue resolvedSubValue(InterpreterContext context){
        String[] path = getPath(this.value);

        InternalValue current = this;
        for(String name: path){
            if(name.isEmpty()){
                break;
            }

            System.out.println(subValues);

            if(current.hasSubVariable(name)){
                current = current.getSubVariable(name);
            }
            else{
                System.out.println("Variable has no property in: " + name);
                return new InternalValue(ValueType.NONE);
            }
        }

        if(current == this){
            current = context.getVariable(this.getName());
        }

        return current;
    }

    public void setResolved(String pth, InternalValue value){
        String[] path = getPath(pth);
        System.out.println(Arrays.toString(path));

        InternalValue current = this;

        int i = 0;
        for(String name: path){
            if(current.hasSubVariable(name)){
                current = current.getSubVariable(name);
            }
            else{
                if(i == path.length-1){
                    current.setSubVariable(name, value);
                    return;
                }

                System.out.println("Variable has no property in: " + name);
                return;
            }
            i++;
        }

        current.setValue(value.getValue());
        current.setType(value.getType());
    }

    protected void setSubVariable(String name, InternalValue value){
        subValues.put(name,value);
    }

    protected InternalValue getSubVariable(String name){
        return this.subValues.get(name);
    }
    protected boolean hasSubVariable(String name){
        return this.subValues.containsKey(name);
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
}
