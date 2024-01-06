package Widgets;

import Utility.AdvancedGraphics;
import Utility.GraphicsBorderModifier;
import Utility.Rectangle;

import java.awt.*;

public class Label extends Widget{
    protected String text;
    protected String foregroundColor = "text1";
    protected String backgroudColor = "secondary";

    protected String onHoverBackgroundColor = "accent";
    protected String onHoverForegroundColor = "text2";
    protected String font;

    protected GraphicsBorderModifier borderModifier = new GraphicsBorderModifier(true, true ,true ,true);

    protected int borderWidth = 2;
    protected int margin = 2;

    protected String borderColor = "accent";

    public Label(String text, String font, int borderWidth, int margin) {
        this.text = text;
        this.font = font;
        this.borderWidth = borderWidth;
        this.margin = margin;
    }

    public Label(String text, String foregroundColor, String backgroudColor, String font, GraphicsBorderModifier borderModifier, int borderWidth, int margin, String borderColor) {
        this.text = text;
        this.foregroundColor = foregroundColor;
        this.backgroudColor = backgroudColor;
        this.font = font;
        this.borderModifier = borderModifier;
        this.borderWidth = borderWidth;
        this.margin = margin;
        this.borderColor = borderColor;
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        setupDraw(g2);
        g2.setFont(theme.getFontByName(font));

        FontMetrics fm = g2.getFontMetrics(g2.getFont());

        g2.setClip(this.getX(),this.getY(),this.getWidth(),this.getHeight());

        /*
            Draw background and border
         */

        Rectangle bounding_rect = this.getBoundingRect();
        bounding_rect.applyMargin(margin);

        String bg = backgroudColor;
        if(mouseOver){
            bg = onHoverBackgroundColor;
        }
        AdvancedGraphics.borderedRect(g2, bounding_rect, this.borderWidth,
                theme.getColorByName(bg),
                theme.getColorByName(borderColor),
                borderModifier
        );

        if(!mouseOver){
            g2.setColor(theme.getColorByName(foregroundColor));
        }
        else{
            g2.setColor(theme.getColorByName(onHoverForegroundColor));
        }


        AdvancedGraphics.drawCenteredText(g2, this.getBoundingRect(), text);

        super.drawSelf(g2);
        //System.out.println(this.getX() + "x" + this.getY() + " " + text + g2.getFont());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getBackgroudColor() {
        return backgroudColor;
    }

    public void setBackgroudColor(String backgroudColor) {
        this.backgroudColor = backgroudColor;
    }

    public GraphicsBorderModifier getBorderModifier() {
        return borderModifier;
    }

    public void setBorderModifier(GraphicsBorderModifier borderModifier) {
        this.borderModifier = borderModifier;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }
}
