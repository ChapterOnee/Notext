package Tests;

import Dissimulo.Inbuilts.Comparisons.CompareValues;
import Dissimulo.Inbuilts.Mathematical.AddValues;
import Dissimulo.InternalValue;
import Dissimulo.Interpreter;
import Dissimulo.InterpreterContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CompareValuesTest {

    InterpreterContext context;
    Interpreter interpreter = new Interpreter();
    ArrayList<InternalValue> values = new ArrayList<>();
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        context = new InterpreterContext();
        values.add(new InternalValue(InternalValue.ValueType.INT,"1"));
        values.add(new InternalValue(InternalValue.ValueType.INT,"1"));
    }

    @org.junit.jupiter.api.Test
    void internalExecute() {
        InternalValue result = new CompareValues(interpreter).internalExecute(values,context);
        assertEquals(InternalValue.ValueType.BOOL, result.getType());
        assertEquals("true", result.getValue());
    }
}