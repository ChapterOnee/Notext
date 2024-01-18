package AmbrosiaUI.Widgets.Editors.HexEditor;

import AmbrosiaUI.Utility.EventStatus;
import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Widgets.Editors.EditorLike;
import AmbrosiaUI.Widgets.Placements.ScrollController;
import AmbrosiaUI.Widgets.Widget;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class HexEditor extends Widget implements EditorLike {
    private byte[] contents = new byte[0];

    protected ScrollController scrollController = new ScrollController(0,0);

    private final Position offset = new Position(60,60);
    private String currentFile;

    private int bytesPerRow = 16;

    @Override
    public void drawSelf(Graphics2D g2) {
        setupDraw(g2);

        // Calculations
        FontMetrics fm = g2.getFontMetrics(theme.getFontByName("normal"));
        //int single_char_width = fm.stringWidth(" " + cursor.getCurrrentCharsUnderCursor().charAt(0));
        int text_height = this.getLineHeight();
        int contentHeight = (contents.length / bytesPerRow) * text_height;
        this.scrollController.setMaxScrollY(contentHeight);

        // Draw background
        g2.setColor(theme.getColorByName("primary"));
        g2.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());

        g2.setColor(theme.getColorByName("text1"));

        AffineTransform at = new AffineTransform();
        at.translate(-scrollController.getScrollX(), -scrollController.getScrollY());

        g2.setTransform(at);

        int currentX, currentY, totalwidth = 0, realCurrentX, realCurrentY;
        byte[] fullrow = new byte[bytesPerRow];

        Position underCursor = getByteUnderCursor();

        String byteString;
        for(int i = 0; i < contents.length; i ++){
            byteString = String.format("%02X", contents[i]);

            realCurrentX = (i%bytesPerRow);
            realCurrentY = (i/bytesPerRow);

            currentX = realCurrentX * 30;
            currentY = realCurrentY * getLineHeight();

            fullrow[i%bytesPerRow] = contents[i];

            if(realCurrentX == underCursor.x && realCurrentY == underCursor.y){
                g2.setColor(theme.getColorByName("selection"));
                g2.fillRect(
                        this.getX()+offset.x+currentX,
                        this.getY()+offset.y+currentY-text_height,
                        fm.stringWidth(byteString),
                        text_height
                );
                g2.setColor(theme.getColorByName("text2"));
            }


            if(currentX == 0){
                g2.drawString(new String(fullrow), this.getX()+offset.x+totalwidth + 100, this.getY()+offset.y+currentY);
                totalwidth = 0;
            }

            g2.drawString(byteString,
                    this.getX()+offset.x+currentX,
                    this.getY()+offset.y+currentY
            );

            totalwidth += fm.stringWidth(byteString) + 10;
        }

        AffineTransform at2 = new AffineTransform();
        at2.translate(0 , 0);

        g2.setTransform(at2);

        super.drawSelf(g2);
    }

    @Override
    public void update(EventStatus eventStatus){
        super.update(eventStatus);
        if(mouseOver){
            getByteUnderCursor();
        }
    }

    public Position getByteUnderCursor(){
        int x = (lastMousePosition.x-this.getX()-offset.x+ scrollController.getScrollX()) / 30;
        int y = (lastMousePosition.y-this.getY()-offset.y+ scrollController.getScrollY()+getLineHeight()/2) / getLineHeight();

        return new Position(x,y);
    }

    public int getLineHeight(){
        return fontsizecanvas.getFontMetrics(theme.getFontByName("normal")).getAscent() + fontsizecanvas.getFontMetrics(theme.getFontByName("normal")).getDescent();
    }

    @Override
    public String getCurrentFile() {
        return currentFile;
    }

    @Override
    public void setCurrentFile(String filename) {
        openFile(filename);
    }

    @Override
    public boolean hasFile() {
        return contents.length == 0;
    }

    @Override
    public void onCurrentFileChanged() {

    }

    @Override
    public void revert() {

    }

    @Override
    public void clear() {
        contents = new byte[0];
    }

    @Override
    public void saveToCurrentlyOpenFile() {
        if(currentFile != null) {
            try {
                Files.write(Path.of(currentFile), contents);
            } catch (IOException e) {
                Logger.printError("Failed to write file in hex editor: " + e);
            }
        }
        else{
            Logger.printError("Cannot save, no files is open.");
        }
    }

    @Override
    public void openFile(String filename) {
        try {
            contents = Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            Logger.printError("Failed to load file in hex editor: " + e);
        }
    }

    @Override
    public String getAllowedFiles() {
        return ".*";
    }

    @Override
    public ScrollController getScrollController() {
        return scrollController;
    }
}
