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
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme) {
        g2.setColor(Theme.getColorByName(color));
        g2.fillRect(rect.x+ currentPosition.x, rect.y+ currentPosition.y, rect.width, rect.height);
    }
}
