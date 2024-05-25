package AmbrosiaUI.ContextMenus;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Utility.StringUtil;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * A class for a simple context menu
 */
public class ContextMenu {
    private ArrayList<ContextMenuOption> options = new ArrayList<>();

    private final int itemHeight = 22;
    private int padding = 5;
    private int itemVerticalPadding = 0;

    private Position position = new Position(0,0);

    private Theme theme;

    public ContextMenu(Theme theme) {
        this.theme = theme;
    }

    public void addOption(ContextMenuOption option){
        options.add(option);
    }

    public void draw(Graphics2D g2){
        AdvancedGraphics.borderedRect(g2, getBoundingRect(), 1, theme.getColorByName("secondary"),theme.getColorByName("primary"),AdvancedGraphics.BORDER_FULL);

        for (ContextMenuOption option: options){
            option.draw(g2, theme);
        }
    }

    /**
     * @return Rectangle of the outer bounds of the menu
     */
    public Rectangle getBoundingRect(){
        int width = 0;

        for (ContextMenuOption text: options){
            int tempWidth = StringUtil.getStringWidth(text.getText(), theme.getFontByName("small"));
            width = Math.max(width, tempWidth);
        }
        width += padding*2 + 100;

        int y = padding;
        for(ContextMenuOption option: options){
            option.setBoundingRectangle(new Rectangle(position.x+padding+itemHeight, position.y+y+itemVerticalPadding,width-padding*2-itemHeight,itemHeight));

            y+=itemVerticalPadding*2+itemHeight;
        }

        int height = (itemHeight+itemVerticalPadding*2)*options.size()+padding*2;

        return new Rectangle(position.x,position.y,width,height);
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getItemVerticalPadding() {
        return itemVerticalPadding;
    }

    public void setItemVerticalPadding(int itemVerticalPadding) {
        this.itemVerticalPadding = itemVerticalPadding;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void onMouseDragged(MouseEvent e){

    }

    public void onMouseMoved(MouseEvent e){
        for (ContextMenuOption option: options){
            option.setMouseOver(new Position(e.getX(), e.getY()).inRectangle(option.getBoundingRectangle()));
        }
    }

    public void onMouseClicked(MouseEvent e){
        for (ContextMenuOption option: options){
            if(option.isMouseOver()){
                option.execute();
            }
        }
    }

    public void onMousePressed(MouseEvent e){

    }

    public void onMouseReleased(MouseEvent e){

    }
}
