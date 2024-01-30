package AegisLang;

import java.util.ArrayList;
import java.util.HashMap;

public class InterpreterContext {
    private final HashMap<String, InterpreterFunction> functions = new HashMap<>();
    private final HashMap<String, InternalValue> variables = new HashMap<>();

    protected InterpreterContext parentContext;

    public InterpreterContext() {

    }

    public InterpreterContext(InterpreterContext parentContext) {
        this.parentContext = parentContext;
    }

    public void addFunction(String name, InterpreterFunction function){
        functions.put(name,function);
    }
    public void addVariable(String name, InternalValue value){
        variables.put(name, value);
    }

    public boolean hasVariable(String name){
        for(InterpreterContext parent: getAllParents()){
            if(parent.hasVariable(name)){
                return true;
            }
        }
        return this.variables.containsKey(name);
    }

    public InternalValue getVariable(String name){
        for(InterpreterContext parent: getAllParents()){
            if(parent.hasVariable(name)){
                return parent.variables.get(name);
            }
        }

        if(this.variables.containsKey(name)){
            return this.variables.get(name);
        }

        return new InternalValue(InternalValue.ValueType.NONE);
    }
    public void setVariable(String name, InternalValue value){
        for(InterpreterContext parent: getAllParents()){
            if(parent.hasVariable(name)){
                parent.variables.put(name, value);
                return;
            }
        }

        this.variables.put(name, value);
    }

    public boolean hasFunction(String name){
        for(InterpreterContext parent: getAllParents()){
            if(parent.hasFunction(name)){
                return true;
            }
        }
        return this.functions.containsKey(name);
    }

    public InterpreterFunction getFunction(String name){
        for(InterpreterContext parent: getAllParents()){
            if(parent.hasFunction(name)){
                return parent.functions.get(name);
            }
        }

        if(this.functions.containsKey(name)){
            return this.functions.get(name);
        }

        return null;
    }

    protected ArrayList<InterpreterContext> getAllParents(){
        ArrayList<InterpreterContext> out = new ArrayList<>();

        if(this.parentContext != null){
            out.addAll(this.parentContext.getAllParents());
            out.add(this.parentContext);
        }

        return out;
    }

    @Override
    public String toString() {
        return "InterpreterContext{" +
                "variables=" + variables +
                ", parentContext=" + parentContext +
                '}';
    }
}
