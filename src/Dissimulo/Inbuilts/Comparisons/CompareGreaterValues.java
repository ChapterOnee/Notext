package Dissimulo.Inbuilts.Comparisons;

import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import Dissimulo.InterpreterFunction;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;

public class CompareGreaterValues extends InterpreterFunction {
    public CompareGreaterValues(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
        values = replaceVariblesWithValues(values, context);

        if(values.size() == 0){
            Logger.printWarning("Function compare greater executed with no arguments.");
            return new InternalValue(InternalValue.ValueType.NONE);
        }
        if(values.size() == 1){
            return new InternalValue(InternalValue.ValueType.BOOL);
        }

        InternalValue out = values.get(0);

        for(int i = 1;i < values.size();i++){
            if(!compareTwoValues(out, values.get(i))){
                return new InternalValue(InternalValue.ValueType.BOOL, "false");
            }
        }

        return new InternalValue(InternalValue.ValueType.BOOL, "true");
    }

    private boolean compareTwoValues(InternalValue value1, InternalValue value2){
        if(value1.getType() == InternalValue.ValueType.INT && value2.getType() == InternalValue.ValueType.INT){
            return Integer.parseInt(value1.getValue()) > Integer.parseInt(value2.getValue());
        }

        return false;
    }
}
