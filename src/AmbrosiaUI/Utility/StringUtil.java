package AmbrosiaUI.Utility;

import java.awt.*;

/**
 * This is used just to get the width of strings in pixels
 */
public class StringUtil {
    protected static final Canvas fontsizecanvas = new Canvas();

    public static int getStringWidth(String text, Font font){
        if(text == null){
            return 0;
        }
        return fontsizecanvas.getFontMetrics(font).stringWidth(text);
    }
}
