package AegisLang;

import AmbrosiaUI.Utility.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private static LinkedHashMap<String, LexerTokenType> tokenTypes = new LinkedHashMap<>();
    public enum LexerTokenType {
        OPERATION,
        EXPRESSION,
        CONTEXT,

        IDENTIFIER_EXPRESSION,
        END_EXPRESSION,
        NUMBER,
        ID,
        STRING,
        BOOL,
        ARGUMENT_SPLITTER,
        AFTER_OPERATION
    }

    private static void initializeTokenTypes(){
        tokenTypes.put(",", LexerTokenType.ARGUMENT_SPLITTER);

        tokenTypes.put("-?\\d+", LexerTokenType.NUMBER);
        tokenTypes.put("(\\\"[^\\\"]*\\\")|'[^']*'", LexerTokenType.STRING);
        tokenTypes.put("true|false", LexerTokenType.BOOL);

        tokenTypes.put("\\+\\+|--", LexerTokenType.AFTER_OPERATION);
        tokenTypes.put("==|&&|\\|\\|", LexerTokenType.OPERATION);
        tokenTypes.put(">=|<=", LexerTokenType.OPERATION);
        tokenTypes.put(">|<", LexerTokenType.OPERATION);
        tokenTypes.put("\\+=|-=|\\*=|/=", LexerTokenType.OPERATION);
        tokenTypes.put("=|\\+|-|\\*|/", LexerTokenType.OPERATION);
        tokenTypes.put(";", LexerTokenType.END_EXPRESSION);
        tokenTypes.put("[a-zA-Z_.0-9]+", LexerTokenType.ID);


        tokenTypes.put("\\(", LexerTokenType.EXPRESSION);
        tokenTypes.put("\\{", LexerTokenType.CONTEXT);
        tokenTypes.put("\\[", LexerTokenType.IDENTIFIER_EXPRESSION);
    }

    public Lexer() {
        //tokenTypes.put("\\}", LexerTokenType.GROUP_CLOSER);
    }

    public static ArrayList<LexerToken> lexData(String data){
        if(tokenTypes.isEmpty()){
            initializeTokenTypes();
        }

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

            if(matcher.group().matches("[({\\[]")){
                ArrayList<Character> contextOpeners = new ArrayList<>();
                StringBuilder dataInContext = new StringBuilder();

                int i = 0;
                char firstOpener = ' ';
                do{
                    if(i+matcher.start() >= data.length()){
                        Logger.printError("\t " + data.substring(matcher.start(), matcher.start()+5));
                        Logger.printError("\t ^^^^^ Unclosed '"+contextOpeners.get(contextOpeners.size()-1) + "'. ");
                        return new ArrayList<>();
                    }
                    char currentChar = data.charAt(i+matcher.start());
                    i++;
                    dataInContext.append(currentChar);

                    if(currentChar == '(' || currentChar == '{' || currentChar == '['){
                        if(firstOpener == ' '){
                            firstOpener  = currentChar;
                        }
                        contextOpeners.add(currentChar);
                        continue;
                    }

                    if (
                            (contextOpeners.get(contextOpeners.size() - 1).equals('(') && currentChar == ')') ||
                            (contextOpeners.get(contextOpeners.size() - 1).equals('{') && currentChar == '}') ||
                                    (contextOpeners.get(contextOpeners.size() - 1).equals('[') && currentChar == ']')
                    ) {
                        contextOpeners.remove(contextOpeners.size() - 1);
                    }
                }while(contextOpeners.size() > 0);

                contextOffset = i+matcher.start();

                LexerTokenType contextType;
                if(firstOpener == '('){
                    contextType = LexerTokenType.EXPRESSION;
                }
                else if(firstOpener == '{'){
                    contextType = LexerTokenType.CONTEXT;
                }
                else{
                    contextType = LexerTokenType.IDENTIFIER_EXPRESSION;
                }
                tokens.add(new AegisLang.LexerToken(dataInContext.toString(), contextType));
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

            if(matcher.group().equals("++") || matcher.group().equals("--")){
                tokens.add(new AegisLang.LexerToken(matcher.group().equals("++") ? "+=" : "-=",LexerTokenType.OPERATION));
                tokens.add(new LexerToken("1",LexerTokenType.NUMBER));
                continue;
            }
            tokens.add(new AegisLang.LexerToken(matcher.group(), operation));
        }
        /*for(LexerToken token: tokens){
            System.out.println(token);
        }*/
        return tokens;
    }


}
