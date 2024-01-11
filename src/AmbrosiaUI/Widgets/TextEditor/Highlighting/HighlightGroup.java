package AmbrosiaUI.Widgets.TextEditor.Highlighting;

import AmbrosiaUI.Utility.Position;

public class HighlightGroup implements Comparable<HighlightGroup>{
    private String content;
    private String backgroundColor;
    private String foregroundColor;
    private int zIndex;

    private Position position;

    public HighlightGroup(String content, String backgroundColor, String foregroundColor, int zIndex, Position position) {
        this.content = content;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.zIndex = zIndex;
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    @Override
    public int compareTo(HighlightGroup o) {
        return Integer.compare(this.zIndex,o.getzIndex());
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getX(){
        return this.position.x;
    }
    public int getY(){
        return this.position.y;
    }

    @Override
    public String toString() {
        return "HighlightGroup{" +
                "content='" + content + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", foregroundColor='" + foregroundColor + '\'' +
                ", zIndex=" + zIndex +
                ", position=" + position +
                '}';
    }
}
