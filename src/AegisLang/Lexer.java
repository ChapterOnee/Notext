package AegisLang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private HashMap<String, LexerTokenType> tokenTypes = new HashMap<>();
    public enum LexerTokenType {
        OPERATION,

        CONTEXT_OPENER,
        CONTEXT_CLOSER,
        CONTEXT,
        GROUP_OPENER,
        GROUP_CLOSER,
        END_EXPRESSION,
        NUMBER,
        ID,
    }

    public Lexer() {
        tokenTypes.put("==", LexerTokenType.OPERATION);
        tokenTypes.put("=|\\+|-|\\*|/", LexerTokenType.OPERATION);
        tokenTypes.put("\\d+", LexerTokenType.NUMBER);
        tokenTypes.put(";", LexerTokenType.END_EXPRESSION);
        tokenTypes.put("[a-zA-Z_]+", LexerTokenType.ID);
        tokenTypes.put("\\(.*\\)", LexerTokenType.CONTEXT);
        tokenTypes.put("\\{.*\\}", LexerTokenType.CONTEXT);

        /*tokenTypes.put("\\(", LexerTokenType.CONTEXT_OPENER);
        tokenTypes.put("\\)", LexerTokenType.CONTEXT_CLOSER);
        tokenTypes.put("\\{", LexerTokenType.GROUP_OPENER);
        tokenTypes.put("\\}", LexerTokenType.GROUP_CLOSER);*/
    }

    public ArrayList<LexerToken> lexData(String data){
        StringBuilder operationsPattern = new StringBuilder();

        for (String op: tokenTypes.keySet()){
            operationsPattern.append(op).append("|");
        }
        operationsPattern.deleteCharAt(operationsPattern.length()-1);

        Pattern pattern = Pattern.compile(String.join("|",operationsPattern.toString()), Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(data);

        ArrayList<AegisLang.LexerToken> tokens = new ArrayList<>();

        LexerTokenType operation = LexerTokenType.END_EXPRESSION;
        while(matcher.find()){
            for(String op: tokenTypes.keySet()){
                if(matcher.group().matches(op)){
                    operation = tokenTypes.get(op);
                    break;
                }
            }

            tokens.add(new AegisLang.LexerToken(matcher.group(), operation));
        }

        /*for(LexerToken token: tokens){
            System.out.println(token);
        }*/

        return tokens;
    }
}
