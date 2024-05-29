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
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A really simple auto suggesting system
 */
public class Hinter {
    private ArrayList<String> currentHints = new ArrayList<>();
    private TextEditor editor;
    private Cursor cursor;

    private final int MAX_SUGGESTION_SHOWN = 10;
    private int selectedHint = -1;
    private ArrayList<KeywordDictionary> dictionaries = new ArrayList<>();

    public Hinter(TextEditor editor, Cursor cursor) {
        this.editor = editor;
        this.cursor = cursor;
    }

    /**
     * Regenerates hints based on file contents
     */
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

        selectedHint = 0;

        sortMostRelevantHint();
    }

    /**
     * Tries to find the most similar word and puts it to the top
     */
    public void sortMostRelevantHint(){
        if(currentHints.isEmpty()){
            return;
        }

        int mostRelevantIndex = 0;
        String mostRelevant = currentHints.get(0);
        String currentWord = getCurrentWord();

        for(String hint: currentHints){
            if(hint.indexOf(currentWord) < mostRelevant.indexOf(currentWord)){
                mostRelevantIndex = currentHints.indexOf(hint);
                mostRelevant = hint;
            }
        }
        currentHints.remove(mostRelevantIndex);
        currentHints.add(0,mostRelevant);
    }

    /**
     * Searches through a dictionary of words and if similar adds it as a hint
     * @param dictionary
     * @param currentWord
     */
    public void applyDictionary(KeywordDictionary dictionary, String currentWord){
        for(String word: dictionary.getWords()){
            if(word.contains(currentWord) && !currentHints.contains(word)){
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
        int lineheight = (editor.getLineHeight()+10);
        int height = lineheight *Math.min(currentHints.size(),MAX_SUGGESTION_SHOWN);

        int realX = x;
        int realY = y;

        if(realY + height > editor.getContentHeight()){
            realY -= height + 5;
        }
        else {
            realY += lineheight + 5;
        }


        if (!hasCurrentHint() || getCurrentWord() == null){
            g2.setClip(lastClip);
            return;
        }
        g2.setColor(theme.getColorByName("secondary"));
        AdvancedGraphics.drawText(g2,
                new Rectangle(x,y,100,g2.getFontMetrics().getAscent()),
                getCurrentHint().substring(Math.min(getCurrentHint().length(),getCurrentWord().length())),
                AdvancedGraphics.Side.LEFT);

        g2.setClip(realX,realY,width,height);
        g2.setColor(theme.getColorByName("secondary"));
        AdvancedGraphics.borderedRect(g2,
                realX,
                realY,
                width,
                height,
                1,
                theme.getColorByName("secondary"),
                theme.getColorByName("primary"),
                AdvancedGraphics.BORDER_FULL
        );

        int yOffset = 0;
        g2.setColor(theme.getColorByName("text1"));

        int i = 0;
        for(String hint: currentHints){
            if(i == selectedHint){
                g2.setColor(theme.getColorByName("accent"));
                g2.fillRect(realX, realY+yOffset, 3, lineheight);
                g2.setColor(theme.getColorByName("text1"));
            }
            AdvancedGraphics.drawText(g2, new Rectangle(
                    realX+10,
                    realY+yOffset,
                    width,
                    lineheight
            ), hint, AdvancedGraphics.Side.LEFT);
            yOffset += lineheight;

            if(i > MAX_SUGGESTION_SHOWN) break;

            i++;
        }

        g2.setClip(lastClip);
    }

    public boolean hasCurrentHint(){
        return !currentHints.isEmpty() && selectedHint != -1;
    }

    public String getCurrentHint(){
        return currentHints.get(selectedHint);
    }

    public void selectNextHint(){
        if(selectedHint < Math.min(MAX_SUGGESTION_SHOWN-1,currentHints.size()-1)){
            selectedHint++;
        }
    }

    public void selectPreviousHint(){
        if(selectedHint > 0){
            selectedHint--;
        }
    }

    /**
     * Loads dictionaries from a directory
     * @param dir The directory
     */
    public void loadFromDirectory(String dir){
        /*File folder = new File(directory);
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
        }*/
        String path = "/"+dir;
        URL url = getClass().getResource(path);
        if (url != null) {
            File directory = new File(url.getPath());
            if (directory.isDirectory()) {
                for (File file : directory.listFiles()) {
                    if(!file.isDirectory()) {
                        KeywordDictionary nw = new KeywordDictionary();
                        nw.loadFromFile(dir+"/"+file.getName());

                        dictionaries.add(nw);
                    }
                }
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
