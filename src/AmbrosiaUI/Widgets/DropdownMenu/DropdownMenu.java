package AmbrosiaUI.Widgets.DropdownMenu;

import AmbrosiaUI.Utility.*;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Theme;
import AmbrosiaUI.Widgets.Label;
import AmbrosiaUI.Widgets.Placements.VerticalPlacement;
import AmbrosiaUI.Widgets.Widget;

import java.awt.*;
import java.util.ArrayList;

public class DropdownMenu extends Label {

    private ArrayList<DropdownMenuItem> items;

    private final int spacerHeight = 8;

    private VerticalPlacement verticalPlacement;

    private Size itemSize = new Size(40,100);

    public DropdownMenu(String text, String font, int borderWidth, int margin, int padding,  Size itemSize) {
        super(text, font, borderWidth, margin, padding);
        this.itemSize = itemSize;
        this.items = new ArrayList<>();
        this.initilizePlacement();
    }


    private void initilizePlacement(){
        verticalPlacement = new VerticalPlacement(this.getTheme()){
            @Override
            public Size getRootSize() {
                return new Size(itemSize.width, itemSize.height*items.size());
            }
            @Override
            public Position getRootPosition() {
                return DropdownMenu.this.getPosition().getOffset(new Position(0,DropdownMenu.this.getHeight()));
            }
        };
        verticalPlacement.setItemMargin(2);
    }

    @Override
    public void setTheme(Theme Theme) {
        this.verticalPlacement.setTheme(Theme);
        super.setTheme(Theme);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);
        g2.setClip(null);

        if(mouseOver){
            g2.setColor(theme.getColorByName(this.getBackgroudColor()));

            AdvancedGraphics.borderedRect(g2,
                    this.getX(),this.getY()+this.getHeight(),Math.max(this.getWidth(), itemSize.width), getContentHeight(),
                    2, theme.getColorByName("secondary"), theme.getColorByName("primary"), AdvancedGraphics.BORDER_FULL
            );
            //g2.fillRect(this.getX(),this.getY()+this.getHeight(),Math.max(this.getWidth(), itemSize.width), itemSize.height*items.size());
        }
    }

    public int getContentHeight(){
        int total = 0;
        for(DropdownMenuItem item: items){
            if(item.isSpacer()){
                total += spacerHeight;
                continue;
            }
            total += itemSize.height;
        }

        return total;
    }

    @Override
    public void update(EventStatus eventStatus) {
        boolean found = false;

        for(Rectangle rect: this.getMouseHoverRectangles()){
            if(eventStatus.getMousePosition().inRectangle(rect)){
                found = true;
                break;
            }
        }

        mouseOver = found;

        lastMousePosition = eventStatus.getMousePosition();

        for(Widget w: this.getChildren()){
            w.update(eventStatus);
        }

        if(disabled){
            mouseOver = false;
        }
    }

    @Override
    public ArrayList<Rectangle> getMouseHoverRectangles() {
        ArrayList<Rectangle> rects = super.getMouseHoverRectangles();
        if(mouseOver){
            rects.add(new Rectangle(this.getX(),this.getY()+this.getHeight()-5,Math.max(this.getWidth(), itemSize.width),getContentHeight()));
        }
        //System.out.println(rects + " " + mouseOver);
        return rects;
    }

    public void addMenuItem(DropdownMenuItem item){
        this.items.add(item);
        int height = itemSize.height;
        if(item.isSpacer()){
            height = spacerHeight;
            item.setBoundWidget(new Frame(foregroundColor,1));
        }

        verticalPlacement.add(item.getBoundWidget(), new UnitValue(height, UnitValue.Unit.PIXELS));
    }

    @Override
    public ArrayList<Widget> getAllChildren() {
        ArrayList<Widget> output = new ArrayList<>();

        if(mouseOver) {
            for (DropdownMenuItem item : items) {
                item.getBoundWidget().setzIndex(this.getzIndex()+1);
                //System.out.println(item.getBoundWidget().getX() + " " + item.getBoundWidget().getY());
                output.add(item.getBoundWidget());
            }
        }

        return output;
    }

    @Override
    public ArrayList<Widget> getChildren() {
        ArrayList<Widget> output = new ArrayList<>();

        if(mouseOver) {
            for (DropdownMenuItem item : items) {
                //System.out.println(item.getBoundWidget().getX() + " " + item.getBoundWidget().getY());
                output.add(item.getBoundWidget());
            }
        }

        return output;
    }

    public Size getItemSize() {
        return itemSize;
    }

    public void setItemSize(Size itemSize) {
        this.itemSize = itemSize;
    }
}
