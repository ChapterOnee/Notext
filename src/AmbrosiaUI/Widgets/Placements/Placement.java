package AmbrosiaUI.Widgets.Placements;
import AmbrosiaUI.Widgets.Theme;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Widgets.Widget;
import AmbrosiaUI.Widgets.Window;

import java.awt.*;
import java.util.ArrayList;

public abstract class Placement {
    protected Position rootPosition;
    protected Size rootSize;

    protected Widget parent;

    protected ArrayList<PlacementCell> children = new ArrayList<>();

    protected Theme theme;

    protected int itemMargin = 0;

    public Position getPosition(int index) {
        //System.out.println(this.getRootPosition() + " " + this.children.get(index).getLastCalculatedPosition() + this.children.get(index));
        return this.getRootPosition().getOffset(this.children.get(index).getLastCalculatedPosition());
    }

    public int getWidth(int index) {
        return this.children.get(index).getLastCalculatedSize().width;
    }

    public int getHeight(int index) {
        return this.children.get(index).getLastCalculatedSize().height;
    }

    public void resize(Size new_size) {
        for(PlacementCell cell: children){
            if(cell.boundElement.getChildrenPlacement() != null){
                cell.boundElement.getChildrenPlacement().resize(new Size(cell.boundElement.getContentWidth(),cell.boundElement.getContentHeight()));
                cell.boundElement.getChildrenPlacement().setRootPosition(new Position(cell.boundElement.getContentX(),cell.boundElement.getContentY()));
            }
        }
        this.setRootSize(new_size);
        onResize();
    }

    public void update(){
        this.resize(this.getRootSize());
    }

    public void remove(Widget w){
        PlacementCell cell;
        for(int i = 0;i < children.size();i++){
            cell = children.get(i);

            if(cell.boundElement == w){
                for(int j = i+1; j < children.size();j++){
                    this.children.get(j).boundElement.setPlacementIndex(j-1);
                }

                this.children.remove(cell);
                break;
            }
        }
    }

    public void onResize(){

    }
    public Position getRootPosition() {
        return rootPosition;
    }

    public void setRootPosition(Position rootPosition) {
        this.rootPosition = rootPosition;
    }

    public Size getRootSize() {
        return rootSize;
    }

    public void setRootSize(Size rootSize) {
        this.rootSize = rootSize;
    }

    public void clear(){
        children.clear();
    }
    public ArrayList<PlacementCell> getChildren() {
        return children;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme Theme) {
        this.theme = Theme;
    }

    public int getItemMargin() {
        return itemMargin;
    }

    public void drawDebug(Graphics2D g2){

    }

    public void setItemMargin(int itemMargin) {
        this.itemMargin = itemMargin;
    }

    public Widget getParent() {
        return parent;
    }

    public void setParent(Widget parent) {
        this.parent = parent;
    }
}
