package Widgets.Icons.PathOperations;

import Utility.Position;
import Widgets.Theme;

import java.awt.*;

public interface PathDrawable {
    public void draw(Graphics2D g2, Position currentPosition, Theme theme);
}
