package AmbrosiaUI.Utility;

import java.awt.*;

public class StringUtil {
    protected static final Canvas fontsizecanvas = new Canvas();

    public static int getStringWidth(String text, Font font){
        if(text == null){
            return 0;
        }
        return fontsizecanvas.getFontMetrics(font).stringWidth(text);
    }
}
