package AegisLang;

import java.util.ArrayList;

public class InterpreterFunction {
    public InternalValue execute(ArrayList<InternalValue> values){
        return new InternalValue(InternalValue.ValueType.NONE);
    }
}
