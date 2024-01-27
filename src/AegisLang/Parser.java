package AegisLang;

import javax.print.MultiDocPrintService;
import java.util.ArrayList;

public class Parser {
    public void parseAbstractSyntaxTrees(ArrayList<LexerToken> tokens){
        ArrayList<LexerToken> keyOperationTokens = new ArrayList<>();

        boolean blocked = false;
        for(LexerToken token: tokens){
            if(token.getType() == Lexer.LexerTokenType.OPERATION && !blocked){
                blocked = true;
                keyOperationTokens.add(token);
            }
            if(blocked && token.getType() == Lexer.LexerTokenType.END_EXPRESSION){
                blocked = false;
            }
        }
        ArrayList<ASTreeNode> keyNodes = new ArrayList<>();
        for(LexerToken token: keyOperationTokens){
            ASTreeNode node = new ASTreeNode(token);
            keyNodes.add(node);

            ArrayList<LexerToken> usedTokens = new ArrayList<>();
            usedTokens.add(token);
            addChildNodes(tokens, node, tokens.indexOf(token), usedTokens);
        }

        System.out.println(keyNodes);
    }

    private void addChildNodes(ArrayList<LexerToken> tokens, ASTreeNode coreNode, int coreNodeIndex, ArrayList<LexerToken> usedTokens){
        /*
            Everything up/left from the operations
         */
        boolean foundLeft = false;
        for(int j = coreNodeIndex-1;j >= 0;j--){
            if(usedTokens.contains(tokens.get(j)) || tokens.get(j).getType() == Lexer.LexerTokenType.END_EXPRESSION){
                break;
            }

            if(tokens.get(j).getType() == Lexer.LexerTokenType.OPERATION){
                ASTreeNode node = new ASTreeNode(tokens.get(j));
                usedTokens.add(tokens.get(j));
                addChildNodes(tokens, node, j, usedTokens);
                coreNode.addChildNodeLeft(node);
                foundLeft = true;
                break;
            }
        }

        if(!foundLeft){
            for(int j = coreNodeIndex-1;j >= 0;j--){
                if(usedTokens.contains(tokens.get(j)) || tokens.get(j).getType() == Lexer.LexerTokenType.END_EXPRESSION){
                    break;
                }
                coreNode.addChildNodeLeft(new ASTreeNode(tokens.get(j)));
            }
        }

        /*
            Everything down/right from the operations
         */
        boolean foundRight = false;
        for(int j = coreNodeIndex+1;j < tokens.size();j++){
            if(usedTokens.contains(tokens.get(j)) || tokens.get(j).getType() == Lexer.LexerTokenType.END_EXPRESSION){
                break;
            }

            if(tokens.get(j).getType() == Lexer.LexerTokenType.OPERATION){
                ASTreeNode node = new ASTreeNode(tokens.get(j));
                usedTokens.add(tokens.get(j));
                addChildNodes(tokens, node, j, usedTokens);
                coreNode.addChildNodeRight(node);
                foundRight = true;
                break;
            }
        }

        if(!foundRight){
            for(int j = coreNodeIndex+1;j < tokens.size();j++){
                if(usedTokens.contains(tokens.get(j)) || tokens.get(j).getType() == Lexer.LexerTokenType.END_EXPRESSION){
                    break;
                }
                coreNode.addChildNodeRight(new ASTreeNode(tokens.get(j)));
            }
        }
    }
}
