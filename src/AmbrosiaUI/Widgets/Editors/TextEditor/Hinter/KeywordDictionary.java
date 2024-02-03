package AmbrosiaUI.Widgets.Editors.TextEditor.Hinter;

import AmbrosiaUI.Utility.FileInterpreter.FileInterpreter;
import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Widgets.Editors.TextEditor.Highlighting.Highlighter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class KeywordDictionary extends FileInterpreter {
    private ArrayList<String> words = new ArrayList<>();

    private ArrayList<String> applies_to = new ArrayList<>();

    public KeywordDictionary() {
        this.addCommand(new InterpretedCommand("for", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.STRING}){
            @Override
            public void execute(ArrayList<String> arguments) {
                KeywordDictionary.this.applies_to.addAll(arguments);
            }
        });

        this.addCommand(new InterpretedCommand("word", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.STRING}){
            @Override
            public void execute(ArrayList<String> arguments) {
                KeywordDictionary.this.words.addAll(arguments);
            }
        });
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public ArrayList<String> getApplies_to() {
        return applies_to;
    }
}
