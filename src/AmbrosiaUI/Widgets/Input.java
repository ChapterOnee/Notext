package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.EventStatus;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Editors.TextEditor.Cursor;
import AmbrosiaUI.Widgets.Editors.TextEditor.Selection;
import AmbrosiaUI.Widgets.Editors.TextEditor.StoredText;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * A widget for single line text input
 */
public class Input extends Label{
    private final StoredText storedText;
    private final Cursor cursor;

    private Selection currentSelection;

    public Input(String font, int borderWidth, int margin, int padding) {
        super("", font, borderWidth, margin, padding, true);

        cursor = new Cursor(new Position(0,0));
        storedText = new StoredText(cursor);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);

        FontMetrics fm = g2.getFontMetrics(g2.getFont());
        Position textPosition = getTextPosition();

        if(currentSelection != null) {
            g2.setColor(theme.getColorByName("selection"));


            Cursor from, to;

            Selection selection = currentSelection.getReorganized();

            from = selection.getFrom();
            to = selection.getTo();

            // First line
            int offset_x = fm.stringWidth(storedText.getLines().get(from.getY()).getText().substring(0, from.getX()));
            int width = fm.stringWidth(storedText.getLines().get(from.getY()).getText().substring(from.getX(), to.getX()));
            g2.fillRect(
                    textPosition.x + offset_x,
                    textPosition.y -fm.getAscent(),
                    width,
                    AdvancedGraphics.getTextHeight(fm)
            );
        }

        drawText(g2);

        g2.setColor(theme.getColorByName(foregroundColor));
        Position pos = cursor.getRealCursorPosition(fm, AdvancedGraphics.getTextHeight(fm));
        
        g2.fillRect(
                textPosition.x+pos.x,
                textPosition.y-fm.getAscent(),
                2,
                AdvancedGraphics.getTextHeight(fm)
        );
    }

    @Override
    public String getText() {
        return storedText.getLines().get(0).getText();
    }

    @Override
    public void onKeyPressed(KeyEvent keyEvent) {
        //System.out.println(keyEvent.getExtendedKeyCode());

        switch (keyEvent.getKeyCode()){
            case 37 -> this.cursor.left();
            case 39 -> this.cursor.right();
                    /*
                        Backspace, handle removing characters.
                    */
            case 8 -> {
                if(currentSelection != null){
                    this.storedText.removeSelection(this.currentSelection);
                    this.clearSelections();
                    break;
                }

                if (this.cursor.canMove(new Position(-1, 0))) {
                    this.storedText.removeCharAt(this.cursor.getX() - 1, this.cursor.getY());
                    this.cursor.left();
                }
            }
                    /*
                        Delete
                     */
            case 127 -> {
                if(this.cursor.getCurrrentCharsUnderCursor().charAt(1) != 0){
                    this.storedText.removeCharAt(this.cursor.getX(),this.cursor.getY());
                }

                //theme.setAccentColor(theme.getColorByName("accent").darker());
            }
                    /*
                        Tab
                    */

            case 9 -> this.insertStringOnCursor("    ");
            default -> {
                char c = keyEvent.getKeyChar();
                Character.UnicodeBlock block = Character.UnicodeBlock.of( c );

                if((!Character.isISOControl(c)) &&
                        c != KeyEvent.CHAR_UNDEFINED &&
                        block != null &&
                        block != Character.UnicodeBlock.SPECIALS
                ) {
                    if(currentSelection != null){
                        this.storedText.removeSelection(this.currentSelection);
                        this.clearSelections();
                        break;
                    }

                    this.insertStringOnCursor(keyEvent.getKeyChar() + "");
                }
            }

        }
    }

    public Position realToCursorPosition(Position pos){
        FontMetrics fm = fontsizecanvas.getFontMetrics(theme.getFontByName("normal"));

        int y = 0;
        int x = pos.x - getTextPosition().x;
        /*
            Locate on x axis
         */
        String line = storedText.getLines().get(y).getText();
        String current_line = "";

        int i = 0;
        while(fm.stringWidth(current_line) < x-2 && !current_line.equals(line)){
            current_line = line.substring(0,i);
            i += 1;
        }

        x = Math.max(0,i-1);
        return new Position(x,y);
    }

    public void insertStringOnCursor(String text){
        this.storedText.insertTextAt(text,cursor.getX(),cursor.getY());

        for(int i = 0; i < text.length();i++) {
            cursor.right();
        }
    }

    @Override
    public void update(EventStatus eventStatus) {
        super.update(eventStatus);

        if(eventStatus.isMouseDown() && mouseOver){
            Position pos = realToCursorPosition(eventStatus.getMousePosition());
            Position last_pos = new Position(cursor.getX(), cursor.getY());

            cursor.setPosition(pos);

            if(!cursor.canMove(new Position(0,0))){
                cursor.setPosition(last_pos);
            }
        }
    }

    public String getContent(){
        return storedText.getLines().get(0).getText();
    }

    public void setContent(String content) {
        storedText.setText(content);
    }

    @Override
    public String getSelectedContent() {
        if(currentSelection != null){
            return currentSelection.getSelectedContentFormated();
        }

        return super.getSelectedContent();
    }

    public void startSelection(){
        if(currentSelection != null){
            return;
        }

        Cursor second = new Cursor(new Position(cursor.getX(), cursor.getY()));
        second.setCurrentTextLines(storedText.getLines());

        currentSelection = new Selection(cursor, second);
    }
    public void endSelection(){
        if(currentSelection == null){
            return;
        }
        Cursor second = new Cursor(new Position(cursor.getX(), cursor.getY()));
        second.setCurrentTextLines(storedText.getLines());

        currentSelection.setFrom(second);
    }

    public void clearSelections(){
        currentSelection = null;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        this.clearSelections();
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        this.endSelection();
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        this.startSelection();
    }
}
