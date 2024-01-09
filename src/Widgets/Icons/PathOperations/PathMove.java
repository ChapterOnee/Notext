package Widgets.Icons.PathOperations;

import Utility.Position;
import Widgets.Theme;

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
    public void draw(Graphics2D g2, Position currentPosition, Theme theme) {
        currentPosition.move(movement);
    }
}
