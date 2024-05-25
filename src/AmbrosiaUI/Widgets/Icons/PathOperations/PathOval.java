package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Operation that draws and oval
 */
public class PathOval implements PathDrawable {

    private Position start;
    private Position end;

    private String color;

    private int lineWidth;

    public PathOval(ArrayList<String> args){
        this.fromArguments(args);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme) {
        g2.setColor(Theme.getColorByName(color));
        g2.setStroke(new BasicStroke(lineWidth));
        g2.drawOval(this.start.x,this.start.y,this.end.x-this.start.x, this.end.y-this.start.y);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(lineWidth));
        g2.drawOval(this.start.x,this.start.y,this.end.x-this.start.x, this.end.y-this.start.y);
    }

    @Override
    public void fromArguments(ArrayList<String> arguments) {
        start = new Position(Integer.parseInt(arguments.get(0)),Integer.parseInt(arguments.get(1)));
        end = new Position(Integer.parseInt(arguments.get(2)),Integer.parseInt(arguments.get(3)));
        lineWidth = Integer.parseInt(arguments.get(4));
        color = arguments.get(5);
    }

    @Override
    public ArrayList<String> toArguments() {
        ArrayList<String> output = new ArrayList<>();
        output.add(start.x + "");
        output.add(start.y + "");
        output.add(end.x + "");
        output.add(end.y + "");
        output.add(lineWidth + "");
        output.add(color);
        return output;
    }

    @Override
    public InterpretedCommand.ArgumentType[] getArgumentTypes() {
        return new InterpretedCommand.ArgumentType[]{
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT,
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT,
                InterpretedCommand.ArgumentType.INT,
                InterpretedCommand.ArgumentType.STRING
        };
    }

    @Override
    public String getName() {
        return "oval";
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public ArrayList<Position> getPositions() {
        return new ArrayList<>(List.of(
                start,
                end
        ));
    }
}
