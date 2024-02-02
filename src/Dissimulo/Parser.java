package Dissimulo;

import java.util.ArrayList;
import java.util.Collections;

public class Parser {

    public static ArrayList<ASTreeNode> parseContext(ASTreeNode context){
        return parseAbstractSyntaxTrees(Lexer.lexData(context.getValue().substring(1,context.getValue().length()-1)));
    }
    public static ArrayList<ASTreeNode> parseAbstractSyntaxTrees(ArrayList<LexerToken> tokens){
        ArrayList<ArrayList<LexerToken>> expressionTokens = new ArrayList<>();
        expressionTokens.add(new ArrayList<>());

        LexerToken lastToken = null;
        for(LexerToken token: tokens){
            if(token.getType() == Lexer.LexerTokenType.END_EXPRESSION) {
                expressionTokens.add(new ArrayList<>());
                continue;
            }
            else if(lastToken != null && lastToken.getType() == Lexer.LexerTokenType.CONTEXT && token.getType() == Lexer.LexerTokenType.ID){
                lastToken = token;
                expressionTokens.add(new ArrayList<>());
                expressionTokens.get(expressionTokens.size()-1).add(token);
                continue;
            }

            expressionTokens.get(expressionTokens.size()-1).add(token);
            lastToken = token;
        }

        ArrayList<ASTreeNode> keyNodes = new ArrayList<>();
        for(ArrayList<LexerToken> expression: expressionTokens){
            if (expression.isEmpty()){
                continue;
            }
            keyNodes.add(generateTreeFromExpression(expression));
        }


        /*for (ASTreeNode node: keyNodes) {
            displayTree(node);
        }*/

        return keyNodes;
    }

    private static ASTreeNode generateTreeFromExpression(ArrayList<LexerToken> tokens){
        /*
            Find all contexts
         */
        ASTreeNode outNode = new ASTreeNode(null,null);

        if(tokens.size() == 0){
            return outNode;
        }
        /*
            Find operation with highest priority
         */
        ArrayList<LexerToken> foundOperations = new ArrayList<>();
        for (LexerToken lexerToken : tokens) {
            if (lexerToken.getType() == Lexer.LexerTokenType.END_EXPRESSION) {
                break;
            }

            if (lexerToken.getType() == Lexer.LexerTokenType.OPERATION) {
                foundOperations.add(lexerToken);
            }
        }
        Collections.reverse(foundOperations);

        if(foundOperations.isEmpty()){
            if(tokens.size() > 1){
                outNode.setRightChildNode(generateTreeFromExpression(new ArrayList<>(tokens.subList(1,tokens.size()))));
            }
            outNode.setType(tokens.get(0).getType());
            outNode.setValue(tokens.get(0).getContent());
            return outNode;
        }

        LexerToken highestPriorityToken = foundOperations.get(0);
        for(LexerToken token: foundOperations){
            if(token.getOperationPriority() > highestPriorityToken.getOperationPriority()){
                highestPriorityToken = token;
            }
        }

        outNode.setType(highestPriorityToken.getType());
        outNode.setValue(highestPriorityToken.getContent());


        /*
            Generate left and right nodes
         */
        ArrayList<LexerToken> leftTokens = new ArrayList<>();
        for(int i = tokens.indexOf(highestPriorityToken)-1;i >= 0;i--){
            leftTokens.add(tokens.get(i));
        }
        Collections.reverse(leftTokens);
        outNode.setLeftChildNode(generateTreeFromExpression(leftTokens));

        // Right
        ArrayList<LexerToken> rightTokens = new ArrayList<>();
        for(int i = tokens.indexOf(highestPriorityToken)+1;i < tokens.size();i++){
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

    public static void displayTree(ASTreeNode node){
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
    private static void addToDisplayTree(ASTreeNode node, int depth, ArrayList<DisplayTreeNode> lines, int offset){
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

    private static String nodeToString(ASTreeNode node){
        return (node.getLeftChildNode() != null ? "┌ " : "") + node.getType() + ":" + node.getValue() + (node.getRightChildNode() != null ? " ┐" : "");
    }
}
