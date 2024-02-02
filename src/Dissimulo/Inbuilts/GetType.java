package Dissimulo.Inbuilts;

import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import Dissimulo.InterpreterFunction;

import java.util.ArrayList;

public class GetType extends InterpreterFunction {

    public GetType(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
        return new InternalValue(InternalValue.ValueType.STRING,values.get(0).getType()+"");
    }
}
