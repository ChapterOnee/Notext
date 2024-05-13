package Dissimulo.Inbuilts;

import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;
import AmbrosiaUI.Widgets.Placements.Placement;
import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import Dissimulo.InterpreterFunction;

import java.util.ArrayList;

public class CastIntoOtherObject extends InterpreterFunction {
    public CastIntoOtherObject(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    protected InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
        if(
                values.size() == 2 && values.get(0).getType() == InternalValue.ValueType.ID && interpreter.getVariableValue(values.get(1),context).getType() == InternalValue.ValueType.REFERENCE
        ){
            InternalValue referenceValue = interpreter.getVariableValue(values.get(1),context);
            String reference = referenceValue.getValue();
            String className = values.get(0).getValue();

            Object nw;
            Object obj = interpreter.getStoredObject(reference);
            Class<?> clazz = interpreter.getClassForName(className);
            if(clazz == null){
                Logger.printError("Error when casting values.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            nw = clazz.cast(obj);
            return interpreter.generateReferenceForObject(nw, clazz);

        }
        else{
            Logger.printError("Invalid arguments for casting");
            return new InternalValue(InternalValue.ValueType.NONE);
        }
    }
}
