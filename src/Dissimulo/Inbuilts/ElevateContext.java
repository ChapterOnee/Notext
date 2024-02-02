package Dissimulo.Inbuilts;

import AmbrosiaUI.Utility.Logger;
import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import Dissimulo.InterpreterFunction;

import java.util.ArrayList;

public class ElevateContext extends InterpreterFunction {
    public ElevateContext(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
        if(values.size() == 2 && values.get(0).getType() == InternalValue.ValueType.ID && values.get(1).getType() == InternalValue.ValueType.INT){
            String name = values.get(0).getValue();

            if(!context.hasVariable(name) && !context.hasFunction(name)){
                Logger.printError("Cannot make variable global, because it doesnt exist.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            if(context.hasVariable(name)) {
                InternalValue temp = context.getVariable(name);

                InterpreterContext current = context;
                for(int i = 0;i < Integer.parseInt(values.get(1).getValue());i++) {
                    current.setVariable(name, null);
                    current = current.getModifiableParentContext();
                    current.setVariable(name, temp);
                }
            }
            else{
                InterpreterFunction temp = context.getFunction(name);

                InterpreterContext current = context;
                for(int i = 0;i < Integer.parseInt(values.get(1).getValue());i++) {
                    current.setFunction(name, null);
                    current = current.getModifiableParentContext();
                    current.setFunction(name, temp);
                }
            }
        }
        else{
            Logger.printError("Elevate function only takes exactly two arguments. ID (Variable or function identifier) , INT (Levels to elevate).");
        }
        return new InternalValue(InternalValue.ValueType.NONE);
    }
}
