package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

public class PathLine implements PathDrawable{

    private Position to;
    private Position from;
    private String color;

    private int width;

    public PathLine(Position to, Position from, String color, int width) {
        this.to = to;
        this.from = from;
        this.color = color;
        this.width = width;
    }

    public PathLine(ArrayList<String> arguments){
        fromArguments(arguments);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme) {
        g2.setColor(Theme.getColorByName(color));
        g2.setStroke(new BasicStroke(width));
        g2.drawLine(from.x + currentPosition.x, from.y  + currentPosition.y, (currentPosition.x + to.x), (currentPosition.y + to.y));
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(width));
        g2.drawLine(from.x + currentPosition.x, from.y  + currentPosition.y, (currentPosition.x + to.x), (currentPosition.y + to.y));
    }

    @Override
    public void fromArguments(ArrayList<String> arguments) {
        from = new Position(
                Integer.parseInt(arguments.get(0)),
                Integer.parseInt(arguments.get(1))
        );
        to = new Position(
                Integer.parseInt(arguments.get(2)),
                Integer.parseInt(arguments.get(3))
        );
        color = arguments.get(4);
        width = Integer.parseInt(arguments.get(5));
    }

    @Override
    public ArrayList<String> toArguments() {
        ArrayList<String> output = new ArrayList<>();
        output.add(from.x + "");
        output.add(from.y + "");
        output.add(to.x + "");
        output.add(to.y + "");
        output.add(color);
        output.add(width + "");

        return output;
    }

    @Override
    public InterpretedCommand.ArgumentType[] getArgumentTypes() {
        return new InterpretedCommand.ArgumentType[]{
                InterpretedCommand.ArgumentType.INT,
                InterpretedCommand.ArgumentType.INT,
                InterpretedCommand.ArgumentType.INT,
                InterpretedCommand.ArgumentType.INT,
                InterpretedCommand.ArgumentType.STRING,
                InterpretedCommand.ArgumentType.INT
        };
    }

    @Override
    public String getName() {
        return "line";
    }

    @Override
    public ArrayList<Position> getPositions() {
        ArrayList<Position> out = new ArrayList<>();
        out.add(from);
        out.add(to);
        return out;
    }

    public Position getTo() {
        return to;
    }

    public void setTo(Position to) {
        this.to = to;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Position getFrom() {
        return from;
    }

    public void setFrom(Position from) {
        this.from = from;
    }
}
