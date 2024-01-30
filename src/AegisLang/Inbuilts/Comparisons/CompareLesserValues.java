package AegisLang.Inbuilts.Comparisons;

import AegisLang.InternalValue;
import AegisLang.Interpreter;
import AegisLang.InterpreterFunction;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;

public class CompareLesserValues extends InterpreterFunction {
    public CompareLesserValues(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue execute(ArrayList<InternalValue> values) {
        values = replaceVariblesWithValues(values);

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
            return Integer.parseInt(value1.getValue()) < Integer.parseInt(value2.getValue());
        }

        return false;
    }
}
