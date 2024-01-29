package AegisLang.Inbuilts;

import AegisLang.InternalValue;
import AegisLang.Interpreter;
import AegisLang.InterpreterFunction;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;

public class CompareValues extends InterpreterFunction {

    public CompareValues(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue execute(ArrayList<InternalValue> values) {
        values = replaceVariblesWithValues(values);

        if(values.size() == 0){
            Logger.printWarning("Function compare executed with no arguments.");
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
        return value1.getType() == value2.getType() && value1.getValue().equals(value2.getValue());
    }

}
