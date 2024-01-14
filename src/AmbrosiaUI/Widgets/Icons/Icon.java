package AmbrosiaUI.Widgets.Icons;

import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;

public class Icon extends Frame {

    private PathImage image;
    private final String backgroundColorHover;

    public Icon(String backgroudColor, String backgroundColorHover, PathImage image) {
        super(backgroudColor, 0);
        this.backgroundColorHover = backgroundColorHover;
        this.image = image;
    }

    @Override
    public void setTheme(Theme Theme) {
        super.setTheme(Theme);
        image.setTheme(Theme);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        setupDraw(g2);
        super.drawSelf(g2);

        if(this.mouseOver){
            g2.setColor(theme.getColorByName(backgroundColorHover));
            g2.fillRect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
        }

        if(image != null) {
            image.draw(g2, new Position(
                    this.getX()+this.getWidth()/2-image.getWidth()/2,
                    this.getY()+this.getHeight()/2-image.getHeight()/2
            ));
        }
    }

    public PathImage getImage() {
        return image;
    }

    public void setImage(PathImage image) {
        this.image = image;
    }
}
