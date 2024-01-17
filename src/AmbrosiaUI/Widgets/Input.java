package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.EventStatus;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Placements.ScrollController;
import AmbrosiaUI.Widgets.TextEditor.Cursor;
import AmbrosiaUI.Widgets.TextEditor.EditorLine;
import AmbrosiaUI.Widgets.TextEditor.Highlighting.SyntaxHighlighter;
import AmbrosiaUI.Widgets.TextEditor.Selection;
import AmbrosiaUI.Widgets.TextEditor.StoredText;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Input extends Label{
    private final StoredText storedText;
    private final Cursor cursor;

    public Input(String font, int borderWidth, int margin, int padding) {
        super("", font, borderWidth, margin, padding);

        cursor = new Cursor(new Position(0,0));
        storedText = new StoredText(cursor);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);

        FontMetrics fm = g2.getFontMetrics(g2.getFont());

        g2.setColor(theme.getColorByName(foregroundColor));


        Position textPosition = getTextPosition();
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
}
