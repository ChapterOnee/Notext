package AegisLang;

import AegisLang.Inbuilts.AddValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Interpreter {
    private HashMap<String, InterpreterFunction> functions = new HashMap<>();
    private HashMap<String, InternalValue> variables = new HashMap<>();

    public Interpreter() {
        functions.put("add", new AddValues());
        functions.put("subtract", new AddValues());
        functions.put("divide", new AddValues());
        functions.put("multiply", new AddValues());

    }

    public void executeTree(ASTreeNode tree){
        System.out.println(tree);
    }

    private InternalValue evaluateExpression(ASTreeNode tree){
        InternalValue leftValue = null;
        InternalValue rightValue = null;
        InternalValue endValue = new InternalValue(InternalValue.ValueType.NONE);

        if(tree.getLeftChildNode() != null){
            leftValue = evaluateExpression(tree.getLeftChildNode());
        }
        if(tree.getLeftChildNode() != null){
            rightValue = evaluateExpression(tree.getLeftChildNode());
        }

        if(leftValue != null && rightValue != null && tree.getType() == Lexer.LexerTokenType.OPERATION){
            endValue = evaluateOperation(leftValue,rightValue,tree.getValue());
        }

        return endValue;
    }

    private InternalValue evaluateOperation(InternalValue value1, InternalValue value2, String operation){
        return switch (operation){
            case "+" -> functions.get("add").execute(new ArrayList<>(List.of(value1,value2)));
            case "-" -> functions.get("subtract").execute(new ArrayList<>(List.of(value1,value2)));
            case "*" -> functions.get("multiply").execute(new ArrayList<>(List.of(value1,value2)));
            case "/" -> functions.get("divide").execute(new ArrayList<>(List.of(value1,value2)));
            default -> new InternalValue(InternalValue.ValueType.NONE);
        };
    }
}
