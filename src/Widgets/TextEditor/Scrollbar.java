package Widgets.TextEditor;

import Utility.Rectangle;
import Utility.UnitValue;
import Widgets.Frame;
import Widgets.Placements.ScrollController;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Scrollbar extends Frame {
    private ScrollController controller;
    private UnitValue.Direction direction;
    private int handleSize = 100;

    private String handleColor = "secondary";
    private String handleColorHover = "accent";

    private int handleMargin = 2;

    private boolean grabbed = false;

    public Scrollbar(String backgroudColor, ScrollController controller, UnitValue.Direction direction) {
        super(backgroudColor, 0);
        this.controller = controller;
        this.direction = direction;

        this.onHoverBackgroundColor = backgroudColor;
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        setupDraw(g2);
        super.drawSelf(g2);

        Rectangle handle = getHandle();


        g2.setColor(theme.getColorByName(handleColor));

        if(lastMousePosition.inRectangle(handle)){
            g2.setColor(theme.getColorByName(handleColorHover));
        }

        g2.fillRect(handle.getX()+handleMargin,handle.getY()+handleMargin,handle.getWidth() - handleMargin*2,handle.getHeight() - handleMargin*2);
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        if(lastMousePosition.inRectangle(getHandle())){
            grabbed = true;
            return;
        }
        grabbed = false;
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        grabbed = false;
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        if(grabbed){
            if(direction == UnitValue.Direction.HORIZONTAL){
                double offset = ((double)controller.getMaxScrollX()/this.getWidth())*(e.getX()-this.getX());

                controller.setScrollX((int) offset);
            }
            else{
                double offset = ((double)controller.getMaxScrollY()/this.getHeight())*(e.getY()-this.getY());

                controller.setScrollY((int) offset);
            }
        }
    }

    public Rectangle getHandle(){
        double handleX = this.getX();
        double handleY = this.getY();
        int handleWidth = handleSize;
        int handleHeight = handleSize;

        if(direction == UnitValue.Direction.HORIZONTAL){
            handleX += ((double)(this.getWidth()-handleSize)/controller.getMaxScrollX())*controller.getScrollX();
            handleHeight = this.getHeight();
        }
        else{
            handleY += ((double)(this.getHeight()-handleSize)/controller.getMaxScrollY())*controller.getScrollY();
            handleWidth = this.getWidth();
        }

        return new Rectangle((int) handleX, (int) handleY, handleWidth, handleHeight);
    }
}
