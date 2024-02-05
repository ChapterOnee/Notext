package Dissimulo.Inbuilts.Comparisons;

import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import Dissimulo.InterpreterFunction;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;

public class BinaryAndValues extends InterpreterFunction {

    public BinaryAndValues(Interpreter interpreter) {
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

        InternalValue out = values.get(0);

        for(int i = 1;i < values.size();i++){
            if(!andTwoValues(out, values.get(i))){
                return new InternalValue(InternalValue.ValueType.BOOL, "false");
            }
        }

        return new InternalValue(InternalValue.ValueType.BOOL, "true");
    }

    private boolean andTwoValues(InternalValue value1, InternalValue value2){
        return value1.getType() == InternalValue.ValueType.BOOL && value2.getType() == InternalValue.ValueType.BOOL &&
                value1.getValue().equals("true") && value2.getValue().equals("true");
    }
}
