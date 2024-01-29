package AegisLang.Inbuilts;

import AegisLang.InternalValue;
import AegisLang.Interpreter;
import AegisLang.InterpreterFunction;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;

public class BinaryOrValues extends InterpreterFunction {
    public BinaryOrValues(Interpreter interpreter) {
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

        for (InternalValue value : values) {
            if(value.getType() == InternalValue.ValueType.BOOL && value.getValue().equals("true")){
                return new InternalValue(InternalValue.ValueType.BOOL, "true");
            }
        }

        return new InternalValue(InternalValue.ValueType.BOOL, "false");
    }
}

