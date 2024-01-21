package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

public class PathMove implements PathDrawable{

    private Position movement;

    public PathMove(Position movement) {
        this.movement = movement;
    }

    public PathMove(int x, int y) {
        this.movement = new Position(x,y);
    }

    public PathMove(ArrayList<String> arguments){
        fromArguments(arguments);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme, double scale) {
        currentPosition.move(movement);
    }

    @Override
    public void fromArguments(ArrayList<String> arguments) {
        movement = new Position(Integer.parseInt(arguments.get(0)), Integer.parseInt(arguments.get(1)));
    }

    @Override
    public ArrayList<String> toArguments() {
        ArrayList<String> output = new ArrayList<>();
        output.add(movement.x + "");
        output.add(movement.y + "");
        return output;
    }

    @Override
    public InterpretedCommand.ArgumentType[] getArgumentTypes() {
        return new InterpretedCommand.ArgumentType[]{
                InterpretedCommand.ArgumentType.INT,
                InterpretedCommand.ArgumentType.INT
        };
    }

    @Override
    public String getName() {
        return "move";
    }

    public Position getMovement() {
        return movement;
    }

    public void setMovement(Position movement) {
        this.movement = movement;
    }
}
