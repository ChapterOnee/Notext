package Dissimulo;

import java.util.ArrayList;

public class InterpreterFunction {

    protected Interpreter interpreter;
    protected InterpreterContext boundContext;

    public InterpreterFunction(Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    public InterpreterFunction(Interpreter interpreter, InterpreterContext boundContext) {
        this.interpreter = interpreter;
        this.boundContext = boundContext;
    }

    public InternalValue externalExecute(ArrayList<InternalValue> values, InterpreterContext context){
        if(this.boundContext != null){
            return internalExecute(values, boundContext);
        }

        return internalExecute(values,context);
    }

    protected InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context){
        return new InternalValue(InternalValue.ValueType.NONE);
    }

    protected ArrayList<InternalValue> replaceVariablesWithValues(ArrayList<InternalValue> values, InterpreterContext context){
        ArrayList<InternalValue> valuesOut = new ArrayList<>();

        for (InternalValue value: values){
            valuesOut.add(interpreter.getVariableValue(value, context));
        }
        
        return valuesOut;
    }

    protected ArrayList<InternalValue> replaceStringObjectsWithStrings(ArrayList<InternalValue> values, InterpreterContext context){
        ArrayList<InternalValue> valuesOut = new ArrayList<>();

        for (InternalValue value: values){
            if(value.getType() != InternalValue.ValueType.REFERENCE){
                valuesOut.add(value);
                continue;
            }
            Object obj = interpreter.getStoredObject(value.getValue());
            if(!(obj instanceof String)){
                continue;
            }

            valuesOut.add(new InternalValue(InternalValue.ValueType.STRING, obj.toString()));
        }

        return valuesOut;
    }
}
