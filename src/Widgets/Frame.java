package Widgets;

import Utility.AdvancedGraphics;
import Utility.GraphicsBorderModifier;
import Utility.Rectangle;

import java.awt.*;

public class Frame extends Widget{
    protected String backgroudColor = "secondary";

    protected String onHoverBackgroundColor = "secondary";

    protected GraphicsBorderModifier borderModifier = new GraphicsBorderModifier(true, true ,true ,true);

    protected int borderWidth = 0;

    protected String borderColor = "accent";

    public Frame(String backgroudColor, int margin) {
        this.backgroudColor = backgroudColor;
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
}
