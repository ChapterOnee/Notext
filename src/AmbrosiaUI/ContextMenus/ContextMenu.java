package AmbrosiaUI.ContextMenus;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Utility.StringUtil;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ContextMenu {
    private ArrayList<ContextMenuOption> options = new ArrayList<>();

    private final int itemHeight = 20;
    private int padding = 10;
    private int itemVerticalPadding = 5;

    private Position position = new Position(0,0);

    private Theme theme;

    public ContextMenu(Theme theme) {
        this.theme = theme;
    }

    public void addOption(ContextMenuOption option){
        options.add(option);
    }

    public void draw(Graphics2D g2){
        AdvancedGraphics.borderedRect(g2, getBoundingRect(), 1, theme.getColorByName("primary"),theme.getColorByName("accent"),AdvancedGraphics.BORDER_FULL);

        g2.setColor(theme.getColorByName("text2"));
        g2.setFont(theme.getFontByName("normal"));
        for (ContextMenuOption option: options){
            AdvancedGraphics.drawText(g2,option.getBoundingRectangle(),option.getText(), AdvancedGraphics.Side.LEFT);
        }
    }

    public Rectangle getBoundingRect(){
        int width = 0;

        int y = 0;
        for (ContextMenuOption text: options){
            int tempWidth = StringUtil.getStringWidth(text.getText(), theme.getFontByName("normal"))+padding*2;
            width = Math.max(width, tempWidth);

            text.setBoundingRectangle(new Rectangle(position.x, position.y+y,tempWidth,itemHeight));
            text.getBoundingRectangle().applyMargin(4);

            y+=itemVerticalPadding+itemHeight;
        }

        int height = (itemHeight+itemVerticalPadding*2)*options.size();

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

    }

    public void onMouseClicked(MouseEvent e){

    }

    public void onMousePressed(MouseEvent e){

    }

    public void onMouseReleased(MouseEvent e){

    }
}
