package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;
import com.sun.source.tree.ArrayAccessTree;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Operation that fills a polygon
 */
public class PathFillPoly implements PathDrawable{
    private Position pos1;
    private Position pos2;
    private Position pos3;
    private Position pos4;

    private String color;

    public PathFillPoly(Position pos1, Position pos2, Position pos3, Position pos4, String color) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
        this.pos4 = pos4;
        this.color = color;
    }

    public PathFillPoly(ArrayList<String> arguments){
        this.fromArguments(arguments);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme) {
        g2.setColor(Theme.getColorByName(color));

        Polygon p = new Polygon(new int[]{
                pos1.x  + currentPosition.x,
                pos2.x  + currentPosition.x,
                pos3.x  + currentPosition.x,
                pos4.x  + currentPosition.x
        },
        new int[]{
                pos1.y  + currentPosition.y,
                pos2.y  + currentPosition.y,
                pos3.y  + currentPosition.y,
                pos4.y  + currentPosition.y
        }, 4);

        g2.fillPolygon(p);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Color color) {
        g2.setColor(color);

        Polygon p = new Polygon(new int[]{
                pos1.x  + currentPosition.x,
                pos2.x  + currentPosition.x,
                pos3.x  + currentPosition.x,
                pos4.x  + currentPosition.x
        },
                new int[]{
                        pos1.y  + currentPosition.y,
                        pos2.y  + currentPosition.y,
                        pos3.y  + currentPosition.y,
                        pos4.y  + currentPosition.y
                }, 4);

        g2.fillPolygon(p);
    }

    @Override
    public void fromArguments(ArrayList<String> arguments) {
        pos1 = new Position(Integer.parseInt(arguments.get(0)),Integer.parseInt(arguments.get(1)));
        pos2 = new Position(Integer.parseInt(arguments.get(2)),Integer.parseInt(arguments.get(3)));
        pos3 = new Position(Integer.parseInt(arguments.get(4)),Integer.parseInt(arguments.get(5)));
        pos4 = new Position(Integer.parseInt(arguments.get(6)),Integer.parseInt(arguments.get(7)));
        color = arguments.get(8);
    }

    @Override
    public ArrayList<String> toArguments() {
        ArrayList<String> output = new ArrayList<>();
        output.add(pos1.x + "");
        output.add(pos1.y + "");
        output.add(pos2.x + "");
        output.add(pos2.y + "");
        output.add(pos3.x + "");
        output.add(pos3.y + "");
        output.add(pos4.x + "");
        output.add(pos4.y + "");
        output.add(color);
        return output;
    }

    public InterpretedCommand.ArgumentType[] getArgumentTypes() {
        return new InterpretedCommand.ArgumentType[]{
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // p1
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // p2
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // p3
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // p4
                InterpretedCommand.ArgumentType.STRING // color
        };
    }

    @Override
    public ArrayList<Position> getPositions() {
        ArrayList<Position> output = new ArrayList<>();
        output.add(pos1);
        output.add(pos2);
        output.add(pos3);
        output.add(pos4);
        return output;
    }

    @Override
    public String getName() {
        return "fillPoly4";
    }

    public Position getPos1() {
        return pos1;
    }

    public void setPos1(Position pos1) {
        this.pos1 = pos1;
    }

    public Position getPos2() {
        return pos2;
    }

    public void setPos2(Position pos2) {
        this.pos2 = pos2;
    }

    public Position getPos3() {
        return pos3;
    }

    public void setPos3(Position pos3) {
        this.pos3 = pos3;
    }

    public Position getPos4() {
        return pos4;
    }

    public void setPos4(Position pos4) {
        this.pos4 = pos4;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
