package Utility;

import java.awt.*;

public class AdvancedGraphics {
    public static final GraphicsBorderModifier BORDER_FULL = new GraphicsBorderModifier(true,true,true,true);
    public static void borderedRect(Graphics2D g2, int x, int y, int width, int height, int borderWidth, Color fill, Color border, GraphicsBorderModifier borderModifier){
        g2.setColor(border);
        g2.fillRect(x,y,width,height);

        g2.setColor(fill);
        g2.fillRect(
                x+borderWidth * (borderModifier.isLeft() ? 1 : 0),
                y+borderWidth * (borderModifier.isTop() ? 1 : 0),
                width - ((borderModifier.isLeft() ? 1 : 0) + (borderModifier.isRight() ? 1 : 0)) * borderWidth,
                height - ((borderModifier.isTop() ? 1 : 0) + (borderModifier.isBottom() ? 1 : 0)) * borderWidth
        );
    }
    public static void borderedRect(Graphics2D g2, Rectangle rect, int borderWidth, Color fill, Color border, GraphicsBorderModifier borderModifier){
        AdvancedGraphics.borderedRect(g2,rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight(),borderWidth,fill,border, borderModifier);
    }

    public static void drawCenteredText(Graphics2D g2, Rectangle bounding_rect, String text){
        FontMetrics fm = g2.getFontMetrics(g2.getFont());
        g2.drawString(text,
                bounding_rect.getX()+ bounding_rect.getWidth()/2 - fm.stringWidth(text)/2,
                bounding_rect.getY()+ bounding_rect.getHeight()/2 + fm.getHeight()/2
        );
    }
}
