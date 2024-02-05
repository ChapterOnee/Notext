package Dissimulo.Inbuilts;

import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import Dissimulo.InterpreterFunction;

import java.util.ArrayList;

public class Print extends InterpreterFunction {
    public Print(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
        values = replaceVariablesWithValues(values, context);
        values = replaceStringObjectsWithStrings(values, context);

        StringBuilder out = new StringBuilder();

        for(InternalValue value: values){
            if (value.getType() == InternalValue.ValueType.REFERENCE){
                out.append(interpreter.getStoredObject(value.getValue()).toString()).append(" ");
            }
            else {
                out.append(value).append(" ");
            }
        }

        System.out.println(out);

        return new InternalValue(InternalValue.ValueType.BOOL,"true");
    }
}

