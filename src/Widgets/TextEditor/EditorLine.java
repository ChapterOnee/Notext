package Widgets.TextEditor;

import Widgets.TextEditor.Highlighting.Highlighter;
import Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

public class EditorLine{
    private String raw_content;

    public EditorLine(String contents) {
        super();
        this.raw_content = contents;
    }

    public void draw(Graphics2D g2, int x, int y){
        FontMetrics fm = g2.getFontMetrics(g2.getFont());

        g2.drawString(this.getText(),x,y+fm.getHeight());
    }


    public String getText() {
        return raw_content;
    }

    public void setText(String raw_content) {
        this.raw_content = raw_content;
    }

    public void addTextAt(String text, int index){
        this.raw_content = this.raw_content.substring(0,index) + text + this.raw_content.substring(index);
    }

    public void pushText(String text){
        this.raw_content = text + raw_content;
    }
    public void appendText(String text){
        this.raw_content = raw_content + text;
    }

    public void removeTextAt(int index){
        StringBuilder sb = new StringBuilder(this.raw_content);
        sb.deleteCharAt(index);
        this.raw_content =  sb.toString();
        //this.raw_content = this.raw_content.substring(0,index-1) + this.raw_content.substring(index);
    }
}