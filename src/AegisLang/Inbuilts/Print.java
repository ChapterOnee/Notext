package AegisLang.Inbuilts;

import AegisLang.InternalValue;
import AegisLang.Interpreter;
import AegisLang.InterpreterContext;
import AegisLang.InterpreterFunction;

import java.util.ArrayList;

public class Print extends InterpreterFunction {
    public Print(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue execute(ArrayList<InternalValue> values, InterpreterContext context) {
        StringBuilder out = new StringBuilder();

        for(InternalValue value: values){
            out.append(value).append(" ");
        }

        System.out.println(out);

        return new InternalValue(InternalValue.ValueType.BOOL,"true");
    }
}

