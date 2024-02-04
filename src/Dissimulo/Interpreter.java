package Dissimulo;

import Dissimulo.Inbuilts.*;
import Dissimulo.Inbuilts.Comparisons.*;
import Dissimulo.Inbuilts.Mathematical.AddValues;
import Dissimulo.Inbuilts.Mathematical.DivideValues;
import Dissimulo.Inbuilts.Mathematical.MultiplyValues;
import Dissimulo.Inbuilts.Mathematical.SubtractValues;
import AmbrosiaUI.Utility.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Interpreter {
    private InterpreterContext globalContext = new InterpreterContext(false);

    private HashMap<String, InterpreterFunction> assignedOperatorsToFunctions = new HashMap<>();

    private HashMap<String, Object> storedObjects = new HashMap<>();

    public Interpreter() {
        InterpreterFunction addFunction = new AddValues(this);
        globalContext.addFunction("add", addFunction);
        assignedOperatorsToFunctions.put("+", addFunction);

        InterpreterFunction subtractFunction = new SubtractValues(this);
        globalContext.addFunction("subtract", subtractFunction);
        assignedOperatorsToFunctions.put("-", subtractFunction);

        InterpreterFunction divideFunction = new DivideValues(this);
        globalContext.addFunction("divide", divideFunction);
        assignedOperatorsToFunctions.put("/", divideFunction);

        InterpreterFunction multiplyFunction = new MultiplyValues(this);
        globalContext.addFunction("multiply", multiplyFunction);
        assignedOperatorsToFunctions.put("*",multiplyFunction);

        InterpreterFunction compareFunction = new CompareValues(this);
        globalContext.addFunction("compare", compareFunction);
        assignedOperatorsToFunctions.put("==", compareFunction);

        InterpreterFunction andFunction = new BinaryAndValues(this);
        globalContext.addFunction("and", andFunction);
        assignedOperatorsToFunctions.put("&&", andFunction);

        InterpreterFunction orFunction = new BinaryOrValues(this);
        globalContext.addFunction("or", orFunction);
        assignedOperatorsToFunctions.put("||", orFunction);

        InterpreterFunction greaterFunction = new CompareGreaterValues(this);
        globalContext.addFunction("greater", greaterFunction);
        assignedOperatorsToFunctions.put(">", greaterFunction);

        InterpreterFunction lesserFunction = new CompareLesserValues(this);
        globalContext.addFunction("lesser", lesserFunction);
        assignedOperatorsToFunctions.put("<",  lesserFunction);

        InterpreterFunction greaterFunctionOrEqual = new CompareGreaterOrEqualValues(this);
        globalContext.addFunction("greaterOrEqual", greaterFunctionOrEqual);
        assignedOperatorsToFunctions.put(">=", greaterFunctionOrEqual);

        InterpreterFunction lesserFunctionOrEqual = new CompareLesserOrEqualValues(this);
        globalContext.addFunction("lesserOrEqual", lesserFunctionOrEqual);
        assignedOperatorsToFunctions.put("<=",  lesserFunctionOrEqual);

        globalContext.addFunction("print", new Print(this));
        globalContext.addFunction("global", new MakeGlobal(this));
        globalContext.addFunction("typeOf", new GetType(this));
        globalContext.addFunction("elevate", new ElevateContext(this));

        globalContext.addFunction("wait", new InterpreterFunction(this) {
            @Override
            protected InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
                if(values.size() != 1){
                    Logger.printError("Wait function missing time.");
                    return new InternalValue(InternalValue.ValueType.NONE);
                }

                try {
                    Thread.sleep(Integer.parseInt(values.get(0).getValue()));
                } catch (InterruptedException ignored) {

                }

                return new InternalValue(InternalValue.ValueType.NONE);
            }
        });

        globalContext.addFunction("import", new InterpreterFunction(this){
            @Override
            protected InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
                if(values.size() != 1){
                    Logger.printError("Import function only takes in a path argument.");
                    return new InternalValue(InternalValue.ValueType.NONE);
                }

                try(BufferedReader bf = new BufferedReader(new FileReader(values.get(0).getValue()))) {
                    StringBuilder allData = new StringBuilder();

                    while (bf.ready()){
                        allData.append(bf.readLine());
                    }

                    Interpreter.this.execute(allData.toString(), context);
                }
                catch (IOException e) {
                    Logger.printError("Failed to import file '"+values.get(0)+"' reason: " + e);
                }


                return new InternalValue(InternalValue.ValueType.NONE);
            }
        });
        globalContext.addFunction("createScanner",  new InterpreterFunction(this){
            @Override
            protected InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
                String id = "REF_"+generateID(16);
                createReferencedObject(id, new Scanner(System.in));

                return new InternalValue(InternalValue.ValueType.REFERENCE,  id);
            }
        });
    }

    private static final Random rng = new Random();
    private static final String idCharacters = "abcdefghijklmnopqrstuvwxyz" + "abcdefghijklmnopqrstuvwxyz".toUpperCase();
    public static String generateID(int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = idCharacters.charAt(rng.nextInt(idCharacters.length()));
        }
        return new String(text);
    }


    public InternalValue execute(String code){
        return execute(code,  new InterpreterContext(globalContext));
    }

    public InternalValue execute(String code, InterpreterContext context){
        ArrayList<ASTreeNode> nodes = Parser.parseAbstractSyntaxTrees(Lexer.lexData(code));
        return executeNodes(nodes,  context);
    }

    public InternalValue createReferencedObject(String referenceName, Object object){
        storedObjects.put(referenceName,object);
        return new InternalValue(InternalValue.ValueType.REFERENCE,referenceName);
    }

    public InternalValue generateReferenceForObject(Object object){
        String referenceName = generateID(16);
        storedObjects.put(referenceName,object);
        return new InternalValue(InternalValue.ValueType.REFERENCE,referenceName);
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
        else if(tree.getType() == Lexer.LexerTokenType.IDENTIFIER_EXPRESSION){
            InternalValue value = parseIdentifierExpression(tree,context);

            tree.setValue(value.getValue());
            tree.setType(Lexer.LexerTokenType.ID);
        }
        else if(matchesTreeSignature(tree, Lexer.LexerTokenType.ID, Lexer.LexerTokenType.IDENTIFIER_EXPRESSION) && !tree.getValue().equals("function")){
            InternalValue value = evaluateExpression(tree.getRightChildNode(),context);
            tree.setValue(tree.getValue() + value.getValue());
            tree.setRightChildNode(tree.getRightChildNode().getRightChildNode());
        }
        else if(matchesTreeSignature(tree, Lexer.LexerTokenType.ID, Lexer.LexerTokenType.IDENTIFIER_EXPRESSION) && tree.getValue().equals("function")){
            InternalValue value = parseIdentifierExpression(tree.getRightChildNode(),context);
            tree.getRightChildNode().setValue(value.getValue());
            tree.getRightChildNode().setType(Lexer.LexerTokenType.ID);
        }

        if(tree.getType() == Lexer.LexerTokenType.ID && tree.getValue().equals("return")){
            if(tree.getRightChildNode() == null){
                return new InternalValue(InternalValue.ValueType.NONE, true);
            }

            InternalValue returnedValue = evaluateExpression(tree.getRightChildNode(), context);
            returnedValue = getVariableValue(returnedValue, context);

            returnedValue.setReturned(true);
            lastStatement = "";
            return returnedValue;
        }
        else if(matchesTreeSignature(tree, Lexer.LexerTokenType.ID, Lexer.LexerTokenType.EXPRESSION) && // Handle functions from references
                tree.getType() == Lexer.LexerTokenType.ID && tree.getValue().split("\\.").length > 1
                && context.hasVariable(tree.getValue().split("\\.")[0]) &&
                getVariableValue(new InternalValue(InternalValue.ValueType.ID,tree.getValue().split("\\.")[0]),context).getType() == InternalValue.ValueType.REFERENCE
        ){
            String[] args = tree.getValue().split("\\.");

            InternalValue reference = getVariableValue(new InternalValue(InternalValue.ValueType.ID,tree.getValue().split("\\.")[0]),context);

            String name = reference.getValue();
            String methodName = args[1];
            ArrayList<InternalValue> arguments = parseArgumentsFromContext(tree.getRightChildNode(),context);
            if(args.length > 2){
                Logger.printError("Invalid method calling from a reference.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }
            if (!storedObjects.containsKey(name)) {
                Logger.printError("No method '"+methodName+"' found under referenced object '"+name+"'.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            Object obj = storedObjects.get(name);
            Object result;
            try {
                Class<?>[] classArguments = new Class<?>[arguments.size()];
                Object[] classArgumentValues = new Object[arguments.size()];
                for(int i = 0;i < arguments.size();i++){
                    classArguments[i] = switch (arguments.get(i).getType()){
                        case INT -> int.class;
                        case DOUBLE -> double.class;
                        case STRING -> String.class;
                        case REFERENCE -> storedObjects.get(arguments.get(i).getValue()).getClass();
                        case BOOL -> boolean.class;
                        default -> null;
                    };
                    classArgumentValues[i] = switch (arguments.get(i).getType()){
                        case INT -> Integer.parseInt(arguments.get(i).getValue());
                        case DOUBLE -> Double.parseDouble(arguments.get(i).getValue());
                        case STRING -> arguments.get(i).getValue();
                        case BOOL -> Boolean.parseBoolean(arguments.get(i).getValue());
                        case REFERENCE -> storedObjects.get(arguments.get(i).getValue());
                        default -> null;
                    };
                }


                Method method = obj.getClass().getMethod(methodName, classArguments);
                result = method.invoke(obj,classArgumentValues);
            } catch (Exception e) {
                Logger.printError("No method '"+methodName+"' found under referenced object '"+name+"'.");
                e.printStackTrace();
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            return objectToInternalValue(result);
        }
        else if(matchesTreeSignature(tree, Lexer.LexerTokenType.ID, Lexer.LexerTokenType.EXPRESSION, Lexer.LexerTokenType.CONTEXT)){
            switch (tree.getValue()){
                case "if" -> {
                    InternalValue condition = evaluateExpression(tree.getRightChildNode(), context);
                    condition = getVariableValue(condition, context);

                    if(condition.getType() == InternalValue.ValueType.BOOL && condition.getValue().equals("true")){
                        InternalValue value = executeNodes(Parser.parseContext(tree.getRightChildNode().getRightChildNode()), localContext);
                        if(value.isReturned()){
                            return value;
                        }
                        lastStatement =  "if_success";
                    }
                    else{
                        lastStatement =  "if_fail";
                    }

                    return new InternalValue(InternalValue.ValueType.NONE);
                }
                case "elif" -> {
                    InternalValue condition = evaluateExpression(tree.getRightChildNode(), context);
                    condition = getVariableValue(condition, context);

                    if(lastStatement.equals("if_fail") && condition.getType() == InternalValue.ValueType.BOOL && condition.getValue().equals("true")){
                        InternalValue value = executeNodes(Parser.parseContext(tree.getRightChildNode().getRightChildNode()), localContext);
                        if(value.isReturned()){
                            return value;
                        }
                        lastStatement =  "if_success";
                    }

                    return new InternalValue(InternalValue.ValueType.NONE);
                }
                case "while" -> {
                    InternalValue condition = evaluateExpression(tree.getRightChildNode(), context);

                    while(condition.getType() == InternalValue.ValueType.BOOL && condition.getValue().equals("true")){
                        InternalValue value = executeNodes(Parser.parseContext(tree.getRightChildNode().getRightChildNode()), localContext);
                        if(value.isReturned()){
                            return value;
                        }

                        condition = evaluateExpression(tree.getRightChildNode(), context);
                    }

                    return new InternalValue(InternalValue.ValueType.NONE);
                }
                case "for" -> {
                    ASTreeNode current = tree.getRightChildNode();
                    ArrayList<LexerToken> tokens = Lexer.lexData(current.getValue().substring(1,current.getValue().length()-1));

                    ArrayList<ArrayList<LexerToken>> individialArgumentTokens = new ArrayList<>();
                    individialArgumentTokens.add(new ArrayList<>());

                    for(LexerToken token: tokens){
                        if(token.getType() == Lexer.LexerTokenType.END_EXPRESSION){
                            individialArgumentTokens.add(new ArrayList<>());
                            continue;
                        }

                        individialArgumentTokens.get(individialArgumentTokens.size()-1).add(token);
                    }

                    if(individialArgumentTokens.size() != 3){
                        Logger.printError("Invalid body for 'for' loop.");
                        return new InternalValue(InternalValue.ValueType.NONE);
                    }

                    ArrayList<LexerToken> initializeStatement = individialArgumentTokens.get(0);
                    ArrayList<LexerToken> repeatCondition = individialArgumentTokens.get(1);
                    ArrayList<LexerToken> repeatedStatement = individialArgumentTokens.get(2);

                    executeNodes(Parser.parseAbstractSyntaxTrees(initializeStatement), localContext);

                    InternalValue repeatConditionResult = evaluateExpression(Parser.parseAbstractSyntaxTrees(repeatCondition).get(0), localContext);
                    while(repeatConditionResult.getType() == InternalValue.ValueType.BOOL && repeatConditionResult.getValue().equals("true")){
                        InternalValue value = executeNodes(Parser.parseContext(tree.getRightChildNode().getRightChildNode()), localContext);

                        if(value.isReturned()){
                            return value;
                        }

                        executeNodes(Parser.parseAbstractSyntaxTrees(repeatedStatement), localContext);
                        repeatConditionResult = evaluateExpression(Parser.parseAbstractSyntaxTrees(repeatCondition).get(0), localContext);
                    }

                    return new InternalValue(InternalValue.ValueType.NONE);
                }
                default -> {

                }
            }
        }
        else if(matchesTreeSignature(tree, Lexer.LexerTokenType.ID, Lexer.LexerTokenType.ID, Lexer.LexerTokenType.EXPRESSION, Lexer.LexerTokenType.CONTEXT)){
            if(!tree.getValue().equals("function")){
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            String name = tree.getRightChildNode().getValue();

            ASTreeNode current = tree.getRightChildNode().getRightChildNode();
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

            ArrayList<String> arguments = new ArrayList<>();

            for (ArrayList<LexerToken> argumentTokens: individialArgumentTokens){
                if(argumentTokens.size() == 0){
                    break;
                }
                ArrayList<ASTreeNode> nodes = Parser.parseAbstractSyntaxTrees(argumentTokens);

                if(nodes.size() != 1){
                    Logger.printError("Illegal name for function argument.");
                    return new InternalValue(InternalValue.ValueType.NONE);
                }

                arguments.add(nodes.get(0).getValue());
            }

            ASTreeNode core = tree.getRightChildNode().getRightChildNode().getRightChildNode();

            context.addFunction(name, new InterpreterFunction(this, context){
                @Override
                protected InternalValue internalExecute(ArrayList<InternalValue> values, InterpreterContext context) {
                    InterpreterContext localFunctionContext = new InterpreterContext(context);

                    if(values.size() != arguments.size()){
                        Logger.printError("Invalid arguments for function " + name);
                        return new InternalValue(InternalValue.ValueType.NONE);
                    }

                    for(int i = 0;i < arguments.size();i++){
                        localFunctionContext.setVariable(arguments.get(i), values.get(i));
                    }

                    return executeNodes(Parser.parseContext(core), localFunctionContext);
                }
            });
        }
        else if (matchesTreeSignature(tree, Lexer.LexerTokenType.ID, Lexer.LexerTokenType.CONTEXT)  && tree.getValue().equals("else")) {
            if(lastStatement.equals("")){
                Logger.printError("Else statement missing if before it.");
            }
            if(lastStatement.equals("if_fail")){
                executeNodes(Parser.parseContext(tree.getRightChildNode()), localContext);
                lastStatement = "";
            }
        }

        //Parser.displayTree(tree);
        if(!foundStatement && tree.getType() == Lexer.LexerTokenType.ID
                && !context.hasVariable(tree.getValue())
                && tree.getRightChildNode() != null && tree.getRightChildNode().getType() == Lexer.LexerTokenType.EXPRESSION
        ){
            if(!context.hasFunction(tree.getValue())){
                Logger.printError("No function: " + tree.getValue() + " found in this context.");
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            ArrayList<InternalValue> arguments = parseArgumentsFromContext(tree.getRightChildNode(), context);

            return context.getFunction(tree.getValue()).externalExecute(arguments, context);
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

                    endValue = evaluateExpression(nodes.get(0), context);
                    return endValue;
                }
                case IDENTIFIER_EXPRESSION -> {
                    return  parseIdentifierExpression(tree,context);
                }
            }


            endValue = new InternalValue(type, value);
        }

        return endValue;
    }

    public InternalValue parseIdentifierExpression(ASTreeNode tree, InterpreterContext context){
        ArrayList<ASTreeNode> nodes = Parser.parseContext(tree);

        InternalValue endValue = evaluateExpression(nodes.get(0), context);
        endValue = getVariableValue(endValue, context);

        return new InternalValue(InternalValue.ValueType.ID,endValue.getValue());
    }

    public InternalValue objectToInternalValue(Object result){
        if(result instanceof Integer){
            return new InternalValue(InternalValue.ValueType.INT, ((int)result) + "");
        }
        else if(result instanceof Double){
            return new InternalValue(InternalValue.ValueType.DOUBLE, ((double)result) + "");
        }
        else if(result instanceof String){
            return new InternalValue(InternalValue.ValueType.STRING, ((String)result));
        }
        else if(result instanceof Boolean){
            return new InternalValue(InternalValue.ValueType.BOOL, ((boolean)result) + "");
        }
        else{
            return generateReferenceForObject(result);
        }
    }

    public ArrayList<InternalValue> parseArgumentsFromContext(ASTreeNode tree, InterpreterContext context){
        ArrayList<LexerToken> tokens = Lexer.lexData(tree.getValue().substring(1, tree.getValue().length()-1));

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
                return new ArrayList<>();
            }

            InternalValue value = evaluateExpression(nodes.get(0), context);
            //value = getVariableValue(value, context);

            arguments.add(value);
        }

        return arguments;
    }

    public InternalValue getVariableValue(InternalValue id, InterpreterContext context){
        if(!context.hasVariable(id.getValue())){
            if(id.getType() == InternalValue.ValueType.ID){
                return new InternalValue(InternalValue.ValueType.NONE);
            }

            return id;
        }

        if(id.getType() == InternalValue.ValueType.ID) {
            id = context.getVariable(id.getValue());
        }

        return id;
    }

    private InternalValue evaluateOperation(InternalValue value1, InternalValue value2, String operation, InterpreterContext context){
        if(assignedOperatorsToFunctions.containsKey(operation)){
            return assignedOperatorsToFunctions.get(operation).externalExecute(new ArrayList<>(List.of(value1,value2)), context);
        }

        switch (operation){
            case "=" -> {
                if(value1.getType() != InternalValue.ValueType.ID){
                    return new InternalValue(InternalValue.ValueType.BOOL, "false");
                }
                value2 = getVariableValue(value2, context);

                context.setVariable(value1.getValue(), value2);
                return new InternalValue(InternalValue.ValueType.BOOL, "true");
            }
            case "+=" -> {
                if(value1.getType() != InternalValue.ValueType.ID){
                    Logger.printError("Invalid use of the '+=' operator.");
                    return new InternalValue(InternalValue.ValueType.BOOL, "false");
                }

                value2 = getVariableValue(value2, context);

                context.setVariable(value1.getValue(), context.getFunction("add").externalExecute(new ArrayList<>(List.of(value1,value2)), context));
                return new InternalValue(InternalValue.ValueType.BOOL, "true");
            }
            case "-=" -> {
                if(value1.getType() != InternalValue.ValueType.ID){
                    Logger.printError("Invalid use of the '-=' operator.");
                    return new InternalValue(InternalValue.ValueType.BOOL, "false");
                }

                value2 = getVariableValue(value2, context);

                context.setVariable(value1.getValue(), context.getFunction("subtract").externalExecute(new ArrayList<>(List.of(value1,value2)), context));
                return new InternalValue(InternalValue.ValueType.BOOL, "true");
            }
            case "*=" -> {
                if(value1.getType() != InternalValue.ValueType.ID){
                    Logger.printError("Invalid use of the '*=' operator.");
                    return new InternalValue(InternalValue.ValueType.BOOL, "false");
                }

                value2 = getVariableValue(value2, context);

                context.setVariable(value1.getValue(), context.getFunction("multiply").externalExecute(new ArrayList<>(List.of(value1,value2)), context));
                return new InternalValue(InternalValue.ValueType.BOOL, "true");
            }
            case "/=" -> {
                if(value1.getType() != InternalValue.ValueType.ID){
                    Logger.printError("Invalid use of the '/=' operator.");
                    return new InternalValue(InternalValue.ValueType.BOOL, "false");
                }

                value2 = getVariableValue(value2, context);

                context.setVariable(value1.getValue(), context.getFunction("divide").externalExecute(new ArrayList<>(List.of(value1,value2)), context));
                return new InternalValue(InternalValue.ValueType.BOOL, "true");
            }
            default -> {
                return new InternalValue(InternalValue.ValueType.NONE);
            }
        }
    }

    public void addFunction(String name, InterpreterFunction function){
        globalContext.addFunction(name,function);
    }

    private boolean matchesTreeSignature(ASTreeNode tree,Lexer.LexerTokenType... types){
        int i = 0;

        ASTreeNode current = tree;
        while (i < types.length){
            if(current.getType() != types[i]){
                return false;
            }
            if(current.getRightChildNode() == null && i + 1 < types.length){
                return false;
            }
            current = current.getRightChildNode();

            i++;
        }
        return true;
    }
}
