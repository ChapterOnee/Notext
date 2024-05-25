package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Theme;


import java.awt.*;
import java.util.ArrayList;

/**
 * Operation that fills a rectangle
 */
public class PathFillRectangle implements PathDrawable {
    private String color;

    private Rectangle rect;

    public PathFillRectangle(String color, Rectangle rect) {
        this.color = color;
        this.rect = rect;
    }

    public PathFillRectangle(ArrayList<String> arguments){
        fromArguments(arguments);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme) {
        g2.setColor(Theme.getColorByName(color));
        g2.fillRect(
                (int) (rect.getX()) + currentPosition.x,
                (int) (rect.getY()) + currentPosition.y, (int) (rect.getWidth()), (int) (rect.getHeight()));
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Color color) {
        g2.setColor(color);
        g2.fillRect(
                (int) (rect.getX()) + currentPosition.x,
                (int) (rect.getY()) + currentPosition.y, (int) (rect.getWidth()), (int) (rect.getHeight()));
    }

    @Override
    public void fromArguments(ArrayList<String> arguments) {
        color = arguments.get(4);
        rect = new Rectangle(
                Integer.parseInt(arguments.get(0)),
                Integer.parseInt(arguments.get(1)),
                Integer.parseInt(arguments.get(2)),
                Integer.parseInt(arguments.get(3))
        );
    }

    @Override
    public ArrayList<Position> getPositions() {
        return new ArrayList<Position>();
    }

    @Override
    public ArrayList<String> toArguments() {
        ArrayList<String> output = new ArrayList<>();

        output.add(rect.getX() + "");
        output.add(rect.getY() + "");
        output.add(rect.getWidth() + "");
        output.add(rect.getHeight() + "");
        output.add(color);

        return output;
    }

    @Override
    public InterpretedCommand.ArgumentType[] getArgumentTypes() {
        return new InterpretedCommand.ArgumentType[]{
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // Rectangle
                InterpretedCommand.ArgumentType.STRING, InterpretedCommand.ArgumentType.INT // color, width
        };
    }

    @Override
    public String getName() {
        return "fillRect";
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }
}
