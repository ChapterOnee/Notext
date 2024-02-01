package AegisLang.Inbuilts;

import AegisLang.InternalValue;
import AegisLang.Interpreter;
import AegisLang.InterpreterContext;
import AegisLang.InterpreterFunction;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;

public class MakeGlobal extends InterpreterFunction {
    public MakeGlobal(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue execute(ArrayList<InternalValue> values, InterpreterContext context) {
        if(values.size() == 1 && values.get(0).getType() == InternalValue.ValueType.ID){
            if(!context.hasVariable(values.get(0).getValue())){
                Logger.printError("Cannot make variable global, because it doesnt exist.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            InternalValue temp = context.getVariable(values.get(0).getValue());
            context.setVariable(values.get(0).getValue(), null);

            context.getHighestModifiableParent().setVariable(values.get(0).getValue(), temp);
        }
        else{
            Logger.printError("Global function takes only one argument of the ID type.");
        }
        return new InternalValue(InternalValue.ValueType.NONE);
    }
}
