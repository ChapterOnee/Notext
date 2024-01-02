package Widgets.TextEditor;

import Widgets.TextEditor.Highlighting.Highlighter;
import Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

public class EditorLine{
    private ArrayList<EditorLineGroup> groups;
    private String raw_content;
    private Theme theme;
    private Highlighter highlighter;

    public EditorLine(String contents, Theme theme, Highlighter highlighter) {
        super();
        this.groups = new ArrayList<>();
        this.raw_content = contents;
        this.highlighter = highlighter;
        this.theme = theme;
        this.reloadGroups();
    }

    public void draw(Graphics2D g2, int x, int y){
        int total_width = 0;

        FontMetrics fm = g2.getFontMetrics(theme.getFontByName("normal"));
        for(EditorLineGroup group: groups) {
            int width = fm.stringWidth(group.getText());

            g2.setColor(theme.getColorByName(group.getForegroundColor()));
            g2.drawString(group.getText(),x+total_width,y+fm.getHeight());

            total_width += width;
        }
    }

    private void reloadGroups(){
        this.groups.clear();
        this.groups.add(new EditorLineGroup(this.raw_content));
        //this.groups = highlighter.generateGroupsFromText(this.raw_content);
    }

    public String getText() {
        return raw_content;
    }

    public void setText(String raw_content) {
        this.raw_content = raw_content;
        this.reloadGroups();
    }

    public void addTextAt(String text, int index){
        this.raw_content = this.raw_content.substring(0,index) + text + this.raw_content.substring(index);
        this.reloadGroups();
    }

    public void pushText(String text){
        this.raw_content = text + raw_content;
        this.reloadGroups();
    }
    public void appendText(String text){
        this.raw_content = raw_content + text;
        this.reloadGroups();
    }

    public void removeTextAt(int index){
        StringBuilder sb = new StringBuilder(this.raw_content);
        sb.deleteCharAt(index);
        this.raw_content =  sb.toString();
        //this.raw_content = this.raw_content.substring(0,index-1) + this.raw_content.substring(index);
        this.reloadGroups();
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void setTextHighlighter(Highlighter highlighter) {
        this.highlighter = highlighter;
    }
}
