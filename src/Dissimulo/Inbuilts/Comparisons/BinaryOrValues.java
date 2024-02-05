package Dissimulo.Inbuilts.Comparisons;

import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import Dissimulo.InterpreterFunction;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;

public class BinaryOrValues extends InterpreterFunction {
    public BinaryOrValues(Interpreter interpreter) {
        super(interpreter);
    }
    @Override
    public InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
        values = replaceVariablesWithValues(values, context);
        values = replaceStringObjectsWithStrings(values, context);

        if(values.size() == 0){
            Logger.printWarning("Function compare executed with no arguments.");
            return new InternalValue(InternalValue.ValueType.NONE);
        }
        if(values.size() == 1){
            return new InternalValue(InternalValue.ValueType.BOOL);
        }

        for (InternalValue value : values) {
            if(value.getType() == InternalValue.ValueType.BOOL && value.getValue().equals("true")){
                return new InternalValue(InternalValue.ValueType.BOOL, "true");
            }
        }

        return new InternalValue(InternalValue.ValueType.BOOL, "false");
    }
}

