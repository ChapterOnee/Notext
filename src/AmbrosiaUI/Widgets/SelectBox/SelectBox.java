package AmbrosiaUI.Widgets.SelectBox;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Widgets.Label;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SelectBox extends Label {
    private ArrayList<SelectBoxOption> options = new ArrayList<>();
    private Size itemSize = new Size(100,40);

    private int selected = -1;


    public SelectBox(String font, int borderWidth, int margin, int padding) {
        super("", font, borderWidth, margin, padding);
    }

    public void addOption(SelectBoxOption option){
        option.setIndex(options.size());
        options.add(option);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);

        if(mouseOver){
            setzIndex(9999);
            g2.setClip(null);
            g2.setColor(theme.getColorByName(this.getBackgroundColor()));

            AdvancedGraphics.borderedRect(g2,
                    this.getX(),this.getY()+this.getHeight(),Math.max(this.getWidth(), itemSize.width), itemSize.height*options.size(),
                    0, theme.getColorByName(backgroundColor), theme.getColorByName(borderColor), AdvancedGraphics.BORDER_FULL
            );

            String option;
            Rectangle textRectangle;
            for(int i = 0; i < options.size();i++){
                option = options.get(i).getText();
                textRectangle = getRectangleForOption(i);

                if(lastMousePosition.inRectangle(textRectangle)){
                    g2.setColor(theme.getColorByName(onHoverBackgroundColor));
                    g2.fillRect(textRectangle.getX(),textRectangle.getY(),textRectangle.getWidth(),textRectangle.getHeight());
                    g2.setColor(theme.getColorByName(onHoverForegroundColor));
                }
                else{
                    g2.setColor(theme.getColorByName(foregroundColor));
                }


                //System.out.println(y);
                AdvancedGraphics.drawText(g2, textRectangle, option, AdvancedGraphics.Side.CENTER);
            }
            //g2.fillRect(this.getX(),this.getY()+this.getHeight(),Math.max(this.getWidth(), itemSize.width), itemSize.height*items.size());
        }
        else {
            setzIndex(1);
        }
    }

    public Rectangle getRectangleForOption(int index){
        if(index < 0 || index >= options.size()) {
            return null;
        }

        return new Rectangle(
                this.getX(),
                this.getY()+this.getHeight()+itemSize.height*index,
                itemSize.width,
                itemSize.height
        );
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        for(int i = 0; i < options.size();i++) {
            if(new Position(e.getX(),e.getY()).inRectangle(getRectangleForOption(i))){
                this.setSelected(i);
                break;
            };
        }
    }

    public SelectBoxOption getSelected(){
        if(options.isEmpty()){
            return new SelectBoxOption("");
        }
        if(selected == -1){
            selected = 0;
        }
        return options.get(selected);
    }

    @Override
    public String getText() {
        return getSelected().getText();
    }

    public void setSelected(int selected){
        if(selected < 0 || selected >= options.size()){
            if(!options.isEmpty()) {
                this.setSelected(0);
            }
            return;
        }

        this.selected = selected;
        this.options.get(selected).onSelected();
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

    public Size getItemSize() {
        return itemSize;
    }

    public void setItemSize(Size itemSize) {
        this.itemSize = itemSize;
    }

    public void selectLast() {
        this.setSelected(this.options.size()-1);
    }
}
