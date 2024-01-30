package AegisLang;

import AegisLang.Inbuilts.*;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Interpreter {
    private HashMap<String, InterpreterFunction> functions = new HashMap<>();
    private HashMap<String, InternalValue> variables = new HashMap<>();

    public Interpreter() {
        functions.put("add", new AddValues(this));
        functions.put("subtract", new SubtractValues(this));
        functions.put("divide", new DivideValues(this));
        functions.put("multiply", new MultiplyValues(this));
        functions.put("compare", new CompareValues(this));
        functions.put("and", new BinaryAndValues(this));
        functions.put("or", new BinaryOrValues(this));
        functions.put("greater", new CompareGreaterValues(this));
        functions.put("lesser", new CompareLesserValues(this));

        functions.put("print", new Print(this));
    }

    public InternalValue execute(String code){
        ArrayList<ASTreeNode> nodes = Parser.parseAbstractSyntaxTrees(Lexer.lexData(code));
        return executeNodes(nodes);
    }

    public InternalValue executeNodes(ArrayList<ASTreeNode> nodes){
        InternalValue returnValue = new InternalValue(InternalValue.ValueType.NONE);

        for(ASTreeNode node: nodes){
            InternalValue value = evaluateExpression(node);

            if(value.isReturned()){
                returnValue = value;
                break;
            }
        }

        //System.out.println("Executed with return value = '"+ returnValue + "'");

        return returnValue;
    }

    private InternalValue evaluateExpression(ASTreeNode tree){
        InternalValue leftValue = null;
        InternalValue rightValue = null;
        InternalValue endValue = new InternalValue(InternalValue.ValueType.NONE);

        //Parser.displayTree(tree);


        if(tree.getType() == Lexer.LexerTokenType.EXPRESSION){
            ArrayList<ASTreeNode> nodes = Parser.parseContext(tree);
            tree = nodes.get(0);
        }
        else if(tree.getType() == Lexer.LexerTokenType.CONTEXT){
            return executeNodes(Parser.parseContext(tree));
        }
        else if(tree.getType() == Lexer.LexerTokenType.ID && tree.getValue().equals("return")){
            if(tree.getRightChildNode() == null){
                return new InternalValue(InternalValue.ValueType.NONE, true);
            }

            InternalValue returnedValue = evaluateExpression(tree.getRightChildNode());

            if(returnedValue.getType() == InternalValue.ValueType.ID){
                returnedValue = getVariableValue(returnedValue);
            }

            returnedValue.setReturned(true);
            return returnedValue;
        }
        else if(tree.getType() == Lexer.LexerTokenType.ID && tree.getValue().equals("if")){
            if(tree.getRightChildNode() == null){
                Logger.printError("If statement missing condition expression.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }
            if(tree.getRightChildNode().getRightChildNode() == null){
                Logger.printError("If statement missing body context.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            InternalValue condition = evaluateExpression(tree.getRightChildNode());

            if(condition.getType() == InternalValue.ValueType.BOOL && condition.getValue().equals("true")){
                executeNodes(Parser.parseContext(tree.getRightChildNode().getRightChildNode()));
            }

            return new InternalValue(InternalValue.ValueType.NONE);
        }
        else if(tree.getType() == Lexer.LexerTokenType.ID && tree.getValue().equals("while")){
            if(tree.getRightChildNode() == null){
                Logger.printError("While statement missing condition expression.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }
            if(tree.getRightChildNode().getRightChildNode() == null){
                Logger.printError("While statement missing body context.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            InternalValue condition = evaluateExpression(tree.getRightChildNode());

            while(condition.getType() == InternalValue.ValueType.BOOL && condition.getValue().equals("true")){
                executeNodes(Parser.parseContext(tree.getRightChildNode().getRightChildNode()));
                condition = evaluateExpression(tree.getRightChildNode());
            }

            return new InternalValue(InternalValue.ValueType.NONE);
        }
        else if(tree.getType() == Lexer.LexerTokenType.ID && !variables.containsKey(tree.getValue()) && tree.getRightChildNode() != null && tree.getRightChildNode().getType() == Lexer.LexerTokenType.EXPRESSION){
            if(!functions.containsKey(tree.getValue())){
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            ASTreeNode current = tree.getRightChildNode();
            ArrayList<LexerToken> tokens = Lexer.lexData(current.getValue().substring(1,current.getValue().length()-1));

            ArrayList<ArrayList<LexerToken>> individialArgumentTokens = new ArrayList<>();
            individialArgumentTokens.add(new ArrayList<>());

            for(LexerToken token: tokens){
                if(token.getType() == Lexer.LexerTokenType.ARGUMENT_SPLITTER){
                    individialArgumentTokens.add(new ArrayList<>());
                    continue;
                }

                individialArgumentTokens.get(individialArgumentTokens.size()-1).add(token);
            }

            ArrayList<InternalValue> arguments = new ArrayList<>();
            for (ArrayList<LexerToken> argumentTokens: individialArgumentTokens){
                ArrayList<ASTreeNode> nodes = Parser.parseAbstractSyntaxTrees(argumentTokens);

                if(nodes.size() == 0){
                    Logger.printError("Invalid expression in function argument.");
                    return new InternalValue(InternalValue.ValueType.NONE);
                }
                else if(nodes.size() > 1){
                    Logger.printError("Illegal context in function argument.");
                    return new InternalValue(InternalValue.ValueType.NONE);
                }

                arguments.add(evaluateExpression(nodes.get(0)));
            }

            return functions.get(tree.getValue()).execute(arguments);
        }

        if(tree.getLeftChildNode() != null){
            leftValue = evaluateExpression(tree.getLeftChildNode());
        }
        if(tree.getLeftChildNode() != null){
            rightValue = evaluateExpression(tree.getRightChildNode());
        }

        if(leftValue != null && rightValue != null && tree.getType() == Lexer.LexerTokenType.OPERATION){
            endValue = evaluateOperation(leftValue,rightValue,tree.getValue());
        }

        if(!tree.hasChildren()){
            InternalValue.ValueType type = InternalValue.ValueType.NONE;
            String value = tree.getValue();

            switch (tree.getType()){
                case ID -> type = InternalValue.ValueType.ID;
                case NUMBER -> type = InternalValue.ValueType.INT;
                case STRING -> {
                    type = InternalValue.ValueType.STRING;
                    value = value.substring(1,value.length()-1);
                }
                case BOOL -> {
                    type = InternalValue.ValueType.BOOL;
                }
                case CONTEXT -> {
                    ArrayList<ASTreeNode> nodes = Parser.parseContext(tree);

                    Parser.displayTree(nodes.get(0));

                    endValue = evaluateExpression(nodes.get(0));
                    return endValue;
                }
            }


            endValue = new InternalValue(type, value);
        }

        return endValue;
    }

    public InternalValue getVariableValue(InternalValue id){
        if(!variables.containsKey(id.getValue())){
            Logger.printError("No variable or identifier named: " + id.getValue());
            return new InternalValue(InternalValue.ValueType.NONE);
        }

        return variables.get(id.getValue());
    }

    private InternalValue evaluateOperation(InternalValue value1, InternalValue value2, String operation){
        switch (operation){
            case "+" -> {
                return functions.get("add").execute(new ArrayList<>(List.of(value1,value2)));
            }
            case "-" -> {
                return functions.get("subtract").execute(new ArrayList<>(List.of(value1,value2)));
            }
            case "*" -> {
                return functions.get("multiply").execute(new ArrayList<>(List.of(value1,value2)));
            }
            case "/" -> {
                return functions.get("divide").execute(new ArrayList<>(List.of(value1,value2)));
            }
            case "=" -> {
                if(value1.getType() != InternalValue.ValueType.ID){
                    return new InternalValue(InternalValue.ValueType.BOOL, "false");
                }

                if(value2.getType() == InternalValue.ValueType.ID){
                    value2 = getVariableValue(value2);
                }

                variables.put(value1.getValue(), value2);
                return new InternalValue(InternalValue.ValueType.BOOL, "true");
            }
            case "==" -> {
                return functions.get("compare").execute(new ArrayList<>(List.of(value1,value2)));
            }
            case "&&" -> {
                return functions.get("and").execute(new ArrayList<>(List.of(value1,value2)));
            }
            case "||" -> {
                return functions.get("or").execute(new ArrayList<>(List.of(value1,value2)));
            }
            case ">" -> {
                return functions.get("greater").execute(new ArrayList<>(List.of(value1,value2)));
            }
            case "<" -> {
                return functions.get("lesser").execute(new ArrayList<>(List.of(value1,value2)));
            }
            default -> {
                return new InternalValue(InternalValue.ValueType.NONE);
            }
        }
    }
}
