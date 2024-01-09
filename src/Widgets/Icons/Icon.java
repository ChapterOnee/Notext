package Widgets.Icons;

import Utility.Position;
import Widgets.Frame;
import Widgets.Theme;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

public class Icon extends Frame {

    PathImage image;

    public Icon(String backgroudColor, PathImage image) {
        super(backgroudColor);
        this.image = image;
    }

    @Override
    public void setTheme(Theme theme) {
        super.setTheme(theme);
        image.setTheme(theme);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);

        if(image != null) {
            image.draw(g2, new Position(
                    this.getX()+this.getWidth()/2-image.getWidth()/2,
                    this.getY()+this.getHeight()/2-image.getHeight()/2
            ));
        }
    }
}
