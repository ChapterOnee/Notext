package AegisLang;

import AegisLang.Inbuilts.*;
import AegisLang.Inbuilts.Comparisons.*;
import AegisLang.Inbuilts.Mathematical.AddValues;
import AegisLang.Inbuilts.Mathematical.DivideValues;
import AegisLang.Inbuilts.Mathematical.MultiplyValues;
import AegisLang.Inbuilts.Mathematical.SubtractValues;
import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    private InterpreterContext globalContext = new InterpreterContext();

    public Interpreter() {
        globalContext.addFunction("add", new AddValues(this));
        globalContext.addFunction("subtract", new SubtractValues(this));
        globalContext.addFunction("divide", new DivideValues(this));
        globalContext.addFunction("multiply", new MultiplyValues(this));
        globalContext.addFunction("compare", new CompareValues(this));
        globalContext.addFunction("and", new BinaryAndValues(this));
        globalContext.addFunction("or", new BinaryOrValues(this));
        globalContext.addFunction("greater", new CompareGreaterValues(this));
        globalContext.addFunction("lesser", new CompareLesserValues(this));

        globalContext.addFunction("print", new Print(this));
    }

    public InternalValue execute(String code){
        ArrayList<ASTreeNode> nodes = Parser.parseAbstractSyntaxTrees(Lexer.lexData(code));
        return executeNodes(nodes,  globalContext);
    }

    public InternalValue executeNodes(ArrayList<ASTreeNode> nodes,  InterpreterContext context){
        InternalValue returnValue = new InternalValue(InternalValue.ValueType.NONE);

        for(ASTreeNode node: nodes){
            InternalValue value = evaluateExpression(node,  context);

            if(value.isReturned()){
                returnValue = value;
                break;
            }
        }

        //System.out.println("Executed with return value = '"+ returnValue + "'");

        return returnValue;
    }

    private String lastStatement = "";
    private InternalValue evaluateExpression(ASTreeNode tree, InterpreterContext context){
        InternalValue leftValue = null;
        InternalValue rightValue = null;
        InternalValue endValue = new InternalValue(InternalValue.ValueType.NONE);

        InterpreterContext localContext = new InterpreterContext(context);

        //Parser.displayTree(tree);
        //System.out.println(lastStatement);
        //System.out.println(localContext);


        boolean foundStatement = false;

        if(tree.getType() == Lexer.LexerTokenType.EXPRESSION){
            ArrayList<ASTreeNode> nodes = Parser.parseContext(tree);
            tree = nodes.get(0);
        }
        else if(tree.getType() == Lexer.LexerTokenType.CONTEXT){
            return executeNodes(Parser.parseContext(tree), localContext);
        }
        else if(tree.getType() == Lexer.LexerTokenType.ID && tree.getValue().equals("return")){
            if(tree.getRightChildNode() == null){
                return new InternalValue(InternalValue.ValueType.NONE, true);
            }

            InternalValue returnedValue = evaluateExpression(tree.getRightChildNode(), context);

            if(returnedValue.getType() == InternalValue.ValueType.ID){
                returnedValue = getVariableValue(returnedValue, context);
            }

            returnedValue.setReturned(true);
            lastStatement = "";
            return returnedValue;
        }
        else if(tree.getType() == Lexer.LexerTokenType.ID
                && tree.getRightChildNode() != null && tree.getRightChildNode().getType() == Lexer.LexerTokenType.EXPRESSION
                && tree.getRightChildNode().getRightChildNode() != null && tree.getRightChildNode().getRightChildNode().getType() == Lexer.LexerTokenType.CONTEXT
        ){
            switch (tree.getValue()){
                case "if" -> {
                    InternalValue condition = evaluateExpression(tree.getRightChildNode(), context);

                    if(condition.getType() == InternalValue.ValueType.BOOL && condition.getValue().equals("true")){
                        executeNodes(Parser.parseContext(tree.getRightChildNode().getRightChildNode()), localContext);
                        lastStatement =  "if_success";
                    }
                    else{
                        lastStatement =  "if_fail";
                    }

                    return new InternalValue(InternalValue.ValueType.NONE);
                }
                case "elif" -> {
                    InternalValue condition = evaluateExpression(tree.getRightChildNode(), context);
                    if(lastStatement.equals("if_fail") && condition.getType() == InternalValue.ValueType.BOOL && condition.getValue().equals("true")){
                        executeNodes(Parser.parseContext(tree.getRightChildNode().getRightChildNode()), localContext);
                        lastStatement =  "if_success";
                    }

                    return new InternalValue(InternalValue.ValueType.NONE);
                }
                case "while" -> {
                    InternalValue condition = evaluateExpression(tree.getRightChildNode(), context);

                    while(condition.getType() == InternalValue.ValueType.BOOL && condition.getValue().equals("true")){
                        executeNodes(Parser.parseContext(tree.getRightChildNode().getRightChildNode()), localContext);
                        condition = evaluateExpression(tree.getRightChildNode(), context);
                    }

                    return new InternalValue(InternalValue.ValueType.NONE);
                }
                default -> {

                }
            }
        } else if (tree.getType() == Lexer.LexerTokenType.ID
                && tree.getRightChildNode() != null && tree.getRightChildNode().getType() == Lexer.LexerTokenType.CONTEXT
                && tree.getValue().equals("else")) {

            if(lastStatement.equals("")){
                Logger.printError("Else statement missing if before it.");
            }
            if(lastStatement.equals("if_fail")){
                executeNodes(Parser.parseContext(tree.getRightChildNode()), localContext);
                lastStatement = "";
            }
        }


        if(!foundStatement && tree.getType() == Lexer.LexerTokenType.ID
                && !context.hasVariable(tree.getValue())
                && tree.getRightChildNode() != null && tree.getRightChildNode().getType() == Lexer.LexerTokenType.EXPRESSION
        ){
            if(!context.hasFunction(tree.getValue())){
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
                    continue;
                }
                else if(nodes.size() > 1){
                    Logger.printError("Illegal context in function argument.");
                    return new InternalValue(InternalValue.ValueType.NONE);
                }

                InternalValue value = evaluateExpression(nodes.get(0), context);

                if(value.getType() == InternalValue.ValueType.ID){
                    value = getVariableValue(value, context);
                }

                arguments.add(value);
            }

            return context.getFunction(tree.getValue()).execute(arguments, context);
        }

        if(tree.getLeftChildNode() != null){
            leftValue = evaluateExpression(tree.getLeftChildNode(), context);
        }
        if(tree.getLeftChildNode() != null){
            rightValue = evaluateExpression(tree.getRightChildNode(), context);
        }

        if(leftValue != null && rightValue != null && tree.getType() == Lexer.LexerTokenType.OPERATION){
            endValue = evaluateOperation(leftValue,rightValue,tree.getValue(), context);
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

                    endValue = evaluateExpression(nodes.get(0), context);
                    return endValue;
                }
            }


            endValue = new InternalValue(type, value);
        }

        return endValue;
    }

    public InternalValue getVariableValue(InternalValue id, InterpreterContext context){
        if(!context.hasVariable(id.getValue())){
            Logger.printError("No variable or identifier named: " + id.getValue());
            return new InternalValue(InternalValue.ValueType.NONE);
        }

        return context.getVariable(id.getValue());
    }

    private InternalValue evaluateOperation(InternalValue value1, InternalValue value2, String operation, InterpreterContext context){
        switch (operation){
            case "+" -> {
                return context.getFunction("add").execute(new ArrayList<>(List.of(value1,value2)), context);
            }
            case "-" -> {
                return context.getFunction("subtract").execute(new ArrayList<>(List.of(value1,value2)), context);
            }
            case "*" -> {
                return context.getFunction("multiply").execute(new ArrayList<>(List.of(value1,value2)), context);
            }
            case "/" -> {
                return context.getFunction("divide").execute(new ArrayList<>(List.of(value1,value2)), context);
            }
            case "=" -> {
                if(value1.getType() != InternalValue.ValueType.ID){
                    return new InternalValue(InternalValue.ValueType.BOOL, "false");
                }

                if(value2.getType() == InternalValue.ValueType.ID){
                    value2 = getVariableValue(value2, context);
                }

                context.setVariable(value1.getValue(), value2);
                return new InternalValue(InternalValue.ValueType.BOOL, "true");
            }
            case "==" -> {
                return context.getFunction("compare").execute(new ArrayList<>(List.of(value1,value2)), context);
            }
            case "&&" -> {
                return context.getFunction("and").execute(new ArrayList<>(List.of(value1,value2)), context);
            }
            case "||" -> {
                return context.getFunction("or").execute(new ArrayList<>(List.of(value1,value2)), context);
            }
            case ">" -> {
                return context.getFunction("greater").execute(new ArrayList<>(List.of(value1,value2)), context);
            }
            case "<" -> {
                return context.getFunction("lesser").execute(new ArrayList<>(List.of(value1,value2)), context);
            }
            default -> {
                return new InternalValue(InternalValue.ValueType.NONE);
            }
        }
    }

    public void addFunction(String name, InterpreterFunction function){
        globalContext.addFunction(name,function);
    }
}
