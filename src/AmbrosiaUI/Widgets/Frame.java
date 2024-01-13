package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.GraphicsBorderModifier;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;

import java.awt.*;

public class Frame extends Widget{
    protected String backgroudColor = "secondary";

    protected String onHoverBackgroundColor = "secondary";

    protected GraphicsBorderModifier borderModifier = new GraphicsBorderModifier(true, true ,true ,true);

    protected int borderWidth = 0;

    protected String borderColor = "accent";

    public Frame(String backgroudColor, int margin) {
        this.backgroudColor = backgroudColor;
        this.onHoverBackgroundColor = backgroudColor;
        this.margin = margin;
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        setupDraw(g2);
        g2.setColor(theme.getColorByName(backgroudColor));

        //if(mouseOver){
        //    g2.setColor(new Color(255,0,0));
        //}
        Rectangle bounding_rect = this.getBoundingRect();

        String bg = backgroudColor;
        if(mouseOver){
            bg = onHoverBackgroundColor;
        }
        AdvancedGraphics.borderedRect(g2, bounding_rect, this.borderWidth,
                theme.getColorByName(bg),
                theme.getColorByName(borderColor),
                borderModifier
        );

        super.drawSelf(g2);
    }

    public String getBackgroudColor() {
        return backgroudColor;
    }

    public void setBackgroudColor(String backgroudColor) {
        this.backgroudColor = backgroudColor;
    }

    public String getOnHoverBackgroundColor() {
        return onHoverBackgroundColor;
    }

    public void setOnHoverBackgroundColor(String onHoverBackgroundColor) {
        this.onHoverBackgroundColor = onHoverBackgroundColor;
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
