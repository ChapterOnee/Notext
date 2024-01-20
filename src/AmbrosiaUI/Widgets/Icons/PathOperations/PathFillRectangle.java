package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;

public class PathFillRectangle implements PathDrawable {
    private final String color;

    private final Rectangle rect;

    public PathFillRectangle(String color, Rectangle rect) {
        this.color = color;
        this.rect = rect;
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme, double scale) {
        g2.setColor(Theme.getColorByName(color));
        g2.fillRect(
                (int) (rect.x) + currentPosition.x,
                (int) (rect.y) + currentPosition.y, (int) (rect.width), (int) (rect.height));
    }
}
