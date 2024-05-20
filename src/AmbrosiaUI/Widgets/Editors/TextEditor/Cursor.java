package AmbrosiaUI.Widgets.Editors.TextEditor;

import AmbrosiaUI.Utility.Position;

import java.awt.*;
import java.util.ArrayList;

/**
 * A cursor in the text editor that saves its own position, there can possibly be multiple in a single editor, never tried that tho
 */
public class Cursor {
    private Position position;
    private ArrayList<EditorLine> currentLines;

    public Cursor(Position position) {
        this.position = position;
    }

    public String getCurrrentCharsUnderCursor(){
        if(!this.canMove(new Position(0,0))){
            return "  ";
        }

        String line = currentLines.get(position.y).getText();

        char before = 0;
        if(position.x > 0){
            before = line.charAt(position.x-1);
        }
        char after = 0;
        if(position.x < line.length()){
            after = line.charAt(position.x);
        }

        return before + "" +  after;
    }

    public boolean canMove(Position offset){
        if(!this.canMoveOnLine(offset.y)){
            return false;
        }

        String line = currentLines.get(position.y+offset.y).getText();

        return position.x+offset.x >= 0 && position.x+offset.x <= line.length();
    }

    public boolean canMoveOnLine(int offset_y){
        return position.y + offset_y >= 0 && position.y + offset_y < currentLines.size();
    }

    public Position getRealCursorPosition(FontMetrics fm, int lineHeight){
        if(!this.canMove(new Position(0,0))){
            return new Position(0,0);
        }

        String line = currentLines.get(position.y).getText();

        int x = fm.stringWidth(line.substring(0,position.x));
        int y = lineHeight*position.y;

        return new Position(x,y);
    }

    public void up(){
        if(!this.canMove(new Position(0,-1))){
            this.upToLineEnd();

            return;
        }
        position.y -= 1;
    }
    public void upToLineEnd(){
        if(this.canMoveOnLine(-1)){
            position.x = this.currentLines.get(position.y-1).getText().length();
            position.y -= 1;
        }
    }
    public void down(){
        if(!this.canMove(new Position(0,1))){
            if(this.canMoveOnLine(1)){
                position.x = this.currentLines.get(position.y+1).getText().length();
                position.y += 1;
            }

            return;
        }
        position.y += 1;
    }
    public void left(){
        if(!this.canMove(new Position(-1,0))){
            return;
        }
        position.x -= 1;
    }
    public void right(){
        if(!this.canMove(new Position(1,0))){
            return;
        }
        position.x += 1;
    }

    public void toLineStart(){
        position.x = 0;
    }

    public int getX(){
        return this.position.x;
    }
    public int getY(){
        return this.position.y;
    }

    public Position getPosition() {
        return position;
    }

    public void setX(int x){
        this.position.x = x;
    }
    public void setY(int y){
        this.position.y = y;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setCurrentTextLines(ArrayList<EditorLine> current_lines) {
        this.currentLines = current_lines;
    }

    public ArrayList<EditorLine> getCurrentLines() {
        return currentLines;
    }

    @Override
    public String toString() {
        return "Cursor{" +
                "position=" + position +
                ", currentLines=" + currentLines +
                '}';
    }
}
