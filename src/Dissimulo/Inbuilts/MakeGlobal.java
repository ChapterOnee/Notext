package Dissimulo.Inbuilts;

import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import Dissimulo.InterpreterFunction;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;

public class MakeGlobal extends InterpreterFunction {
    public MakeGlobal(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
        if(values.size() == 1 && values.get(0).getType() == InternalValue.ValueType.ID){
            String name = values.get(0).getValue();

            if(!context.hasVariable(name) && !context.hasFunction(name)){
                Logger.printError("Cannot make variable global, because it doesnt exist.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            if(context.hasVariable(name)) {
                InternalValue temp = context.getVariable(name);
                context.setVariable(name, null);

                context.getHighestModifiableParent().setVariable(name, temp);
            }
            else{
                InterpreterFunction temp = context.getFunction(name);
                context.setFunction(name, null);

                context.getHighestModifiableParent().setFunction(name, temp);
            }
        }
        else{
            Logger.printError("Global function takes only one argument of the ID type.");
        }
        return new InternalValue(InternalValue.ValueType.NONE);
    }
}
