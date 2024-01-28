package AegisLang;

import java.util.ArrayList;

public class ASTreeNode implements Comparable<ASTreeNode>{
    private Lexer.LexerTokenType type;
    private String value;

    private ArrayList<ASTreeNode> leftChildNodes = new ArrayList<>();
    private ArrayList<ASTreeNode> rightChildNodes = new ArrayList<>();

    public ASTreeNode(Lexer.LexerTokenType type, String value) {
        this.type = type;
        this.value = value;
    }
    public ASTreeNode(LexerToken token){
        this.type = token.getType();
        this.value = token.getContent();
    }

    public void addChildNodeLeft(ASTreeNode node){
        leftChildNodes.add(node);
    }
    public void addChildNodeRight(ASTreeNode node){
        rightChildNodes.add(node);
    }

    public Lexer.LexerTokenType getType() {
        return type;
    }

    public void setType(Lexer.LexerTokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<ASTreeNode> getLeftChildNodes() {
        return leftChildNodes;
    }

    public void setLeftChildNodes(ArrayList<ASTreeNode> leftChildNodes) {
        this.leftChildNodes = leftChildNodes;
    }

    public ArrayList<ASTreeNode> getRightChildNodes() {
        return rightChildNodes;
    }

    public void setRightChildNodes(ArrayList<ASTreeNode> rightChildNodes) {
        this.rightChildNodes = rightChildNodes;
    }

    @Override
    public String toString() {
        return (leftChildNodes.isEmpty() ? "" : leftChildNodes+"<-")+"("+type+" "+value+")"+(rightChildNodes.isEmpty()?"":"->"+rightChildNodes);
    }

    public int getOperationPriority(){
        return switch (value){
            case "+" -> 0;
            case "-" -> 0;
            case "*" -> 1;
            case "/" -> 1;
            case "=" -> 2;
            default -> 0;
        };
    }
    @Override
    public int compareTo(ASTreeNode o) {
        return Integer.compare(o.getOperationPriority(), getOperationPriority());
    }
}
