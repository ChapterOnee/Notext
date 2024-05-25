package AmbrosiaUI.Widgets.Editors.TextEditor;

import java.awt.*;

/**
 * A single line in the editor, has functions for easy editing
 */
public class EditorLine{
    private String raw_content;

    public EditorLine(String contents) {
        super();
        this.raw_content = contents;
    }

    public void draw(Graphics2D g2, int x, int y, int lineHeight, int xstart, int xend){
        g2.drawString(this.getText().substring(xstart,xend),x,y+lineHeight);
    }


    public String getText() {
        return raw_content;
    }

    public void setText(String raw_content) {
        this.raw_content = raw_content;
    }

    /**
     * Inserts text on an index
     * @param text
     * @param index
     */
    public void addTextAt(String text, int index){
        this.raw_content = this.raw_content.substring(0,index) + text + this.raw_content.substring(index);
    }

    public void pushText(String text){
        this.raw_content = text + raw_content;
    }

    /**
     * Adds text to the end of the line
     * @param text
     */
    public void appendText(String text){
        this.raw_content = raw_content + text;
    }

    /**
     * Removes character at given index
     * @param index
     */
    public void removeTextAt(int index){
        StringBuilder sb = new StringBuilder(this.raw_content);
        sb.deleteCharAt(index);
        this.raw_content =  sb.toString();
        //this.raw_content = this.raw_content.substring(0,index-1) + this.raw_content.substring(index);
    }

    /**
     * Removes text between two indexes
     * @param start
     * @param end
     */
    public void removeAllBetween(int start, int end){
        StringBuilder buf = new StringBuilder(this.raw_content);

        buf.replace(start, end, "");

        this.raw_content = buf.toString();
    }

    /**
     * Replace all instances of 'from' by 'to'
     * @param from
     * @param to
     */
    public void replace(String from, String to){
        this.raw_content = this.raw_content.replace(from,to);
    }

    public int size(){
        return this.raw_content.length();
    }

    @Override
    public String toString() {
        return raw_content;
    }
}
