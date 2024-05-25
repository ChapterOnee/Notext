package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Keybind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A button widget
 */
public class Button extends Label {
    private Keybind bind;

    public Button(String text, String font, int borderWidth, int margin, int padding) {
        super(text, font, borderWidth, margin, padding);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);
        /*
            On hover
        */

        if(bind != null) {
            String bindName = bind.getKeybinds().toString();
            bindName = bindName.replace("pressed", "");
            bindName = bindName.replaceAll("( )+", "+");

            AdvancedGraphics.drawText(g2, getBoundingRect().applyMargin(2), bindName, AdvancedGraphics.Side.RIGTH);
        }
        //System.out.println(this.getX() + "x" + this.getY() + " " + text + g2.getFont());
    }
    public void setBind(JPanel panel, KeyStroke keyStroke){
        bind = new Keybind(this.text, panel, keyStroke) {
            @Override
            public void activated(ActionEvent e) {
                if(!disabled) {
                    onMouseClicked(null);
                }
            }
        };
    }
}
