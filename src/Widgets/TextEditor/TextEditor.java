package Widgets.TextEditor;

import Widgets.TextEditor.Highlighting.HighlightGroup;
import Utility.EventStatus;
import Utility.Position;
import Widgets.TextEditor.Highlighting.SyntaxHighlighter;
import Widgets.Theme;
import Widgets.Widget;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TextEditor extends Widget {
    private final StoredText text;

    private final Position offset = new Position(60,20);
    final private int LINE_OFFSET = 20;

    private Position scroll = new Position(0,0);
    private int contentHeight = 0;

    private Selection currentSelection = null;
    private final ArrayList<Selection> activeSelections = new ArrayList<>();

    private final Cursor cursor;

    private Graphics2D lastg2;

    private final SyntaxHighlighter highlighter;

    private String currentlyOpenFile;

    public TextEditor() {
        super();
        this.cursor = new Cursor(new Position(0, 0));
        this.text = new StoredText(this.cursor);
        //this.setFocusTraversalKeysEnabled(false); // Stop taking away my TAB ://

        /*this.highlighter = new Highlighter("Simple");
        highlighter.addQuery(new HighlightQuery("(\"[^\"]*\")|'[^']*'", "primary", "accent2"));
        highlighter.addQuery(new HighlightQuery("import|as|def|for|if|else|return", "primary", "accent"));
        highlighter.addQuery(new HighlightQuery("(#.*)|(//.*)", "primary", "secondary"));*/

        this.highlighter = new SyntaxHighlighter("default");
        this.highlighter.loadFromDirectory("syntax/default");

        //this.highlighter.loadFromFile("syntax/default/theme.snx");

        cursor.setCurrentTextLines(text.getLines());
    }

    public void insertStringOnCursor(String text){
        this.text.insertTextAt(text,cursor.getX(),cursor.getY());

        for(int i = 0; i < text.length();i++) {
            cursor.right();
        }
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        setupDraw(g2);

        // Calculations
        FontMetrics fm = g2.getFontMetrics(theme.getFontByName("normal"));
        //int single_char_width = fm.stringWidth(" " + cursor.getCurrrentCharsUnderCursor().charAt(0));
        int text_height = fm.getHeight();
        Position pos = cursor.getRealCursorPosition(fm);
        offset.x = fm.stringWidth(" ."+text.getLines().size());

        // Draw background
        g2.setColor(theme.getColorByName("primary"));
        g2.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());

        g2.setColor(theme.getColorByName("secondary"));
        g2.fillRect(this.getX(),this.getY(), offset.x+5, this.getHeight());

        g2.setColor(theme.getColorByName("accent"));
        g2.fillRect(this.getX()+ offset.x+3,this.getY(),2, this.getHeight());


        //
        // Shift the viewport
        //
        //System.out.println((OFFSET.y + pos.y + this.getHeight()/2)+ " " + this.getHeight() + "translate y: " + ((double) this.getHeight() /2 - OFFSET.y + pos.y));

        AffineTransform at = new AffineTransform();
        at.translate(-scroll.x , -scroll.y);

        g2.setTransform(at);

        //
        // Draw selections
        //

        g2.setColor(theme.getColorByName("accent2"));
        Cursor from, to;
        for(Selection selection: activeSelections){
            selection = selection.getReorganized();
            if(!selection.valid()){
                continue;
            }

            ArrayList<String> selected_lines = selection.getSelectedContent();
            from = selection.getFrom();
            to = selection.getTo();

            // First line
            int offset_x = fm.stringWidth(text.getLines().get(from.getY()).getText().substring(0,from.getX()));
            g2.fillRect(
                    this.getX() + offset.x + LINE_OFFSET + offset_x,
                    this.getY() + offset.y + from.getY()*text_height,
                    fm.stringWidth(selected_lines.get(0)),
                    text_height
            );
            // If only a segment is selected
            if(selected_lines.size() == 1){
                continue;
            }

            g2.fillRect(
                    this.getX() + offset.x + LINE_OFFSET + offset_x,
                    this.getY() + offset.y + from.getY()*text_height,
                    this.getWidth()-offset_x,
                    text_height
            );
            // Last line
            g2.fillRect(
                    this.getX() + offset.x + LINE_OFFSET,
                    this.getY() + offset.y + to.getY()*text_height,
                    fm.stringWidth(selected_lines.get(selected_lines.size()-1)),
                    text_height
            );
            // Only two lines
            if(selected_lines.size() == 2){
                continue;
            }

            // All lines in between
            for(int i = 1;i < selected_lines.size()-1;i++){
                g2.fillRect(
                        this.getX() + offset.x + LINE_OFFSET,
                        this.getY() + offset.y + (from.getY()+i)*text_height,
                        this.getWidth(),
                        text_height
                );
            }
        }

        //
        // Draw all text
        //
        int line_number = 0;
        String line_text;
        int x,y;
        for (EditorLine line : text.getLines()) {
            line_text = line_number + ".";
            x = this.getX() + offset.x;
            y = this.getY() + offset.y+line_number*text_height;

            // Draw line number
            g2.setColor(theme.getColorByName("text1"));
            g2.drawString(line_text, x-fm.stringWidth(line_text), y+text_height);

            // Draw line content
            line.draw(g2, x+LINE_OFFSET,y);
            line_number += 1;
        }

        //
        // Draw all highlights
        //
        String[] content;
        for(HighlightGroup group: highlighter.getCurrentHighlighter().generateHighlights(this.getFullContent())){
            x = this.getX() + offset.x + getRealX(group.getX(),group.getY(),fm) + LINE_OFFSET;
            y = this.getY() + offset.y + group.getY()*text_height;

            content = group.getContent().split("\n");

            if(content.length > 1){
                for(int i = 0;i < content.length;i++){
                    x = this.getX() + offset.x + getRealX(i == 0 ? group.getX() : 0,group.getY(),fm) + LINE_OFFSET;
                    y = this.getY() + offset.y + (group.getY()+i)*text_height;

                    g2.setColor(theme.getColorByName("primary"));
                    g2.drawString(content[i], x, y+text_height);
                    g2.setColor(theme.getColorByName(group.getForegroundColor()));
                    g2.drawString(content[i], x, y+text_height);
                }
                continue;
            }

            g2.setColor(theme.getColorByName("primary"));
            g2.drawString(group.getContent(), x, y+text_height);
            g2.setColor(theme.getColorByName(group.getForegroundColor()));
            g2.drawString(group.getContent(), x, y+text_height);
        }

        contentHeight = text.getLines().size()*text_height;

        //
        // Draw cursor
        //

        /*if(pos.y-scroll.y > this.getHeight()-100){
            setScrollY(scroll.y + height);
        }
        if(pos.y-scroll.y < height){
            setScrollY(scroll.y - height);
        }*/

        g2.setColor(theme.getColorByName("text1"));
        g2.fillRect(
                this.getX() + offset.x + pos.x - 1 + LINE_OFFSET,
                this.getY() + offset.y + pos.y,
                2,
                text_height
        );

        AffineTransform at2 = new AffineTransform();
        at2.translate(0 , 0);

        g2.setTransform(at2);
        lastg2 = g2;


        //
        //  Draw all hints
        //
        //g2.drawString(cursor.getCurrrentCharsUnderCursor(), this.getWidth() - 50, this.getHeight() - 50);

        String cursorPosition = cursor.getX() + ":" + cursor.getY() + " " + highlighter.getCurrentHighlighter().getName() + " " + currentlyOpenFile;
        int cursorPositionWidth = fm.stringWidth(cursorPosition);

        int cursorPositionDisplayMargin = 5;

        g2.setColor(theme.getColorByName("secondary"));
        g2.fillRect(
                this.getX() + this.getWidth() - cursorPositionWidth - cursorPositionDisplayMargin*2,
                this.getY() + this.getHeight() - cursorPositionDisplayMargin - text_height,
                cursorPositionWidth+cursorPositionDisplayMargin*2,
                text_height+cursorPositionDisplayMargin
        );
        g2.setColor(theme.getColorByName("text1"));
        g2.drawString(cursorPosition,
                this.getX() + this.getWidth() - cursorPositionWidth - 5,
                this.getY() + this.getHeight() - 5
        ); // Widgets.TextEditor.Cursor position in bottom right corner

        super.drawSelf(g2);
    }

    public String getFullContent(){
        return text.getFullContent();
    }

    public int getRealX(int x, int y, FontMetrics fm){
        if(y < 0 || y > text.getLines().size()-1){
            return 0;
        }

        return fm.stringWidth(text.getLines().get(y).getText().substring(0,x));
    }

    public void startSelection(){
        if(currentSelection != null){
            return;
        }
        clearSelections();

        Cursor second = new Cursor(new Position(cursor.getX(), cursor.getY()));
        second.setCurrentTextLines(text.getLines());

        currentSelection = new Selection(cursor, second);
        activeSelections.add(currentSelection);
    }
    public void endSelection(){
        if(currentSelection == null){
            return;
        }
        Cursor second = new Cursor(new Position(cursor.getX(), cursor.getY()));
        second.setCurrentTextLines(text.getLines());

        currentSelection.setFrom(second);
        currentSelection = null;
    }

    public void clearSelections(){
        if(currentSelection != null){
            this.endSelection();
        }
        activeSelections.clear();
    }

    public Position realToCursorPosition(Position pos, Graphics2D g2){
        FontMetrics fm = g2.getFontMetrics(theme.getFontByName("normal"));

        Position cursor_pos = cursor.getRealCursorPosition(fm);

        int display_offset = scroll.y;

        int y = pos.y - offset.y - this.getY() + display_offset;
        y = y / fm.getHeight();
        int x = pos.x - offset.x - LINE_OFFSET - this.getX();

        if(y < 0 || y >= text.getLines().size()){
            return cursor_pos;
        }

        /*
            Locate on x axis
         */
        String line = text.getLines().get(y).getText();
        String current_line = "";

        int i = 0;
        while(fm.stringWidth(current_line) < x-2 && !current_line.equals(line)){
            current_line = line.substring(0,i);
            i += 1;
        }

        x = Math.max(0,i-1);
        return new Position(x,y);
    }

    public void clear(){
        this.text.getLines().clear();
        scroll = new Position(0,0);
        activeSelections.clear();
    }
    public void openFile(String filename){
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);

            highlighter.toDetectedHighlighterFromFilename(filename);
            clear();

            StringBuilder all_data = new StringBuilder();

            int lines = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                all_data.append(data).append("\n");

                lines += 1;

                if (lines > 5000){
                    System.out.println("Max amount of lines reached. For safety reasons cutting reading of text");
                }
            }

            text.setText(all_data.toString());

            myReader.close();
            currentlyOpenFile = filename;

        } catch (FileNotFoundException ignored) {

        }
    }

    public void saveToCurrentlyOpenFile(){
        try {
            FileWriter myWriter = new FileWriter(currentlyOpenFile);
            for(EditorLine line: text.getLines()) {
                myWriter.write(line.getText() + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException ignored) {

        }
    }

    @Override
    public void onClicked(EventStatus eventStatus) {
        /*Position pos = realToCursorPosition(eventStatus.getMousePosition(),lastg2);
        Position last_pos = new Position(cursor.getX(), cursor.getY());

        cursor.setPosition(pos);

        if(!cursor.canMove(new Position(0,0))){
            cursor.setPosition(last_pos);
        }
        else{
            if(this.currentSelection == null) {
                if(!activeSelections.isEmpty()) {
                    System.out.println(activeSelections.get(0).getSelectedContent());
                }
                clearSelections();
            }
        }*/
    }

    public void setScrollY(int y){
        this.scroll.y = Math.max(0, Math.min(this.contentHeight,y));
    }
    public Cursor getCursor() {
        return cursor;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public EditorLine getLine(int y){
        return this.text.getLines().get(y);
    }

    public EditorLine getLineUnderCursor(){
        return this.getLine(this.cursor.getY());
    }

    public Position getScroll() {
        return scroll;
    }

    public ArrayList<EditorLine> getLines() {
        return text.getLines();
    }

    public String getCurrentlyOpenFile() {
        return currentlyOpenFile;
    }

    @Override
    public void update(EventStatus eventStatus) {
        super.update(eventStatus);
        if(eventStatus.isMouseDown()){
            Position pos = realToCursorPosition(eventStatus.getMousePosition(),lastg2);
            Position last_pos = new Position(cursor.getX(), cursor.getY());

            cursor.setPosition(pos);

            if(!cursor.canMove(new Position(0,0))){
                cursor.setPosition(last_pos);
            }
        }
    }

    public StoredText getText() {
        return text;
    }

    public ArrayList<Selection> getActiveSelections() {
        return activeSelections;
    }
}