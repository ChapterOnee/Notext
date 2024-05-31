package AmbrosiaUI.Widgets.Editors.TextEditor.Highlighting;

import AmbrosiaUI.Utility.Logger;
import App.Config;
import App.Extension;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * A grouping of Highlighters that the text editor can pick from
 */
public class SyntaxHighlighter {
    private String name;
    private Highlighter currentHighlighter;
    private ArrayList<Highlighter> highlighters = new ArrayList<>();

    public SyntaxHighlighter(String name) {
        this.name = name;

        this.highlighters.add(new Highlighter("PlainText"));
    }

    /**
     * Loads all syntax files from a set directory
     * @param dir The directory path
     */
    public void loadFromConfig(){
        /*File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null){
            Logger.printWarning("No syntax highlighters loaded, folder is empty.");
            return;
        }

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && listOfFile.getName().endsWith(".snx")) {
                //System.out.println("File " + listOfFile.getName());
                Highlighter nw = new Highlighter("");
                nw.loadFromFile(listOfFile.getAbsolutePath());

                highlighters.add(nw);
            }
        }*/

        for(String path : Config.syntaxes) {
            Highlighter nw = new Highlighter("");
            nw.loadFromFile(path);

            highlighters.add(nw);
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
