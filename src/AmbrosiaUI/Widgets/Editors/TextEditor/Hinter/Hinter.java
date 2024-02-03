package AmbrosiaUI.Widgets.Editors.TextEditor.Hinter;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Editors.EditorLike;
import AmbrosiaUI.Widgets.Editors.TextEditor.Cursor;
import AmbrosiaUI.Widgets.Editors.TextEditor.EditorLine;
import AmbrosiaUI.Widgets.Editors.TextEditor.Highlighting.Highlighter;
import AmbrosiaUI.Widgets.Editors.TextEditor.TextEditor;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hinter {
    private ArrayList<String> currentHints = new ArrayList<>();
    private TextEditor editor;
    private Cursor cursor;

    private ArrayList<KeywordDictionary> dictionaries = new ArrayList<>();

    public Hinter(TextEditor editor, Cursor cursor) {
        this.editor = editor;
        this.cursor = cursor;
    }

    public void reloadHints(){
        currentHints.clear();

        String currentWord = getCurrentWord();
        Pattern pattern = Pattern.compile("(\\w+)\\b", Pattern.MULTILINE); // Finding all words
        Matcher matcher = pattern.matcher(editor.getFullContent());

        if(currentWord == null){
            return;
        }
        while(matcher.find()){
            if(matcher.group().contains(currentWord)
                    && !currentHints.contains(matcher.group())
                    && !matcher.group().equals(currentWord)
            ){
                currentHints.add(matcher.group());
            }
        }

        if(!editor.hasFile()){
            return;
        }

        for(KeywordDictionary dictionary: dictionaries){
            for(String st: dictionary.getApplies_to()){
                if (editor.getCurrentFile().endsWith(st)){
                    applyDictionary(dictionary, currentWord);
                }
            }
        }
    }

    public void applyDictionary(KeywordDictionary dictionary, String currentWord){
        for(String word: dictionary.getWords()){
            if(word.contains(currentWord)){
                currentHints.add(word);
            }
        }
    }

    public String getCurrentWord(){
        EditorLine currentLine = editor.getLine(cursor.getY());

        Pattern patternW = Pattern.compile("(\\w+)$", Pattern.MULTILINE); // Find current word
        Matcher matcherW = patternW.matcher(currentLine.getText());

        if(matcherW.find()){
            return matcherW.group(1);
        }
        return null;
    }
    public void draw(Graphics2D g2, int x, int y){
        Theme theme = editor.getTheme();

        Shape lastClip = g2.getClip();

        int width = 200;
        int lineheight = (editor.getLineHeight()+5);
        int height = lineheight *Math.min(currentHints.size(),10);

        g2.setClip(x,y,width,height);

        if (currentHints.isEmpty()){
            g2.setClip(lastClip);
            return;
        }
        g2.setColor(theme.getColorByName("secondary"));
        AdvancedGraphics.borderedRect(g2,
                x,
                y,
                width,
                height,
                1,
                theme.getColorByName("secondary"),
                theme.getColorByName("text1"),
                AdvancedGraphics.BORDER_FULL
        );

        int yOffset = 0;
        g2.setColor(theme.getColorByName("text1"));
        for(String hint: currentHints){
            AdvancedGraphics.drawText(g2, new Rectangle(
                    x,
                    y+yOffset,
                    width,
                    lineheight
            ), hint, AdvancedGraphics.Side.CENTER);
            yOffset += lineheight;
        }

        g2.setClip(lastClip);
    }

    public void loadFromDirectory(String directory){
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null){
            Logger.printWarning("No dicitionaries loaded, folder is empty.");
            return;
        }

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && listOfFile.getName().endsWith(".dict")) {
                //System.out.println("File " + listOfFile.getName());
                KeywordDictionary nw = new KeywordDictionary();
                nw.loadFromFile(listOfFile.getAbsolutePath());

                dictionaries.add(nw);
            }
        }
    }

    public ArrayList<String> getCurrentHints() {
        return currentHints;
    }

    public void setCurrentHints(ArrayList<String> currentHints) {
        this.currentHints = currentHints;
    }
}
