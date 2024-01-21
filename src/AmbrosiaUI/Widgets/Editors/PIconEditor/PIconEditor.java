package AmbrosiaUI.Widgets.Editors.PIconEditor;

import AmbrosiaUI.Utility.GraphicsBorderModifier;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Editors.EditorLike;
import AmbrosiaUI.Widgets.Editors.TextEditor.EditorLine;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Icons.PathOperations.PathDrawable;
import AmbrosiaUI.Widgets.Icons.PathOperations.PathLine;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.FileWriter;
import java.io.IOException;

public class PIconEditor extends Frame implements EditorLike {

    private PathImage currentImage;
    private String currentFile;

    private PathDrawable selected;

    public PIconEditor(String backgroudColor, int margin) {
        super(backgroudColor, margin);

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

        g2.setColor(theme.getColorByName("secondary"));
        g2.fillRect(
                getImagePosition().x,
                getImagePosition().y,
                (int)((currentImage.getWidth())*currentImage.getScale()),
                (int)((currentImage.getHeight())*currentImage.getScale())
        );
        currentImage.draw(g2, getImagePosition());

        for(PathDrawable op: currentImage.getOparations()){
            if(op instanceof PathLine ln){
                Position realPosition = ln.getLastPosition().getMultiplied(currentImage.getScale()).getOffset(getImagePosition());

                if(realPosition.getDistanceTo(lastMousePosition) < 20){
                    g2.setColor(theme.getColorByName("selection"));
                    g2.drawArc(realPosition.x-10, realPosition.y-10, 20,20,0,360);
                }
            }
        }

        if(selected instanceof PathLine ln){
            Position realPosition = ln.getLastPosition().getMultiplied(currentImage.getScale()).getOffset(getImagePosition());

            g2.setColor(theme.getColorByName("selection"));
            g2.drawArc(realPosition.x-10, realPosition.y-10, 20,20,0,360);
        }
    }

    public Position getImagePosition(){
        return new Position(
                this.getContentX()+this.getContentWidth()/2-(int)((currentImage.getWidth()/2)*currentImage.getScale()),
                this.getContentY()+this.getContentHeight()/2-(int)((currentImage.getHeight()/2)*currentImage.getScale())
        );
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        if(currentImage == null){
            return;
        }

        for(PathDrawable op: currentImage.getOparations()){
            if(op instanceof PathLine ln){
                Position realPosition = ln.getLastPosition().getMultiplied(currentImage.getScale()).getOffset(getImagePosition());

                if(realPosition.getDistanceTo(lastMousePosition) < 20){
                    selected = ln;
                }
            }
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
        currentFile = filename;
        currentImage = new PathImage(filename);
        currentImage.setTheme(theme);
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        currentImage.setScale(Math.max(0.5,event.getWheelRotation()+currentImage.getScale()));
    }

    @Override
    public String getAllowedFiles() {
        return ".*\\.pimg";
    }
}
