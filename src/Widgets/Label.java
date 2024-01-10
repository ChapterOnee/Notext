package Widgets;

import Utility.AdvancedGraphics;
import Utility.GraphicsBorderModifier;

import java.awt.*;

public class Label extends Frame{
    protected String text;
    protected String foregroundColor = "text1";
    protected String onHoverForegroundColor = "text2";
    protected String font;

    protected AdvancedGraphics.Side textPlacement = AdvancedGraphics.Side.CENTER;

    public Label(String text, String font, int borderWidth, int margin) {
        super("secondary", margin);
        this.text = text;
        this.font = font;
        this.borderWidth = borderWidth;

        this.onHoverBackgroundColor = "accent";
    }

    public Label(String text, String foregroundColor, String backgroudColor, String font, GraphicsBorderModifier borderModifier, int borderWidth, int margin, String borderColor) {
        super(backgroudColor, margin);
        this.text = text;
        this.foregroundColor = foregroundColor;
        this.backgroudColor = backgroudColor;
        this.font = font;
        this.borderModifier = borderModifier;
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;

        this.onHoverBackgroundColor = "accent";
    }
    
    @Override
    public void drawSelf(Graphics2D g2) {
        setupDraw(g2);
        g2.setFont(theme.getFontByName(font));

        /*
            Draw background and border
         */
        super.drawSelf(g2);

        if(!mouseOver){
            g2.setColor(theme.getColorByName(foregroundColor));
        }
        else{
            g2.setColor(theme.getColorByName(onHoverForegroundColor));
        }


        AdvancedGraphics.drawText(g2, this.getBoundingRect(), text, textPlacement);
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

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }
}
