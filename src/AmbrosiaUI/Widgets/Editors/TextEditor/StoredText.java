package AmbrosiaUI.Widgets.Editors.TextEditor;

import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Utility.Position;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class that manages all readable text, has useful functions for editing the contents, loading from files and others.
 * Also stores its own state for reverting changes.
 */
public class StoredText {
    private final ArrayList<EditorLine> lines;

    private String currentFile = null;

    private boolean storingBlocked = false;

    private static class State{
        private final ArrayList<EditorLine> lines;
        private final Position cursorPosition;

        private final String filename;

        public State(ArrayList<EditorLine> lines, Position cursorPosition, String filename) {
            this.lines = lines;
            this.cursorPosition = cursorPosition;
            this.filename = filename;
        }
    }
    private final ArrayList<State> pastText;

    private final Cursor actingCursor;

    public StoredText(Cursor actingCursor) {
        lines = new ArrayList<>();
        lines.add(new EditorLine(""));
        pastText = new ArrayList<>();
        this.actingCursor = actingCursor;

        this.actingCursor.setCurrentTextLines(lines);
    }

    public ArrayList<EditorLine> getLines(){
        return lines;
    }

    public void storeState(){
        if (storingBlocked){
            return;
        }

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

        if(currentFile == null){
            pastText.add(new State(newlines, new Position(actingCursor.getX(), actingCursor.getY()), null));
        }
        else{
            pastText.add(new State(newlines, new Position(actingCursor.getX(), actingCursor.getY()), new String(currentFile)));
        }
    }

    public void setText(String raw_text){
        if(raw_text.isBlank()){
            raw_text = " \n\n";
        }
        raw_text = raw_text.replaceAll("\t", "    ");

        this.storeState();
        String[] data = raw_text.split("\n\r|\n|\r");
        this.lines.clear();

        for (String line: data){
            this.lines.add(new EditorLine(line));
        }

        actingCursor.setX(0);
        actingCursor.setY(0);
    }

    public boolean fromFile(String filename){
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);

            StringBuilder all_data = new StringBuilder();

            int lines = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                all_data.append(data).append("\n");

                lines += 1;

                if (lines > 50000){
                    Logger.printWarning("Max amount of lines reached. For safety reasons cutting reading of text");
                    break;
                }
            }

            /*if(all_data.toString().equals("")){
                return false;
            }*/

            this.storeState();
            this.setText(all_data.toString());

            myReader.close();
            this.currentFile = filename;
            onFileChanged();

            return true;
        } catch (FileNotFoundException ignored) {
            return false;
        }
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

        //System.out.println(currentFile + " " + st.filename + " " + this.currentFile.equals(st.filename));

        boolean file_changed;
        if(this.currentFile == null){
            file_changed = st.filename != null;
        }
        else{
            file_changed = !this.currentFile.equals(st.filename);
        }


        this.currentFile = st.filename;

        this.pastText.remove(pastText.size()-1);

        if(file_changed){
            onFileChanged();
        }
    }

    public boolean hasFile(){
        //System.out.println(currentFile);
        return currentFile != null && !currentFile.equals("null");
    }

    public void onFileChanged(){

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

    public void removeSelection(Selection selection){
        selection = selection.getReorganized();
        if(!selection.valid()){
            return;
        }

        Cursor from, to;
        ArrayList<String> selected_lines = selection.getSelectedContent();
        //System.out.println(selected_lines);
        from = selection.getFrom();
        to = selection.getTo();

        actingCursor.setX(from.getX());
        actingCursor.setY(from.getY());

        EditorLine first_line = this.lines.get(from.getY());
        // If only a segment is selected
        if(selected_lines.size() == 1){
            first_line.removeAllBetween(from.getX(),to.getX());
            return;
        }
        else{
            first_line.removeAllBetween(from.getX(),first_line.size());
        }

        EditorLine second_line = this.lines.get(to.getY());
        second_line.removeAllBetween(0,to.getX());
        first_line.addTextAt(second_line.getText(),first_line.size());
        lines.remove(second_line);

        // Only two lines
        if(selected_lines.size() == 2){
            return;
        }

        // All lines in between
        for(int i = 1;i < selected_lines.size()-1;i++){
            this.lines.remove(from.getY()+1);
        }
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

        actingCursor.setY(index+1);
        actingCursor.upToLineEnd();
    }

    public String getFullContent(){
        StringBuilder content = new StringBuilder();

        for(EditorLine line: this.lines){
            content.append(line.getText()).append("\n");
        }

        return content.toString();
    }

    public String getContentBetween(int startY, int endY){
        StringBuilder content = new StringBuilder();

        for(int i = startY; i < Math.min(endY,this.lines.size());i++){
            content.append(lines.get(i).getText()).append("\n");
        }

        return content.toString();
    }

    public void clear(){
        storeState();
        lines.clear();
        lines.add(new EditorLine(""));
        setCurrentFile(null);
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public void blockStoring(){
        storingBlocked = true;
    }

    public void unblockStoring(){
        storingBlocked = false;
    }

    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
        this.onFileChanged();
    }
}
