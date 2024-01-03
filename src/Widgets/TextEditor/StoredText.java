package Widgets.TextEditor;

import Utility.Position;

import java.util.ArrayList;

public class StoredText {
    private ArrayList<EditorLine> lines;

    private class State{
        private final ArrayList<EditorLine> lines;
        private final Position cursorPosition;

        public State(ArrayList<EditorLine> lines, Position cursorPosition) {
            this.lines = lines;
            this.cursorPosition = cursorPosition;
        }
    }
    private final ArrayList<State> pastText;

    private final Cursor actingCursor;

    public StoredText(Cursor actingCursor) {
        lines = new ArrayList<>();
        lines.add(new EditorLine(""));
        pastText = new ArrayList<>();
        this.actingCursor = actingCursor;
    }

    public ArrayList<EditorLine> getLines(){
        return lines;
    }

    public void storeState(){
        ArrayList<EditorLine> newlines = new ArrayList<>();

        for(EditorLine line: lines){
            newlines.add(new EditorLine(line.getText()));
        }

        if(newlines.size() == 0){
            newlines.add(new EditorLine(""));
        }

        if(pastText.size() > 200){
            pastText.remove(0);
        }

        pastText.add(new State(newlines, new Position(actingCursor.getX(),actingCursor.getY())));
    }

    public void setText(String raw_text){
        this.storeState();
        String[] data = raw_text.split("\n\r|\n|\r");
        this.lines.clear();

        for (String line: data){
            this.lines.add(new EditorLine(line));
        }

        actingCursor.setX(0);
        actingCursor.setY(0);
    }

    public void revert(){
        if(this.pastText.size() < 1){
            return;
        }
        State st = pastText.get(pastText.size()-1);

        this.lines.clear();
        for(EditorLine line: st.lines){
            this.lines.add(new EditorLine(line.getText()));
        }

        actingCursor.setX(st.cursorPosition.x);
        actingCursor.setY(st.cursorPosition.y);

        this.pastText.remove(pastText.size()-1);
    }

    public void removeCharAt(int x, int y){
        this.storeState();
        EditorLine line = getLineAt(y);

        if(line == null) return;

        line.removeTextAt(x);
    }
    public void insertTextAt(String text, int x, int y){
        this.storeState();
        EditorLine line = getLineAt(y);

        if(line == null) return;

        line.addTextAt(text,x);
    }
    public void appendTextAt(String text, int y){
        this.storeState();
        EditorLine line = getLineAt(y);

        if(line == null) return;

        line.appendText(text);
    }

    public void removeLine(EditorLine line){
        this.storeState();
        this.lines.remove(line);
    }


    public EditorLine getLineAt(int y){
        if(y < 0 || y >= lines.size()){
            return null;
        }
        return lines.get(y);
    }

    public void newLine(String text){
        this.storeState();
        this.insertNewLine(text,this.lines.size());
    }
    public void insertNewLine(String text, int index){
        this.storeState();
        EditorLine line = new EditorLine(text);
        lines.add(index ,line);
    }

    public String getFullContent(){
        StringBuilder content = new StringBuilder();

        for(EditorLine line: this.lines){
            content.append(line.getText()).append("\n");
        }

        return content.toString();
    }
}
