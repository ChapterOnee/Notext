package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;

public class PathFillPoly implements PathDrawable{
    private Position pos1;
    private Position pos2;
    private Position pos3;
    private Position pos4;

    private final String color;

    public PathFillPoly(Position pos1, Position pos2, Position pos3, Position pos4, String color) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
        this.pos4 = pos4;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme) {
        g2.setColor(Theme.getColorByName(color));

        Polygon p = new Polygon(new int[]{
                pos1.x + currentPosition.x,
                pos2.x + currentPosition.x,
                pos3.x + currentPosition.x,
                pos4.x + currentPosition.x
        },
        new int[]{
                pos1.y + currentPosition.y,
                pos2.y + currentPosition.y,
                pos3.y + currentPosition.y,
                pos4.y + currentPosition.y
        }, 4);

        g2.fillPolygon(p);
    }
}
