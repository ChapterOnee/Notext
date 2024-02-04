package App;

import AmbrosiaUI.Utility.FileInterpreter.FileInterpreter;
import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.FileUtil;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Widgets.Editors.TextEditor.Highlighting.Highlighter;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Theme;

import java.util.ArrayList;

public class Extension extends FileInterpreter {
    private String directory;
    private String name;
    private String description;

    private PathImage icon;

    public Extension(String directory, Theme theme) {
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
