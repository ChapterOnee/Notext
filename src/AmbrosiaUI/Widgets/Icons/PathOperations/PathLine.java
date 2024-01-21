package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

public class PathLine implements PathDrawable{

    private Position to;
    private String color;

    private int width;

    private final Position lastPosition = new Position(0,0);

    public PathLine(Position to, String color, int width) {
        this.to = to;
        this.color = color;
        this.width = width;
    }

    public PathLine(int x, int y, String color, int width) {
        this.width = width;
        this.to = new Position(x,y);
        this.color = color;
    }

    public PathLine(ArrayList<String> arguments){
        fromArguments(arguments);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme, double scale) {
        g2.setColor(Theme.getColorByName(color));
        g2.setStroke(new BasicStroke(width));
        g2.drawLine(currentPosition.x,currentPosition.y, (currentPosition.x + to.x), (currentPosition.y + to.y));

        lastPosition.x = (currentPosition.x + to.x);
        lastPosition.y = (currentPosition.y + to.y);

        currentPosition.move(to);
    }

    @Override
    public void fromArguments(ArrayList<String> arguments) {
        to = new Position(
                Integer.parseInt(arguments.get(0)),
                Integer.parseInt(arguments.get(1))
        );
        color = arguments.get(2);
        width = Integer.parseInt(arguments.get(3));
    }

    @Override
    public ArrayList<String> toArguments() {
        ArrayList<String> output = new ArrayList<>();
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
                InterpretedCommand.ArgumentType.STRING,
                InterpretedCommand.ArgumentType.INT
        };
    }

    @Override
    public String getName() {
        return "lineTo";
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

    public Position getLastPosition() {
        return lastPosition;
    }
}
