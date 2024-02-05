package AmbrosiaUI.Utility;

import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

import static AmbrosiaUI.Utility.StringUtil.getStringWidth;

public class AdvancedGraphics {

    public enum Side{
        TOP,
        RIGTH,
        BOTTOM,
        LEFT,
        CENTER
    }


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
    public static void drawHint(Graphics2D g2, Position pos, ArrayList<String> hints, Theme theme){
        int lineHeight = 20;
        int maxWidth = 0;
        for (String text: hints){
            maxWidth = Math.max(maxWidth, getStringWidth(text, theme.getFontByName("normal"))+20);
        }

        int hintX = pos.x;
        int hintY = pos.y;

        g2.setColor(theme.getColorByName("secondary"));
        g2.fillRect(
                hintX,
                hintY,
                maxWidth,
                hints.size()*lineHeight+20
        );

        g2.setColor(theme.getColorByName("text1"));
        int y = 0;
        for(String text: hints){
            AdvancedGraphics.drawText(g2,
                    new Rectangle(hintX+10,
                            hintY+y,
                            maxWidth,
                            lineHeight),
                    text,
                    AdvancedGraphics.Side.LEFT
            );
            y += lineHeight;
        }
    }
    public static void borderedRect(Graphics2D g2, Rectangle rect, int borderWidth, Color fill, Color border, GraphicsBorderModifier borderModifier){
        AdvancedGraphics.borderedRect(g2,rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight(),borderWidth,fill,border, borderModifier);
    }

    public static void drawText(Graphics2D g2, Rectangle bounding_rect, String text, Side placementSide){
        FontMetrics fm = g2.getFontMetrics(g2.getFont());

        Position pos = getTextPositionFromBoundingRect(fm, bounding_rect, text, placementSide);

        g2.drawString(text, pos.x, pos.y);
    }
    
    public static int getTextHeight(FontMetrics fm){
        return fm.getAscent()+fm.getDescent();
    }

    public static Position getTextPositionFromBoundingRect(FontMetrics fm,Rectangle bounding_rect, String text, Side placementSide){
        int x = bounding_rect.getX();
        int y = bounding_rect.getY() + fm.getAscent();

        switch (placementSide){
            case CENTER -> {
                x += bounding_rect.getWidth()/2 - fm.stringWidth(text)/2;
                y += bounding_rect.getHeight()/2 - fm.getAscent()/2;
            }
            case LEFT -> {
                y += bounding_rect.getHeight()/2 - fm.getAscent()/2;
            }
            case RIGTH -> {
                x += bounding_rect.getWidth() - fm.stringWidth(text);
                y += bounding_rect.getHeight()/2 - fm.getAscent()/2;
            }
            case TOP -> {
                x += bounding_rect.getWidth()/2 - fm.stringWidth(text)/2;
            }
            case BOTTOM -> {
                x += bounding_rect.getWidth()/2 - fm.stringWidth(text)/2;
                y += bounding_rect.getHeight();
            }
        }

        return new Position(x,y);
    }
}
