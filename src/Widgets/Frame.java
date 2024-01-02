package Widgets;

import java.awt.*;

public class Frame extends Widget{
    private String backgroudColor;

    public Frame(String backgroudColor) {
        this.backgroudColor = backgroudColor;
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        setupDraw(g2);
        g2.setColor(theme.getColorByName(backgroudColor));

        //if(mouseOver){
        //    g2.setColor(new Color(255,0,0));
        //}

        g2.fillRect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
        super.drawSelf(g2);
    }
}
