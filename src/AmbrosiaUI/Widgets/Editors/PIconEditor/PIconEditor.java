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
import java.util.LinkedHashMap;
import java.util.List;

public class PIconEditor extends Frame implements EditorLike {

    private PathImage currentImage;
    private String currentFile;

    private ArrayList<Position> selected = new ArrayList<>();

    private ScrollController componentsScroll = new ScrollController(0,0);

    private boolean shiftDown = false;

    private Rectangle selectionRectangle;

    private LinkedHashMap<String,String> colors = new LinkedHashMap<>();

    public PIconEditor(String backgroudColor, int margin) {
        super(backgroudColor, margin);

        setBorderColor("secondary");
        setBorderWidth(2);
        setBorderModifier(new GraphicsBorderModifier(false,false,false,true));

        reloadColors();
    }

    public PIconEditor() {
        super("primary", 0);

        setBorderColor("secondary");
        setBorderWidth(2);
        setBorderModifier(new GraphicsBorderModifier(false,false,false,true));

        reloadColors();
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
        int lineHeight = 20;
        int maxWidth = 0;
        for (String text: theme.getColors().keySet()){
            maxWidth = Math.max(maxWidth, getStringWidth(text, theme.getFontByName("normal"))+lineHeight+10);
        }

        int hintX = this.getContentX();
        int hintY = this.getContentY()+2;

        int y = 0;
        for(String text: theme.getColors().keySet()){
            g2.setColor(theme.getColorByName("text2"));
            AdvancedGraphics.drawText(g2,
                    new Rectangle(hintX+10+lineHeight,
                            hintY+y,
                            maxWidth-lineHeight,
                            lineHeight),
                    text,
                    AdvancedGraphics.Side.LEFT
            );
            g2.setColor(theme.getColors().get(text));
            g2.fillRect(hintX, hintY+y, lineHeight, lineHeight);
            y += lineHeight;
        }

        //
        //  Draw down right hint
        //
        ArrayList<String> hintText = new ArrayList<>();
        hintText.add("Real size:" + currentImage.getWidth() + " x " + currentImage.getHeight());
        hintText.add("E: add new line");
        hintText.add("Delete, D: delete selected objects");
        hintText.add("Q: cycle colors on selected objects");
        hintText.add("W: add a fillPoly4");

        maxWidth = 0;
        for (String text: hintText){
            maxWidth = Math.max(maxWidth, getStringWidth(text, theme.getFontByName("normal"))+20);
        }

        hintX = this.getContentX()+this.getContentWidth()-maxWidth;
        hintY = this.getContentY();

        g2.setColor(theme.getColorByName("secondary"));
        g2.fillRect(
                hintX,
                hintY,
                maxWidth,
                hintText.size()*lineHeight+20
        );

        g2.setColor(theme.getColorByName("text1"));
        y = 0;
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

        ArrayList<String> hoverHints = new ArrayList<>();

        HashMap<String, ArrayList<Position>> grabbable = getGrabbablePositions();
        for(String key: grabbable.keySet()){
            ArrayList<Position> positions = grabbable.get(key);

            if(positions.size() == 1){
                PathDrawable op = getAssociatedOperation(positions.get(0));
                Position realPos = getRealPosition(positions.get(0));

                drawGrabPoint(g2,realPos, op.getColor());

                if(realPos.getDistanceTo(lastMousePosition) < 10){
                    hoverHints.add(op.getName() + "->" + op.getColor());
                    currentImage.drawOperation(g2, getImagePosition(), op, theme.getColorByName("selection"));

                    drawSelection(g2, realPos,op.getColor());
                }
                else if(selected.contains(positions.get(0))){
                    drawSelection(g2, realPos, op.getColor());
                }

                continue;
            }

            double segment = (Math.PI*2) / (double)positions.size();
            int grabOffsetX, grabOffsetY;
            int radius = 12;
            int i = 0;
            Position finalPositon;

            for (Position pos: positions){
                grabOffsetX = (int) (Math.cos(segment*i) * radius);
                grabOffsetY = (int) (Math.sin(segment*i) * radius);

                finalPositon = getRealPosition(pos).getOffset(grabOffsetX,grabOffsetY);
                PathDrawable op = getAssociatedOperation(pos);

                drawGrabPoint(g2,finalPositon, op.getColor());
                if(finalPositon.getDistanceTo(lastMousePosition) < 10){
                    hoverHints.add(op.getName() + "->" + op.getColor());
                    currentImage.drawOperation(g2, getImagePosition(), op, theme.getColorByName("selection"));

                    drawSelection(g2, finalPositon, op.getColor());
                }
                else if(selected.contains(pos)){
                    drawSelection(g2, finalPositon,op.getColor());
                }

                i += 1;
            }
        }

        AdvancedGraphics.drawHint(g2, lastMousePosition.getOffset(10,10), hoverHints, theme);

        componentsScroll.setMaxScrollY(0);

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

    public void reloadColors(){
        if(theme == null){
            return;
        }
        colors.clear();
        String lastKey = "";
        String firstColor = null;
        for(String color: theme.getColors().keySet()){
            if(firstColor == null){
                firstColor = color;
            }
            colors.put(lastKey, color);
            lastKey = color;
        }
        colors.put(lastKey,firstColor);
    }

    public String getNextColor(String color){
        return colors.get(color);
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
                    int radius = 12;
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
                shiftDown = false;
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


    public Position getCursorPositionOnImage(){
        return lastMousePosition.getOffset(getImagePosition().getMultiplied(-1)).getDivided(currentImage.getScale());
    }
    @Override
    public void onKeyPressed(KeyEvent keyEvent) {
        System.out.println(keyEvent.getKeyCode());
        Position cursorPositonOnImage = getCursorPositionOnImage();
        switch (keyEvent.getKeyCode()){
            case 16 -> shiftDown = true;
            // Delete
            case 127, 68 -> {
                for(Position pos: selected){
                    currentImage.getOparations().remove(getAssociatedOperation(pos));
                }
            }
            // E
            case 69 -> {
                Position nextPos = cursorPositonOnImage;

                currentImage.getOparations().add(new PathLine(
                        cursorPositonOnImage.getOffset(0,0),
                        nextPos,
                        "secondary",
                        1
                ));
            }
            // W
            case 87 -> {

                currentImage.getOparations().add(new PathFillPoly(
                        cursorPositonOnImage.getOffset(0,0),
                        cursorPositonOnImage.getOffset(10,0),
                        cursorPositonOnImage.getOffset(10,10),
                        cursorPositonOnImage.getOffset(0,10),
                        "secondary"
                ));
            }
            // Q
            case 81 -> {
                if(colors.isEmpty()){
                    reloadColors();
                }

                ArrayList<PathDrawable> alreadyChanged = new ArrayList<>();
                for(Position pos: selected){
                    PathDrawable current = getAssociatedOperation(pos);
                    if(alreadyChanged.contains(current)){
                        continue;
                    }
                    alreadyChanged.add(current);

                    current.setColor(getNextColor(current.getColor()));
                }
            }
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
