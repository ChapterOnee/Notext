package AegisLang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private HashMap<String, LexerOperation> operations = new HashMap<>();
    public enum LexerOperation {
        MULTIPLY,
        ADD,
        SUBTRACT,
        DIVIDE,
        SET,
        END_EXPRESSION,
        NUMBER
    }

    public Lexer() {
        operations.put("=", LexerOperation.SET);
        operations.put("\\*", LexerOperation.MULTIPLY);
        operations.put("-", LexerOperation.SUBTRACT);
        operations.put("\\+", LexerOperation.ADD);
        operations.put("/", LexerOperation.DIVIDE);
        operations.put("\\d+", LexerOperation.NUMBER);
        operations.put(";", LexerOperation.END_EXPRESSION);
    }

    public void lexData(String data){
        StringBuilder operationsPattern = new StringBuilder();

        for (String op: operations.keySet()){
            operationsPattern.append(Pattern.quote(op)).append("|");
        }
        operationsPattern.deleteCharAt(operationsPattern.length()-1);

        Pattern pattern = Pattern.compile(String.join("|",operationsPattern.toString()));
        Matcher matcher = pattern.matcher(data);

        ArrayList<LexerToken> tokens = new ArrayList<>();

        int last = 0;
        String content;
        LexerOperation operation = LexerOperation.END_EXPRESSION;
        while(matcher.find()){
            content = data.substring(last, matcher.start());

            for(String op: operations.keySet()){
                if(matcher.group().matches(op)){
                    operation = operations.get(op);
                    break;
                }
            }

            tokens.add(new LexerToken(content, operation));

            last = matcher.end();
        }

        System.out.println(tokens);
    }
}
