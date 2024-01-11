package AmbrosiaUI.Widgets.TextEditor.Highlighting;

import java.io.File;
import java.util.ArrayList;

public class SyntaxHighlighter {
    private String name;
    private Highlighter currentHighlighter;
    private ArrayList<Highlighter> highlighters = new ArrayList<>();

    public SyntaxHighlighter(String name) {
        this.name = name;

        this.highlighters.add(new Highlighter("PlainText"));
    }

    public void loadFromDirectory(String directory){
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && listOfFile.getName().endsWith(".snx")) {
                //System.out.println("File " + listOfFile.getName());
                Highlighter nw = new Highlighter("");
                nw.loadFromFile(listOfFile.getAbsolutePath());

                highlighters.add(nw);
            }
        }
    }

    public void toDetectedHighlighterFromFilename(String filename){
        ArrayList<Highlighter> available = this.detectHighlighterFromFilename(filename);

        if(available.isEmpty()){
            this.currentHighlighter = highlighters.get(0);
            return;
        }

        this.currentHighlighter = available.get(0);
    }

    public ArrayList<Highlighter> detectHighlighterFromFilename(String filename){
        ArrayList<Highlighter> available = new ArrayList<>();

        for(Highlighter highlighter: highlighters){
            for(String applies: highlighter.getApplies_to()){
                if(filename.endsWith(applies)){
                    available.add(highlighter);
                    break;
                }
            }
        }

        return available;
    }

    public Highlighter getCurrentHighlighter() {
        if(currentHighlighter == null){
            return highlighters.get(0);
        }
        return currentHighlighter;
    }
}
