package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Rectangle;

import java.awt.*;

public class Label extends Frame {
    protected String text;
    protected String foregroundColor = "text1";
    protected String disabledForegroundColor = "text1_disabled";
    protected String onHoverForegroundColor = "text2";
    protected String font;

    protected AdvancedGraphics.Side textPlacement = AdvancedGraphics.Side.CENTER;

    protected int padding = 0;

    public Label(String text, String font, int borderWidth, int margin, int padding) {
        super("secondary", margin);
        this.text = text;
        this.font = font;
        this.borderWidth = borderWidth;
        this.padding = padding;

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

        if(disabled){
            g2.setColor(theme.getColorByName(disabledForegroundColor));
        }

        Rectangle rect = new Rectangle(this.getX(),this.getY(),this.getWidth(),this.getHeight());
        rect.applyMargin(padding);

        AdvancedGraphics.drawText(g2, rect, getText(), textPlacement);
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

    public String getOnHoverForegroundColor() {
        return onHoverForegroundColor;
    }

    public void setOnHoverForegroundColor(String onHoverForegroundColor) {
        this.onHoverForegroundColor = onHoverForegroundColor;
    }

    public AdvancedGraphics.Side getTextPlacement() {
        return textPlacement;
    }

    public void setTextPlacement(AdvancedGraphics.Side textPlacement) {
        this.textPlacement = textPlacement;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }
}
