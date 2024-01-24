package AmbrosiaUI.Widgets.TabbedFrame;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.GraphicsBorderModifier;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Icons.PathOperations.PathDrawable;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.PlacementCell;
import AmbrosiaUI.Widgets.Widget;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class TabbedFrame extends Frame {
    private ArrayList<TabbedFrameTab> tabs = new ArrayList<>();
    private int selectedTab = -1;
    private int tabHeight = 35;
    private GridPlacement corePlacement;

    public TabbedFrame(String backgroudColor, int margin) {
        super(backgroudColor, margin);
    }

    public void initialize(){
        corePlacement = new GridPlacement(theme);
        corePlacement.setRowTemplateFromString("auto");
        corePlacement.setColumnTemplateFromString("auto");
        this.setChildrenPlacement(corePlacement);
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);
        setupDraw(g2);
        g2.setClip(this.getX(),this.getY(),this.getWidth(),this.getHeight());

        if(tabs.size() < 2) {
            return;
        }

        g2.setColor(theme.getColorByName("secondary"));
        g2.fillRect(this.getX(),this.getContentY()-2,this.getWidth(),2);

        String tabBg;
        String tabFg;

        int i = 0;
        for (Rectangle rect: getTabRectangles()){
            TabbedFrameTab tab = tabs.get(i);
            if(i == selectedTab){
                tabBg = "primary";
                tabFg = "text2";
            }
            else {
                tabBg = "secondary";
                tabFg = "text1";
            }


            if (lastMousePosition.inRectangle(rect)){
                tabBg = "accent";
                tabFg = "accentText";
            }

            AdvancedGraphics.borderedRect(g2, rect, 1, theme.getColorByName(tabBg), theme.getColorByName("secondary"), new GraphicsBorderModifier(true, true, false, true));
            g2.setColor(theme.getColorByName(tabFg));
            AdvancedGraphics.drawText(g2,rect,tab.getName(), AdvancedGraphics.Side.CENTER);

            i++;
        }
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        int i = 0;
        for (Rectangle rect: getTabRectangles()) {
            if(lastMousePosition.inRectangle(rect)){
                if(e.getButton() == 1) {
                    selectTab(i);
                }
                else if (e.getButton() == 3){
                    removeTab(i);
                }
            }
            i++;
        }
        window.update();
    }

    public ArrayList<Rectangle> getTabRectangles(){
        ArrayList<Rectangle> output = new ArrayList<>();

        int currentX = 10;
        int textWidth;

        Rectangle rect;
        for (TabbedFrameTab tab: tabs){
            textWidth = getStringWidth(tab.getName(), theme.getFontByName("normal"));
            rect = new Rectangle(this.getX()+currentX, this.getContentY()-tabHeight+10, textWidth+10, tabHeight-10);

            output.add(rect);

            currentX += textWidth + 20;
        }
        return output;
    }

    @Override
    public int getContentHeight() {
        if(tabs.size() > 1){
            return super.getContentHeight() - tabHeight;
        }

        return super.getContentHeight();
    }

    @Override
    public int getContentY() {
        if(tabs.size() > 1){
            return super.getContentY() + tabHeight;
        }

        return super.getContentY();
    }

    public TabbedFrameTab getSelectedTab(){
        if(tabs.isEmpty()){
            return null;
        }

        if(selectedTab == -1){
            selectedTab = 0;
        }

        return tabs.get(selectedTab);
    }

    public TabbedFrameTab addTab(String name){
        Frame frm = new Frame(backgroudColor,0);
        corePlacement.add(frm,0,0,1,1);
        tabs.add(new TabbedFrameTab(frm, name));

        return tabs.get(tabs.size()-1);
    }

    @Override
    public ArrayList<Widget> getChildren() {
        ArrayList<Widget> output = new ArrayList<>();

        if(getSelectedTab() != null) {
            Widget element = getSelectedTab().getBoundElement();

            if (element.getChildrenPlacement() != null) {
                for (PlacementCell cell : element.getChildrenPlacement().getChildren()) {
                    output.add(cell.getBoundElement());
                }
            }
        }

        return output;
    }

    public void selectTab(int index){
        if(index < 0 || index >= tabs.size()){
            return;
        }

        selectedTab = index;
    }

    public void removeTab(TabbedFrameTab tab){
        tabs.remove(tab);

        if (selectedTab != 0){
            selectTab(selectedTab-1);
        }
    }

    public void removeTab(int tabIndex){
        removeTab(tabs.get(tabIndex));
    }

    public ArrayList<TabbedFrameTab> getTabs() {
        return tabs;
    }
}
