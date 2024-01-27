package AegisLang;

public class LexerToken {
    private String content;
    private Lexer.LexerOperation operation;

    public LexerToken(String content, Lexer.LexerOperation operation) {
        this.content = content;
        this.operation = operation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Lexer.LexerOperation getOperation() {
        return operation;
    }

    public void setOperation(Lexer.LexerOperation operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "LexerToken{" +
                "content='" + content + '\'' +
                ", operation=" + operation +
                '}';
    }
}
