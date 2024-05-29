package Tests;

import Dissimulo.Inbuilts.Mathematical.AddValues;
import Dissimulo.Inbuilts.Mathematical.DivideValues;
import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DivideValuesTest {

    InterpreterContext context;
    Interpreter interpreter = new Interpreter();
    ArrayList<InternalValue> values = new ArrayList<>();
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        context = new InterpreterContext();
        values.add(new InternalValue(InternalValue.ValueType.INT,"10"));
        values.add(new InternalValue(InternalValue.ValueType.INT,"5"));
    }

    @org.junit.jupiter.api.Test
    void internalExecute() {
        InternalValue result = new DivideValues(interpreter).internalExecute(values,context);
        assertEquals(InternalValue.ValueType.INT, result.getType());
        assertEquals("2", result.getValue());
    }
}