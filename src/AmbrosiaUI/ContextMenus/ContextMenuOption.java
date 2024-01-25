package AmbrosiaUI.ContextMenus;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;

public class ContextMenuOption {
    private String text;
    private Rectangle boundingRectangle;

    private boolean mouseOver;

    private PathImage image;

    public ContextMenuOption(String text) {
        this.text = text;
    }
    public ContextMenuOption(String text, PathImage image) {
        this.text = text;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    protected void execute(){

    }

    public void draw(Graphics2D g2, Theme theme){
        g2.setFont(theme.getFontByName("small"));

        Rectangle textRect = getBoundingRectangle();

        if(image != null){
            image.setTheme(theme);
            //System.out.println(image.getCenteredPosition(getBoundingRectangle()) + " " + image.getOparations());
            image.getScaled((double) boundingRectangle.getHeight() / image.getHeight()).draw(g2, new Position(boundingRectangle.getX()-boundingRectangle.getHeight(),boundingRectangle.getY()));
        }

        if(mouseOver){
            g2.setColor(theme.getColorByName("accent"));
            g2.fillRect(textRect.getX(),textRect.getY(),textRect.getWidth(),textRect.getHeight());
            g2.setColor(theme.getColorByName("accentText"));
        }
        else{
            g2.setColor(theme.getColorByName("text1"));
        }

        AdvancedGraphics.drawText(g2,textRect,getText(), AdvancedGraphics.Side.LEFT);
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public void setBoundingRectangle(Rectangle boundingRectangle) {
        this.boundingRectangle = boundingRectangle;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }
}
