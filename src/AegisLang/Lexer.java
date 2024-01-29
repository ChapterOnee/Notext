package AegisLang;

import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
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
        tokenTypes.put("==|&&|\\|\\|", LexerTokenType.OPERATION);
        tokenTypes.put("=|\\+|-|\\*|/", LexerTokenType.OPERATION);
        tokenTypes.put("\\d+", LexerTokenType.NUMBER);
        tokenTypes.put(";", LexerTokenType.END_EXPRESSION);
        tokenTypes.put("[a-zA-Z_]+", LexerTokenType.ID);

        tokenTypes.put("\\(", LexerTokenType.CONTEXT_OPENER);
        //tokenTypes.put("\\)", LexerTokenType.CONTEXT_CLOSER);
        tokenTypes.put("\\{", LexerTokenType.GROUP_OPENER);
        //tokenTypes.put("\\}", LexerTokenType.GROUP_CLOSER);
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

        int contextOffset = 0;
        while(matcher.find()){
            if (matcher.start() < contextOffset){
                continue;
            }

            if(matcher.group().matches("[({]")){
                ArrayList<Character> contextOpeners = new ArrayList<>();
                StringBuilder dataInContext = new StringBuilder();

                int i = 0;
                do{
                    if(i+matcher.start() >= data.length()){
                        Logger.printError("\t " + data.substring(matcher.start(), matcher.start()+5));
                        Logger.printError("\t ^^^^^ Unclosed '"+contextOpeners.get(contextOpeners.size()-1) + "'. ");
                        return new ArrayList<>();
                    }
                    char currentChar = data.charAt(i+matcher.start());
                    i++;
                    dataInContext.append(currentChar);

                    if(currentChar == '(' || currentChar == '{'){
                        contextOpeners.add(currentChar);
                        continue;
                    }

                    if (
                            (contextOpeners.get(contextOpeners.size() - 1).equals('(') && currentChar == ')') ||
                            (contextOpeners.get(contextOpeners.size() - 1).equals('{') && currentChar == '}')
                    ) {
                        contextOpeners.remove(contextOpeners.size() - 1);
                    }
                }while(contextOpeners.size() > 0);

                contextOffset = i+matcher.start();
                tokens.add(new AegisLang.LexerToken(dataInContext.toString(), LexerTokenType.CONTEXT));
                continue;
            }
            else {
                contextOffset = matcher.end();
            }

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
