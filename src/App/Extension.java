package App;

import AmbrosiaUI.Utility.FileInterpreter.FileInterpreter;
import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.FileUtil;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Widgets.Editors.TextEditor.Highlighting.Highlighter;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Theme;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * An extension
 */
public class Extension extends FileInterpreter {
    private String directory;
    private String name;
    private String description;

    private PathImage icon;

    private Root root;

    public Extension(String directory, Theme theme, Root root) {
        this.directory = directory;

        this.addCommand(new InterpretedCommand("name", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.STRING}){
            @Override
            public void execute(ArrayList<String> arguments) {
                Extension.this.name = arguments.get(0);
            }
        });
        this.addCommand(new InterpretedCommand("description", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.STRING}){
            @Override
            public void execute(ArrayList<String> arguments) {
                Extension.this.description = arguments.get(0);
            }
        });
        this.addCommand(new InterpretedCommand("icon", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.STRING}){
            @Override
            public void execute(ArrayList<String> arguments) {
                icon = new PathImage(FileUtil.joinPath(directory, arguments.get(0)));
                icon.setTheme(theme);
            }
        });

        this.addCommand(new InterpretedCommand("script", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.STRING}){
            @Override
            public void execute(ArrayList<String> arguments) {
                try(BufferedReader bf = new BufferedReader(new FileReader(FileUtil.joinPath(directory, arguments.get(0))))){
                    StringBuilder allData = new StringBuilder();

                    while (bf.ready()){
                        allData.append(bf.readLine());
                    }

                    Thread askThread = new Thread(() -> { root.getInterpreter().execute(allData.toString()); });
                    askThread.start();  // Start the thread to show the window.
                } catch (IOException ignored) {

                }
            }
        });
    }

    public String getDirectory() {
        return directory;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PathImage getIcon() {
        return icon;
    }
}
