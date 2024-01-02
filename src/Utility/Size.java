package Utility;

import java.awt.*;

public class Size {
    public int width;
    public int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Size fromDimension(Dimension dimension){
        return new Size(dimension.width, dimension.height);
    }

    @Override
    public String toString() {
        return "Size{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
