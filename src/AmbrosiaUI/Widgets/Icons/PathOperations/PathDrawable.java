package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

public interface PathDrawable {
    void draw(Graphics2D g2, Position currentPosition, Theme Theme);

    void fromArguments(ArrayList<String> arguments);
    ArrayList<String> toArguments();

    InterpretedCommand.ArgumentType[] getArgumentTypes();

    String getName();

    ArrayList<Position> getPositions();

}
