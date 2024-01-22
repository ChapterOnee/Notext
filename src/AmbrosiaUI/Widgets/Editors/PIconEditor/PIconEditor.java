package AmbrosiaUI.Widgets.Editors.PIconEditor;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.GraphicsBorderModifier;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Editors.EditorLike;
import AmbrosiaUI.Widgets.Editors.TextEditor.EditorLine;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Icons.PathOperations.PathDrawable;
import AmbrosiaUI.Widgets.Icons.PathOperations.PathFillPoly;
import AmbrosiaUI.Widgets.Icons.PathOperations.PathLine;
import AmbrosiaUI.Widgets.Placements.ScrollController;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PIconEditor extends Frame implements EditorLike {

    private PathImage currentImage;
    private String currentFile;

    private ArrayList<Position> selected = new ArrayList<>();

    private ScrollController componentsScroll = new ScrollController(0,0);

    private boolean shiftDown = false;

    private Rectangle selectionRectangle;

    public PIconEditor(String backgroudColor, int margin) {
        super(backgroudColor, margin);

        setBorderColor("secondary");
        setBorderWidth(2);
        setBorderModifier(new GraphicsBorderModifier(false,false,false,true));
    }

    public PIconEditor() {
        super("primary", 0);

        setBorderColor("secondary");
        setBorderWidth(2);
        setBorderModifier(new GraphicsBorderModifier(false,false,false,true));
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);
        setupDraw(g2);

        if(currentImage == null){
            return;
        }

        int imageWidth = (int)((currentImage.getWidth())*currentImage.getScale());
        int imageHeight = (int)((currentImage.getHeight())*currentImage.getScale());

        g2.setColor(theme.getColorByName("secondary"));
        g2.drawRect(
                getImagePosition().x,
                getImagePosition().y,
                imageWidth,
                imageHeight
        );
        g2.drawLine(getImagePosition().x + imageWidth/2, getImagePosition().y, getImagePosition().x + imageWidth/2, getImagePosition().y+imageHeight);
        g2.drawLine(getImagePosition().x, getImagePosition().y + imageHeight/2, getImagePosition().x+imageWidth, getImagePosition().y+ imageWidth/2);

        currentImage.draw(g2, getImagePosition());

        int offsetY = 0;
        for(PathDrawable op: currentImage.getOparations()){
            Position preview_pos = this.getContentPosition().getOffset(this.getContentWidth()-currentImage.getWidth(),offsetY-componentsScroll.getScrollY());

            g2.setColor(theme.getColorByName("secondary"));
            g2.drawRect(preview_pos.x, preview_pos.y, currentImage.getWidth(), currentImage.getHeight());
            g2.setColor(theme.getColorByName("selection"));
            g2.fillRect(preview_pos.x, preview_pos.y, currentImage.getWidth(), currentImage.getHeight());
            op.draw(g2,preview_pos,theme);

            if(op instanceof PathLine ln){
                Position realPosition = ln.getFrom().getMultiplied(currentImage.getScale()).getOffset(getImagePosition());
                Position realPosition2 = ln.getTo().getMultiplied(currentImage.getScale()).getOffset(getImagePosition());

                if(realPosition.getDistanceTo(lastMousePosition) < 20){
                    drawSelection(g2,realPosition);
                }
                else if(realPosition2.getDistanceTo(lastMousePosition) < 20){
                    drawSelection(g2,realPosition2);
                }

                drawGrabPoint(g2,realPosition);
                drawGrabPoint(g2,realPosition2);
            }
            else if (op instanceof PathFillPoly ln){
                Position realPosition = ln.getPos1().getMultiplied(currentImage.getScale()).getOffset(getImagePosition());
                Position realPosition2 = ln.getPos2().getMultiplied(currentImage.getScale()).getOffset(getImagePosition());
                Position realPosition3 = ln.getPos3().getMultiplied(currentImage.getScale()).getOffset(getImagePosition());
                Position realPosition4 = ln.getPos4().getMultiplied(currentImage.getScale()).getOffset(getImagePosition());

                if(realPosition.getDistanceTo(lastMousePosition) < 20){
                    drawSelection(g2,realPosition);
                }
                else if(realPosition2.getDistanceTo(lastMousePosition) < 20){
                    drawSelection(g2,realPosition2);
                }
                else if(realPosition3.getDistanceTo(lastMousePosition) < 20){
                    drawSelection(g2,realPosition3);
                }
                else if(realPosition4.getDistanceTo(lastMousePosition) < 20){
                    drawSelection(g2,realPosition4);
                }

                drawGrabPoint(g2,realPosition);
                drawGrabPoint(g2,realPosition2);
                drawGrabPoint(g2,realPosition3);
                drawGrabPoint(g2,realPosition4);
            }

            offsetY += currentImage.getHeight()+5;
        }

        componentsScroll.setMaxScrollY(offsetY);

        if(selected != null){
            for(Position pos: selected) {
                drawSelection(g2, pos.getMultiplied(currentImage.getScale()).getOffset(getImagePosition()));
            }
        }

        if(selectionRectangle != null){
            Color c = new Color(
                    theme.getColorByName("selection").getRed(),
                    theme.getColorByName("selection").getGreen(),
                    theme.getColorByName("selection").getBlue(),
                    127
            );

            AdvancedGraphics.borderedRect(g2, selectionRectangle, 1, c, theme.getColorByName("secondary"), AdvancedGraphics.BORDER_FULL);
        }
    }

    public Position getImagePosition(){
        return new Position(
                this.getContentX()+this.getContentWidth()/2-(int)((currentImage.getWidth()/2)*currentImage.getScale()),
                this.getContentY()+this.getContentHeight()/2-(int)((currentImage.getHeight()/2)*currentImage.getScale())
        );
    }

    public void drawSelection(Graphics2D g2, Position pos){
        g2.setColor(theme.getColorByName("selection"));
        g2.drawArc(pos.x-10, pos.y-10, 20,20,0,360);
    }

    public void drawGrabPoint(Graphics2D g2, Position pos){
        g2.setColor(theme.getColorByName("selection"));
        g2.fillArc(pos.x-5, pos.y-5, 10,10,0,360);
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        if(currentImage == null){
            return;
        }

        boolean selectedSomething = false;
        switch (e.getButton()) {
            case 1 -> {
                for (PathDrawable op : currentImage.getOparations()) {
                    for(Position pos: op.getPositions()){
                        if(checkPositionForSelection(pos)){
                            selectedSomething = true;
                            break;
                        }
                    }
                    if (selectedSomething){
                        break;
                    }
                }
            }
            case 3 -> {
                selected.clear();
            }
            default -> {}
        }

        if (!selectedSomething && !shiftDown){
            selectionRectangle = new Rectangle(e.getX(),e.getY(),0,0);
        }

        Position mousePos = new Position(e.getX() - getImagePosition().x, e.getY() - getImagePosition().y).getDivided(currentImage.getScale());

        lastMouseDragged.x = mousePos.x;
        lastMouseDragged.y = mousePos.y;
    }

    @Override
    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == 16){
            shiftDown = true;
        }
    }

    @Override
    public void onKeyReleased(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == 16){
            shiftDown = false;
        }
    }

    public boolean checkPositionForSelection(Position pos){
        Position realPosition = pos.getMultiplied(currentImage.getScale()).getOffset(getImagePosition());
        if (realPosition.getDistanceTo(lastMousePosition) < 20) {
            return addSelectedPosition(pos);
        }
        return false;
    }
    @Override
    public Position getContentPosition() {
        return super.getContentPosition().getOffset(
                borderWidth * (borderModifier.isLeft() ? 1 : 0),
                borderWidth * (borderModifier.isTop() ? 1 : 0)
        );
    }
    private boolean addSelectedPosition(Position pos){
        if(selected.contains(pos)){
            return false;
        }

        selected.add(pos);
        return true;
    }

    private final Position lastMouseDragged = new Position(0,0);
    @Override
    public void onMouseDragged(MouseEvent e) {
        if(selectionRectangle != null){
            selectionRectangle.setWidth(e.getX() - selectionRectangle.getX());
            selectionRectangle.setHeight(e.getY() - selectionRectangle.getY());

            return;
        }

        Position mousePos = new Position(e.getX() - getImagePosition().x, e.getY() - getImagePosition().y).getDivided(currentImage.getScale());

        for(Position pos: selected) {
            pos.x = pos.x + (mousePos.x-lastMouseDragged.x);
            pos.y = pos.y + (mousePos.y-lastMouseDragged.y);
        }

        lastMouseDragged.x = mousePos.x;
        lastMouseDragged.y = mousePos.y;
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        if(selectionRectangle != null){
            Position realPosition;

            for (PathDrawable op : currentImage.getOparations()) {
                for(Position pos: op.getPositions()){
                    realPosition =  pos.getMultiplied(currentImage.getScale()).getOffset(getImagePosition());
                    if(realPosition.inRectangle(selectionRectangle)){
                        addSelectedPosition(pos);
                    }
                }
            }

            selectionRectangle = null;
        }
    }

    @Override
    public String getCurrentFile() {
        return currentFile;
    }

    @Override
    public void setCurrentFile(String filename) {
        currentFile = filename;
        onCurrentFileChanged();
    }

    @Override
    public boolean hasFile() {
        return currentFile != null;
    }

    @Override
    public void onCurrentFileChanged() {

    }

    @Override
    public void revert() {

    }

    @Override
    public void clear() {

    }

    @Override
    public void saveToCurrentlyOpenFile() {
        currentImage.saveToFile(currentFile);
    }

    @Override
    public void openFile(String filename) {
        setCurrentFile(filename);
        currentImage = new PathImage(filename);
        currentImage.setTheme(theme);
    }

    @Override
    public ScrollController getScrollController() {
        return componentsScroll;
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        currentImage.setScale(Math.max(0.5,event.getWheelRotation()+currentImage.getScale()));
    }
    @Override
    public String getAllowedFiles() {
        return ".*\\.pimg";
    }

    @Override
    public boolean dontAutoScroll() {
        return true;
    }
}
