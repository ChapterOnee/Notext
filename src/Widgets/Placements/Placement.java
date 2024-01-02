package Widgets.Placements;
import Widgets.Theme;
import Utility.Position;
import Utility.Size;

import java.util.ArrayList;

public abstract class Placement {
    protected Position rootPosition;
    protected Size rootSize;

    protected ArrayList<PlacementCell> children = new ArrayList<>();

    protected Theme theme;
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
                cell.boundElement.getChildrenPlacement().resize(new Size(cell.boundElement.getWidth(),cell.boundElement.getHeight()));
                cell.boundElement.getChildrenPlacement().setRootPosition(new Position(cell.boundElement.getX(),cell.boundElement.getY()));
            }
        }
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

    public ArrayList<PlacementCell> getChildren() {
        return children;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
