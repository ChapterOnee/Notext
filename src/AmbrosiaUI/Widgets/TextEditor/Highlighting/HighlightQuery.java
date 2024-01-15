package AmbrosiaUI.Widgets.TextEditor.Highlighting;

import AmbrosiaUI.Utility.Position;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighlightQuery {
    private String pattern;
    private String backgroundColor;
    private String foregroundColor;

    public HighlightQuery(String pattern, String backgroundColor, String foregroundColor) {
        this.pattern = pattern;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
    }

    public ArrayList<HighlightGroup> findGroupsInText(String text, int offsetY){
        Pattern pattern = Pattern.compile(this.pattern);
        Matcher matcher = pattern.matcher(text);

        ArrayList<HighlightGroup> found = new ArrayList<>();
        String found_group;
        String[] split_text;
        int y;
        int x;
        if(matcher.find()) {
            do {
                found_group = matcher.group();
                split_text = text.substring(0,matcher.start()+1).split("\n");
                //System.out.println(text.substring(0,matcher.start()) + "->" + Arrays.toString(split_text));

                y = split_text.length-1;
                if (y < 0) {
                    y = 0;
                    x = 0;
                }
                else{
                    x = split_text[split_text.length-1].length()-1;
                }


                found.add(new HighlightGroup(found_group, backgroundColor, foregroundColor, matcher.start(), new Position(x,y+offsetY)));
            } while (matcher.find());
        }

        return found;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }
}
