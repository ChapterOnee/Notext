package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;

public class PathRectangle implements PathDrawable{
    private final String color;

    private final Rectangle rect;

    private final int width;

    public PathRectangle(String color, Rectangle rect, int width) {
        this.color = color;
        this.rect = rect;
        this.width = width;
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme, double scale) {
        g2.setColor(Theme.getColorByName(color));
        g2.setStroke(new BasicStroke(width));
        g2.drawRect(rect.x+ currentPosition.x, rect.y+ currentPosition.y, rect.width, rect.height);
    }
}
