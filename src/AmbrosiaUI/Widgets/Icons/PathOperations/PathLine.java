package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;

public class PathLine implements PathDrawable{

    private final Position to;
    private final String color;

    private final int width;

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

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme) {
        g2.setColor(Theme.getColorByName(color));
        g2.setStroke(new BasicStroke(width));
        g2.drawLine(currentPosition.x,currentPosition.y, currentPosition.x + to.x, currentPosition.y + to.y);
        currentPosition.move(to);
    }
}
