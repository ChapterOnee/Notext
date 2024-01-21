package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

public class PathRectangle implements PathDrawable{
    private String color;

    private Rectangle rect;

    private int width;

    public PathRectangle(String color, Rectangle rect, int width) {
        this.color = color;
        this.rect = rect;
        this.width = width;
    }

    public PathRectangle(ArrayList<String> arguments){
        fromArguments(arguments);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme) {
        g2.setColor(Theme.getColorByName(color));
        g2.setStroke(new BasicStroke(width));
        g2.drawRect(rect.x+ currentPosition.x, rect.y+ currentPosition.y, rect.width, rect.height);
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
        width = Integer.parseInt(arguments.get(5));
    }

    @Override
    public ArrayList<String> toArguments() {
        ArrayList<String> output = new ArrayList<>();

        output.add(rect.x + "");
        output.add(rect.y + "");
        output.add(rect.width + "");
        output.add(rect.height + "");
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
        return "rect";
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
