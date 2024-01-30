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
import java.util.HashMap;

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

        //
        // Draw image grid background
        //
        g2.setColor(theme.getColorByName("secondary"));
        g2.drawRect(
                getImagePosition().x,
                getImagePosition().y,
                imageWidth,
                imageHeight
        );
        g2.drawLine(getImagePosition().x + imageWidth/2, getImagePosition().y, getImagePosition().x + imageWidth/2, getImagePosition().y+imageHeight);
        g2.drawLine(getImagePosition().x, getImagePosition().y + imageHeight/2, getImagePosition().x+imageWidth, getImagePosition().y+ imageHeight/2);

        //
        //  Draw up right hint
        //


        ArrayList<String> hintText = new ArrayList<>();
        hintText.add("Real size:" + currentImage.getWidth() + " x " + currentImage.getHeight());

        int lineHeight = 20;
        int maxWidth = 0;
        for (String text: hintText){
            maxWidth = Math.max(maxWidth, getStringWidth(text, theme.getFontByName("normal"))+20);
        }

        int hintX = this.getContentX();
        int hintY = this.getContentY()+this.getContentHeight()-(hintText.size()*lineHeight+20);

        g2.setColor(theme.getColorByName("secondary"));
        g2.fillRect(
                hintX,
                hintY,
                maxWidth,
                hintText.size()*lineHeight+20
        );

        g2.setColor(theme.getColorByName("text1"));
        int y = 0;
        for(String text: hintText){
            AdvancedGraphics.drawText(g2,
                    new Rectangle(hintX+10,
                            hintY+y,
                            maxWidth,
                            lineHeight),
                    text,
                    AdvancedGraphics.Side.LEFT
            );
            y += lineHeight;
        }

        currentImage.draw(g2, getImagePosition());


        int offsetY = 0;
        int textWidth = 150;
        int previewWidth = currentImage.getWidth() + textWidth;
        for(PathDrawable op: currentImage.getOparations()){
            Position preview_pos = this.getContentPosition().getOffset(this.getContentWidth()-previewWidth,offsetY-componentsScroll.getScrollY());

            g2.setColor(theme.getColorByName("secondary"));
            g2.fillRect(preview_pos.x, preview_pos.y, previewWidth, currentImage.getHeight());

            if(op.getPositions().size() > 0) {
                g2.setColor(theme.getColorByName("accent"));
                int segment = textWidth / op.getPositions().size();
                int i = 0;
                for (Position pos : op.getPositions()) {
                    if (selected.contains(pos)) {
                        g2.fillRect(preview_pos.x + segment * i, preview_pos.y, segment, currentImage.getHeight() / 10);
                    }
                    i++;
                }
            }

            g2.setColor(theme.getColorByName("text1"));
            AdvancedGraphics.drawText(g2,new Rectangle(preview_pos.x+10, preview_pos.y, previewWidth, currentImage.getHeight()),
                    op.getName() + "[" + currentImage.getOparations().indexOf(op) + "]", AdvancedGraphics.Side.LEFT);

            g2.setColor(theme.getColorByName("selection"));
            g2.fillRect(preview_pos.x+textWidth, preview_pos.y, currentImage.getWidth(), currentImage.getHeight());

            op.draw(g2,preview_pos.getOffset(textWidth,0),theme);

            offsetY += currentImage.getHeight()+5;
        }

        HashMap<String, ArrayList<Position>> grabbable = getGrabbablePositions();
        for(String key: grabbable.keySet()){
            ArrayList<Position> positions = grabbable.get(key);

            if(positions.size() == 1){
                drawGrabPoint(g2,getRealPosition(positions.get(0)), getAssociatedOperation(positions.get(0)).getColor());

                if(getRealPosition(positions.get(0)).getDistanceTo(lastMousePosition) < 10){
                    drawSelection(g2, getRealPosition(positions.get(0)), getAssociatedOperation(positions.get(0)).getColor());
                }
                else if(selected.contains(positions.get(0))){
                    drawSelection(g2, getRealPosition(positions.get(0)), getAssociatedOperation(positions.get(0)).getColor());
                }

                continue;
            }

            double segment = (Math.PI*2) / (double)positions.size();
            int grabOffsetX, grabOffsetY;
            int radius = 10;
            int i = 0;
            Position finalPositon;

            for (Position pos: positions){
                grabOffsetX = (int) (Math.cos(segment*i) * radius);
                grabOffsetY = (int) (Math.sin(segment*i) * radius);

                finalPositon = getRealPosition(pos).getOffset(grabOffsetX,grabOffsetY);


                drawGrabPoint(g2,finalPositon, getAssociatedOperation(pos).getColor());
                if(finalPositon.getDistanceTo(lastMousePosition) < 10){
                    drawSelection(g2, finalPositon, getAssociatedOperation(pos).getColor());
                }
                else if(selected.contains(pos)){
                    drawSelection(g2, finalPositon, getAssociatedOperation(pos).getColor());
                }

                i += 1;
            }
        }

        componentsScroll.setMaxScrollY(offsetY);

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

    public PathDrawable getAssociatedOperation(Position pos){
        for(PathDrawable op: currentImage.getOparations()){
            for(Position pos2: op.getPositions()){
                if(pos2 == pos){
                    return op;
                }
            }
        }
        return null;
    }

    public HashMap<String, ArrayList<Position>> getGrabbablePositions(){
        HashMap<String, ArrayList<Position>> grabbable = new HashMap<>();

        for(PathDrawable op: currentImage.getOparations()){
            for(Position pos: op.getPositions()){
                if(grabbable.containsKey(pos.encode())){
                    grabbable.get(pos.encode()).add(pos);
                }
                else {
                    grabbable.put(pos.encode(), new ArrayList<>());
                    grabbable.get(pos.encode()).add(pos);
                }
            }
        }

        return grabbable;
    }

    public Position getImagePosition(){
        return new Position(
                this.getContentX()+this.getContentWidth()/2-(int)((currentImage.getWidth()/2)*currentImage.getScale()),
                this.getContentY()+this.getContentHeight()/2-(int)((currentImage.getHeight()/2)*currentImage.getScale())
        );
    }

    public void drawSelection(Graphics2D g2, Position pos, String color){
        g2.setColor(theme.getColorByName("selection"));
        g2.fillArc(pos.x-9, pos.y-9, 18,18,0,360);
        g2.setColor(theme.getColorByName(color));
        g2.fillArc(pos.x-8, pos.y-8, 16,16,0,360);
    }

    public void drawGrabPoint(Graphics2D g2, Position pos, String color){
        g2.setColor(theme.getColorByName("selection"));
        g2.fillArc(pos.x-8, pos.y-8, 16,16,0,360);
        g2.setColor(theme.getColorByName(color));
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
                HashMap<String, ArrayList<Position>> grabbable = getGrabbablePositions();

                for(String key: grabbable.keySet()){
                    ArrayList<Position> positions = grabbable.get(key);

                    if(positions.size() == 1){
                        if(checkPositionForSelection(positions.get(0),getRealPosition(positions.get(0)))){
                            selectedSomething = true;
                            break;
                        }
                        continue;
                    }

                    double segment = (Math.PI*2) / (double)positions.size();
                    int grabOffsetX, grabOffsetY;
                    int radius = 10;
                    int i = 0;
                    Position finalPositon;

                    for (Position pos: positions){
                        grabOffsetX = (int) (Math.cos(segment*i) * radius);
                        grabOffsetY = (int) (Math.sin(segment*i) * radius);

                        finalPositon = getRealPosition(pos).getOffset(grabOffsetX,grabOffsetY);

                        if(checkPositionForSelection(pos,finalPositon)){
                            selectedSomething = true;
                            break;
                        }

                        i += 1;
                    }
                    if(selectedSomething){
                        break;
                    }
                }

                /*for (PathDrawable op : currentImage.getOparations()) {
                    for(Position pos: op.getPositions()){
                        if(checkPositionForSelection(pos)){
                            selectedSomething = true;
                            break;
                        }
                    }
                    if (selectedSomething){
                        break;
                    }
                }*/
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

    public Position getRealPosition(Position pos){
        return pos.getMultiplied(currentImage.getScale()).getOffset(getImagePosition());
    }

    public boolean checkPositionForSelection(Position pos, Position checkPosition){
        if (checkPosition.getDistanceTo(lastMousePosition) < 10) {
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
            selected.remove(pos);
            return false;
        }

        selected.add(pos);
        return true;
    }

    private final Position lastMouseDragged = new Position(0,0);
    @Override
    public void onMouseDragged(MouseEvent e) {
        if(currentImage == null){
            return;
        }

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
        if(currentImage == null){
            return;
        }

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
        if(!filename.matches(getAllowedFiles())){
            return;
        }

        setCurrentFile(filename);
        currentImage = new PathImage(filename);
        currentImage.setTheme(theme);

        currentImage.setScale((double)(this.getContentHeight()-100)/currentImage.getHeight());
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

    @Override
    public void reload() {
        if(hasFile()){
            openFile(getCurrentFile());
        }
    }
}
