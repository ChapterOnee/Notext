package Dissimulo;

public class ASTreeNode implements Comparable<ASTreeNode>{
    private Lexer.LexerTokenType type;
    private String value;

    private ASTreeNode leftChildNode;
    private ASTreeNode rightChildNode;

    public ASTreeNode(Lexer.LexerTokenType type, String value) {
        this.type = type;
        this.value = value;
    }
    public ASTreeNode(LexerToken token){
        this.type = token.getType();
        this.value = token.getContent();
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

    public ASTreeNode getLeftChildNode() {
        return leftChildNode;
    }

    public void setLeftChildNode(ASTreeNode leftChildNode) {
        this.leftChildNode = leftChildNode;
    }

    public ASTreeNode getRightChildNode() {
        return rightChildNode;
    }

    public void setRightChildNode(ASTreeNode rightChildNode) {
        this.rightChildNode = rightChildNode;
    }

    @Override
    public String toString() {
        return (leftChildNode == null ? "" : leftChildNode+"<-")+"("+type+":"+value+")"+(rightChildNode == null ? "" : "->" + rightChildNode);
    }

    public boolean hasChildren(){
        return leftChildNode != null || rightChildNode != null;
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
