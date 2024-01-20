package AmbrosiaUI.Widgets.Icons.PathOperations;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;

public class PathMove implements PathDrawable{

    private final Position movement;

    public PathMove(Position movement) {
        this.movement = movement;
    }

    public PathMove(int x, int y) {
        this.movement = new Position(x,y);
    }

    @Override
    public void draw(Graphics2D g2, Position currentPosition, Theme Theme, double scale) {
        currentPosition.move(movement);
    }
}
