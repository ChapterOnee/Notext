package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Utility.Size;

import java.awt.*;
import java.util.ArrayList;

public class SelectBox extends Frame{
    private ArrayList<String> options = new ArrayList<>();
    private Size itemSize = new Size(100,40);

    public SelectBox(String backgroudColor, int margin) {
        super(backgroudColor, margin);
    }

    public void addOption(String text){
        options.add(text);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);

        if(mouseOver){
            g2.setClip(null);
            g2.setColor(theme.getColorByName(this.getBackgroudColor()));

            AdvancedGraphics.borderedRect(g2,
                    this.getX(),this.getY()+this.getHeight(),Math.max(this.getWidth(), itemSize.width), itemSize.height*options.size(),
                    2, theme.getColorByName("secondary"), theme.getColorByName("primary"), AdvancedGraphics.BORDER_FULL
            );

            int y = 0;
            for(String option: options){
                //System.out.println(y);
                AdvancedGraphics.drawText(g2, new Rectangle(
                        this.getX(),
                        this.getY()+this.getHeight()+y,
                        itemSize.width,
                        itemSize.height
                ), option, AdvancedGraphics.Side.LEFT);

                y += itemSize.height;
            }
            //g2.fillRect(this.getX(),this.getY()+this.getHeight(),Math.max(this.getWidth(), itemSize.width), itemSize.height*items.size());
        }
    }

    @Override
    public ArrayList<Rectangle> getMouseHoverRectangles() {
        ArrayList<Rectangle> rects = super.getMouseHoverRectangles();
        if(mouseOver){
            rects.add(new Rectangle(this.getX(),this.getY()+this.getHeight()-5,Math.max(this.getWidth(), itemSize.width),itemSize.height*options.size()));
        }
        //System.out.println(rects + " " + mouseOver);
        return rects;
    }
}
