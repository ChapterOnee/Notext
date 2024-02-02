package Dissimulo.Inbuilts.Mathematical;

import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import Dissimulo.InterpreterFunction;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;

public class MultiplyValues extends InterpreterFunction {
    public MultiplyValues(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
        values = replaceVariblesWithValues(values, context);

        if(values.size() == 0){
            Logger.printWarning("Function multiply executed with no arguments.");
            return new InternalValue(InternalValue.ValueType.NONE);
        }
        if(values.size() == 1){
            return values.get(0);
        }

        InternalValue out = values.get(0);

        for(int i = 1;i < values.size();i++){
            out = multiplyTwoValues(out, values.get(i));
        }

        return out;
    }

    private InternalValue multiplyTwoValues(InternalValue value1, InternalValue value2){
        if(value1.getType() == InternalValue.ValueType.INT && value2.getType() == InternalValue.ValueType.INT){
            return new InternalValue(InternalValue.ValueType.INT,
                    Integer.parseInt(value1.getValue()) * Integer.parseInt(value2.getValue()) + "");
        }

        return new InternalValue(InternalValue.ValueType.NONE);
    }
}
