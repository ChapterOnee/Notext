package AegisLang;

import AmbrosiaUI.Utility.Logger;

import javax.print.MultiDocPrintService;
import java.util.ArrayList;
import java.util.Collections;

public class Parser {
    public void parseAbstractSyntaxTrees(ArrayList<LexerToken> tokens){
        ArrayList<ArrayList<LexerToken>> expressionTokens = new ArrayList<>();
        expressionTokens.add(new ArrayList<>());

        for(LexerToken token: tokens){
            if(token.getType() == Lexer.LexerTokenType.END_EXPRESSION) {
                expressionTokens.add(new ArrayList<>());
                continue;
            }

            expressionTokens.get(expressionTokens.size()-1).add(token);
        }

        ArrayList<ASTreeNode> keyNodes = new ArrayList<>();
        for(ArrayList<LexerToken> expression: expressionTokens){
            keyNodes.add(generateTreeFromExpression(expression));
        }

        System.out.println(keyNodes);
    }

    public ASTreeNode generateTreeFromExpression(ArrayList<LexerToken> tokens){
        /*
            Find all contexts
         */

        ArrayList<ASTreeNode> nodesWithContexts = new ArrayList<>();
        for(int i = 0;i < tokens.size();i++){
            if(tokens.get(i).getType() == Lexer.LexerTokenType.CONTEXT_OPENER){
                for(int j = tokens.size()-1; j >= i;j--){
                    if(tokens.get(j).getType() == Lexer.LexerTokenType.CONTEXT_CLOSER){
                        System.out.println("Found context" + i + "->" + j);
                        ASTreeNode nwNode = generateTreeFromExpression(new ArrayList<>(tokens.subList(i,j)));
                        nwNode.setType(Lexer.LexerTokenType.CONTEXT);
                        nodesWithContexts.add(nwNode);
                        i = j;
                    }
                }
                continue;
            }

            nodesWithContexts.add(new ASTreeNode(tokens.get(i)));
        }

        ASTreeNode outNode = new ASTreeNode(null,null);

        /*
            Find operation with highest priority
         */
        ArrayList<ASTreeNode> foundOperations = new ArrayList<>();
        for(ASTreeNode node:  nodesWithContexts){
            if(node.getType() == Lexer.LexerTokenType.OPERATION){
                foundOperations.add(node);
            }
        }
        Collections.sort(foundOperations);

        if(foundOperations.isEmpty()){
            if(nodesWithContexts.isEmpty()){
                return outNode;
            }
            outNode.setType(nodesWithContexts.get(0).getType());
            outNode.setValue(nodesWithContexts.get(0).getValue());
            return outNode;
        }
        outNode.setType(foundOperations.get(0).getType());
        outNode.setValue(foundOperations.get(0).getValue());
        /*
            Generate left and right nodes
         */
        ArrayList<LexerToken> leftTokens = new ArrayList<>();
        for(int i = nodesWithContexts.indexOf(foundOperations.get(0))-1;i >= 0;i--){
            leftTokens.add(new LexerToken(nodesWithContexts.get(i)));
        }
        outNode.addChildNodeLeft(generateTreeFromExpression(leftTokens));

        // Right
        ArrayList<LexerToken> rightTokens = new ArrayList<>();
        for(int i = nodesWithContexts.indexOf(foundOperations.get(0))+1;i < nodesWithContexts.size();i++){
            rightTokens.add(new LexerToken(nodesWithContexts.get(i)));
        }
        outNode.addChildNodeRight(generateTreeFromExpression(rightTokens));

        return outNode;
    }
}
