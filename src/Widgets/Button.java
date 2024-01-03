package Widgets;

import java.awt.*;

public class Button extends Label {

    public Button(String text, String font, int borderWidth, int margin) {
        super(text, font, borderWidth, margin);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);
        /*
            On hover
         */
        //System.out.println(this.getX() + "x" + this.getY() + " " + text + g2.getFont());
    }
}