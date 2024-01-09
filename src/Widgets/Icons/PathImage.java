package Widgets.Icons;

import Utility.Position;
import Utility.Size;
import Widgets.Icons.PathOperations.PathDrawable;
import Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

public class PathImage {
    private ArrayList<PathDrawable> oparations = new ArrayList<>();

    private Size size;
    private Theme theme;

    public PathImage(Size size) {
        this.size = size;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getWidth(){
        return size.width;
    }
    public int getHeight(){
        return size.height;
    }

    public void draw(Graphics2D g2, Position startPosition){
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if(theme == null){
            return;
        }

        g2.setClip(startPosition.x, startPosition.y, size.width,size.height);
        Position position = new Position(startPosition.x,startPosition.y);

        for(PathDrawable operation: oparations){
            operation.draw(g2,position,theme);
        }
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void add(PathDrawable drawable){
        oparations.add(drawable);
    }
}
