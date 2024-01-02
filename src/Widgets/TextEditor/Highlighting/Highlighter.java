package Widgets.TextEditor.Highlighting;

import Utility.FileLoader;
import Widgets.TextEditor.EditorLineGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Highlighter extends FileLoader {
    private ArrayList<HighlightQuery> queries = new ArrayList<>();
    private ArrayList<String> applies_to = new ArrayList<>();
    private String name;

    public Highlighter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addApplicable(String name){
        this.applies_to.add(name);
    }

    public void addQuery(HighlightQuery query){
        this.queries.add(query);
    }

    @Deprecated
    public ArrayList<EditorLineGroup> generateGroupsFromText(String text){
        ArrayList<HighlightGroup> all_highlight_groups = new ArrayList<>();
        ArrayList<EditorLineGroup> output_groups = new ArrayList<>();

        for(HighlightQuery qer: queries){
            all_highlight_groups.addAll(qer.findGroupsInText(text));
        }

        Collections.sort(all_highlight_groups);

        if(!all_highlight_groups.isEmpty()) {
            //
            //  Get all found strings
            //
            ArrayList<String> found_text = new ArrayList<>();
            for(HighlightGroup grp: all_highlight_groups){
                String content = grp.getContent();
                // Escape special regex characters if needed
                content = Pattern.quote(content);
                found_text.add(content);
            }
            //System.out.println(found_text);

            //
            //  Split text in designated places
            //
            String splitter =  String.join("|", found_text);
            //System.out.println(splitter);
            ArrayList<String> splits = new ArrayList<>(List.of(text.split(splitter)));

            for(String s: splits){
                output_groups.add(new EditorLineGroup(s));
            }

            if(!output_groups.isEmpty()) {
                HighlightGroup current_highlight_group;
                for (int i = 0; i < all_highlight_groups.size(); i++) {
                    current_highlight_group = all_highlight_groups.get(i);

                    int index = (i*2)+1;
                    if(index > output_groups.size()){
                        index = output_groups.size()-1;
                    }

                    output_groups.add(index,
                            new EditorLineGroup(
                                    current_highlight_group.getContent(),
                                    current_highlight_group.getBackgroundColor(),
                                    current_highlight_group.getForegroundColor())
                    );
                }
            }
            else{
                output_groups.add(
                        new EditorLineGroup(
                                all_highlight_groups.get(0).getContent(),
                                all_highlight_groups.get(0).getBackgroundColor(),
                                all_highlight_groups.get(0).getForegroundColor()
                        )
                );
            }
        }
        else {
            output_groups.add(new EditorLineGroup(text));
        }

        return output_groups;
    }


    public ArrayList<HighlightGroup> generateHighlights(String text){
        ArrayList<HighlightGroup> all_highlight_groups = new ArrayList<>();
        //ArrayList<HighlightGroup> output_groups = new ArrayList<>();

        for(HighlightQuery qer: queries){
            all_highlight_groups.addAll(qer.findGroupsInText(text));
        }

        return all_highlight_groups;
    }
    /*
        config for .*\.txt
        config for [regex for filename]

        group [color name] [regex]
        group accent ("[^"]*")|'[^']*'
     */

    @Override
    public void handleTag(String tag, String name, String args) {
        switch (tag) {
            case "config" -> {
                String[] arguments = args.split(",");

                switch (name){
                    case "for" -> {
                        if(arguments.length < 1){
                            System.out.println("Invalid arguments for 'for'.");
                            return;
                        }

                        this.applies_to.addAll(List.of(arguments));
                    }
                    case "name" -> {
                        if(arguments.length < 1){
                            System.out.println("Invalid arguments for 'name'.");
                            return;
                        }

                        this.name = arguments[0];
                    }
                }

            }
            case "group" -> {
                if(args.startsWith("\"") && args.endsWith("\"")){
                    args = args.substring(1,args.length()-1);
                }
                this.queries.add(new HighlightQuery(args,"none",name));
            }
        }
    }

    public ArrayList<String> getApplies_to() {
        return applies_to;
    }
}
