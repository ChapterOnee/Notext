package AmbrosiaUI.Widgets.Editors.TextEditor.Hinter;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Editors.EditorLike;
import AmbrosiaUI.Widgets.Editors.TextEditor.Cursor;
import AmbrosiaUI.Widgets.Editors.TextEditor.EditorLine;
import AmbrosiaUI.Widgets.Editors.TextEditor.TextEditor;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hinter {
    private ArrayList<String> currentHints = new ArrayList<>();

    private TextEditor editor;
    private Cursor cursor;

    public Hinter(TextEditor editor, Cursor cursor) {
        this.editor = editor;
        this.cursor = cursor;
    }

    public void reloadHints(){
        currentHints.clear();

        EditorLine currentLine = editor.getLine(cursor.getY());

        String currentWord = "";
        Pattern patternW = Pattern.compile("\\s(\\w+)$", Pattern.MULTILINE); // Finding all words
        Matcher matcherW = patternW.matcher(currentLine.getText());

        if(matcherW.find()){
            currentWord = matcherW.group();
        }
        else{
            return;
        }

        Pattern pattern = Pattern.compile("\\b(\\w+)\\b", Pattern.MULTILINE); // Finding all words
        Matcher matcher = pattern.matcher(editor.getFullContent());


        while(matcher.find()){
            System.out.println("(?:.*" + currentWord + ")");
            if(matcher.group().matches("(?:.*" + currentWord + ")")){
                currentHints.add(matcher.group());
            }
        }
    }
    public void draw(Graphics2D g2, int x, int y){
        Theme theme = editor.getTheme();

        Shape lastClip = g2.getClip();

        int width = 200;
        int height = editor.getLineHeight()*4;

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
                    height
            ), hint, AdvancedGraphics.Side.CENTER);
            yOffset += editor.getLineHeight();
        }

        g2.setClip(lastClip);
    }
}
