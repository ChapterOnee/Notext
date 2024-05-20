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

/**
 * A dropdown menu widget
 */
public class DropdownMenu extends Label {

    private final ArrayList<DropdownMenuItem> items;
    private final ArrayList<DropdownMenu> dropdownMenusUnder = new ArrayList<>();
    private final int spacerHeight = 8;
    private boolean inAnotherDropdownMenu = false;

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
                return getItemSpaceRectangle().getPosition();
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
            g2.setColor(theme.getColorByName(this.getBackgroundColor()));

            AdvancedGraphics.borderedRect(g2,
                    getItemSpaceRectangle(),
                    2, theme.getColorByName("secondary"), theme.getColorByName("primary"), AdvancedGraphics.BORDER_FULL
            );
            //g2.fillRect(this.getX(),this.getY()+this.getHeight(),Math.max(this.getWidth(), itemSize.width), itemSize.height*items.size());
        }
    }

    public int getFullItemHeight(){
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
    public ArrayList<Rectangle> getMouseHoverRectangles() {
        ArrayList<Rectangle> rects = super.getMouseHoverRectangles();
        if(mouseOver){
            rects.add(getItemSpaceRectangle());

            for (DropdownMenu menu: dropdownMenusUnder){
                rects.addAll(menu.getMouseHoverRectangles());
            }
        }
        //System.out.println(rects + " " + mouseOver);
        return rects;
    }

    public Rectangle getItemSpaceRectangle(){
        return new Rectangle(
                this.getX() + (inAnotherDropdownMenu ? this.getWidth()-1: 0),
                this.getY() + (!inAnotherDropdownMenu ? this.getHeight()-1 : 0),
                Math.max(this.getWidth(), itemSize.width),
                getFullItemHeight());
    }

    public void addMenuItem(DropdownMenuItem item){
        this.items.add(item);
        int height = itemSize.height;
        if(item.isSpacer()){
            height = spacerHeight;
            item.setBoundWidget(new Frame(foregroundColor,1));
        }

        if(item.getBoundWidget().getClass().getSimpleName().equals("DropdownMenu")){
            ((DropdownMenu) item.getBoundWidget()).setInAnotherDropdownMenu(true);
            ((DropdownMenu) item.getBoundWidget()).setzIndex(this.getzIndex()+1);
            dropdownMenusUnder.add(((DropdownMenu) item.getBoundWidget()));
        }

        verticalPlacement.add(item.getBoundWidget(), new UnitValue(height, UnitValue.Unit.PIXELS));
        verticalPlacement.update();
    }

    @Override
    public ArrayList<Widget> getAllChildren() {
        ArrayList<Widget> output = new ArrayList<>();

        if(mouseOver) {
            for (DropdownMenuItem item : items) {
                item.getBoundWidget().setzIndex(this.getzIndex()+1);
                //System.out.println(item.getBoundWidget().getX() + " " + item.getBoundWidget().getY());
                output.add(item.getBoundWidget());
                output.addAll(item.getBoundWidget().getAllChildren());
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

    public boolean isInAnotherDropdownMenu() {
        return inAnotherDropdownMenu;
    }

    public void setInAnotherDropdownMenu(boolean inAnotherDropdownMenu) {
        this.inAnotherDropdownMenu = inAnotherDropdownMenu;
    }
}
