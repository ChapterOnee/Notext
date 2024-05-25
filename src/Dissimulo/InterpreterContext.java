package Dissimulo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A context that saves variable and objects under set names
 */
public class InterpreterContext {
    private HashMap<String, InterpreterFunction> functions = new HashMap<>();
    private HashMap<String, InternalValue> variables = new HashMap<>();
    protected InterpreterContext parentContext;

    private boolean modifiable = true;

    public InterpreterContext() {

    }

    public InterpreterContext(boolean modifiable) {
        this.modifiable = modifiable;
    }

    public boolean isModifiable() {
        return modifiable;
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

    public void setFunction(String name, InterpreterFunction function){
        for(InterpreterContext parent: getAllParents()){
            if(parent.hasFunction(name)){
                parent.functions.put(name, function);
                return;
            }
        }

        this.functions.put(name, function);
    }

    protected ArrayList<InterpreterContext> getAllParents(){
        ArrayList<InterpreterContext> out = new ArrayList<>();

        if(this.parentContext != null){
            out.addAll(this.parentContext.getAllParents());
            out.add(this.parentContext);
        }

        return out;
    }

    public InterpreterContext getHighestModifiableParent(){
        InterpreterContext current = this;

        while(current.parentContext != null && current.parentContext.modifiable){
            current = current.parentContext;
        }

        return current;
    }

    public HashMap<String, InterpreterFunction> getFunctions() {
        return functions;
    }

    public HashMap<String, InternalValue> getVariables() {
        return variables;
    }

    public InterpreterContext getParentContext() {
        return parentContext;
    }

    public InterpreterContext getModifiableParentContext(){
        if(this.parentContext != null && this.parentContext.modifiable){
            return this.parentContext;
        }
        return this;
    }
    @Override
    public String toString() {
        return "InterpreterContext{" +
                "variables=" + variables +
                ", parentContext=" + parentContext +
                '}';
    }
}
