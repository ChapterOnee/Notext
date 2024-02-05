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
            try {
                Object obj = interpreter.getStoredObject(reference);
                Class<?> clazz = Class.forName(className);
                nw = clazz.cast(obj);
                return interpreter.generateReferenceForObject(nw, clazz);
            } catch (ClassNotFoundException e) {
                Logger.printError("Error when casting values: " + e);
                return new InternalValue(InternalValue.ValueType.NONE);
            }
        }
        else{
            Logger.printError("Invalid arguments for casting");
            return new InternalValue(InternalValue.ValueType.NONE);
        }
    }
}
