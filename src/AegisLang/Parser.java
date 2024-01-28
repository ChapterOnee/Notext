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
            if (expression.isEmpty()){
                continue;
            }
            keyNodes.add(generateTreeFromExpression(expression));
        }

        for (ASTreeNode node: keyNodes) {
            displayTree(node);
        }
    }

    public ASTreeNode generateTreeFromExpression(ArrayList<LexerToken> tokens){
        /*
            Find all contexts
         */

        if(tokens.get(0).getType() == Lexer.LexerTokenType.CONTEXT_OPENER && tokens.get(tokens.size()-1).getType() == Lexer.LexerTokenType.CONTEXT_CLOSER){
            tokens = new ArrayList<>(tokens.subList(1, tokens.size()-1));
        }

        ASTreeNode outNode = new ASTreeNode(null,null);

        /*
            Find operation with highest priority
         */
        ArrayList<LexerToken> foundOperations = new ArrayList<>();
        for(int i = 0;i < tokens.size();i++){
            if(tokens.get(i).getType() == Lexer.LexerTokenType.CONTEXT_OPENER){
                for(int j = tokens.size()-1; j >= i;j--){
                    if(tokens.get(j).getType() == Lexer.LexerTokenType.CONTEXT_CLOSER){
                        i = j;
                        break;
                    }
                }
                continue;
            }

            if(tokens.get(i).getType() == Lexer.LexerTokenType.OPERATION){
                foundOperations.add(tokens.get(i));
            }
        }
        Collections.sort(foundOperations);

        if(foundOperations.isEmpty()){
            outNode.setType(tokens.get(0).getType());
            outNode.setValue(tokens.get(0).getContent());
            return outNode;
        }
        outNode.setType(foundOperations.get(0).getType());
        outNode.setValue(foundOperations.get(0).getContent());
        /*
            Generate left and right nodes
         */
        ArrayList<LexerToken> leftTokens = new ArrayList<>();
        for(int i = tokens.indexOf(foundOperations.get(0))-1;i >= 0;i--){
            leftTokens.add(tokens.get(i));
        }
        Collections.reverse(leftTokens);
        outNode.setLeftChildNode(generateTreeFromExpression(leftTokens));

        // Right
        ArrayList<LexerToken> rightTokens = new ArrayList<>();
        for(int i = tokens.indexOf(foundOperations.get(0))+1;i < tokens.size();i++){
            rightTokens.add(tokens.get(i));
        }
        outNode.setRightChildNode(generateTreeFromExpression(rightTokens));

        return outNode;
    }

    private static class DisplayTreeNode implements Comparable<DisplayTreeNode>{
        private int depth;
        private int offset;
        private ASTreeNode node;

        public DisplayTreeNode(int depth, int offset, ASTreeNode node) {
            this.depth = depth;
            this.offset = offset;
            this.node = node;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public ASTreeNode getNode() {
            return node;
        }

        public void setNode(ASTreeNode node) {
            this.node = node;
        }

        @Override
        public int compareTo(DisplayTreeNode displayTreeNode) {
            return Integer.compare(depth,displayTreeNode.depth);
        }
    }

    public void displayTree(ASTreeNode node){
        ArrayList<DisplayTreeNode> lines = new ArrayList<>();

        addToDisplayTree(node, 0, lines, 0);

        Collections.sort(lines);

        int highestOffset = 0;
        int maxDepth = 0;
        for (DisplayTreeNode nodes: lines){
            highestOffset = Math.min(nodes.getOffset(),highestOffset);
            maxDepth = Math.max(nodes.getDepth(), maxDepth);
        }

        String current;
        int currentOffset = 0;
        for(int i = 0;i <= maxDepth;i++){
            int currentlyUsedSpace = 0;

            for (DisplayTreeNode nodes: lines) {
                if(nodes.getDepth() == i){
                    currentOffset = (highestOffset*-1)+nodes.getOffset()-currentlyUsedSpace;

                    if(currentOffset < 0){
                        currentOffset = 1;
                    }

                    current = new String(new char[currentOffset]).replace("\0"," ") + nodeToString(nodes.getNode());

                    currentlyUsedSpace += current.length();
                    System.out.print(current);
                }
            }

            System.out.println();
        }
    }
    private void addToDisplayTree(ASTreeNode node, int depth, ArrayList<DisplayTreeNode> lines, int offset){
        depth += 1;

        String nd = nodeToString(node);

        if(node.getLeftChildNode() != null){
            addToDisplayTree(node.getLeftChildNode(), depth, lines,offset-nodeToString(node.getLeftChildNode()).length()/2);
        }
        if(node.getRightChildNode() != null){
            addToDisplayTree(node.getRightChildNode(), depth, lines,offset+nd.length()-nodeToString(node.getRightChildNode()).length()/2);
        }

        lines.add(new DisplayTreeNode(depth, offset, node));
    }

    private String nodeToString(ASTreeNode node){
        return (node.getLeftChildNode() != null ? "┌ " : "") + node.getType() + ":" + node.getValue() + (node.getRightChildNode() != null ? " ┐" : "");
    }
}
