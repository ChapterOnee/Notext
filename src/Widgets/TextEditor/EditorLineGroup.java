package Widgets.TextEditor;

import Widgets.Theme;

public class EditorLineGroup{
    public String backgroundColor;
    public String foregroundColor;

    private String text;

    public EditorLineGroup(String text) {
        this.backgroundColor = "primary";
        this.foregroundColor = "text1";
        this.text = text;
    }

    public EditorLineGroup(String text, String backgroundColor, String foregroundColor) {
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.text = text;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addText(String text){
        this.setText(this.getText()+text);
    }

    @Override
    public String toString() {
        return "Widgets.TextEditor.EditorLineGroup{" +
                "backgroundColor=" + backgroundColor +
                ", foregroundColor=" + foregroundColor +
                ", text='" + text + '\'' +
                '}';
    }
}
