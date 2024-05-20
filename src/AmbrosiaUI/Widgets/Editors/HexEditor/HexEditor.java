package AmbrosiaUI.Widgets.Editors.HexEditor;

import AmbrosiaUI.Utility.*;
import AmbrosiaUI.Widgets.Editors.EditorLike;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Placements.ScrollController;
import AmbrosiaUI.Widgets.Widget;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * An editor that can display bytes, and browse trough them
 */
public class HexEditor extends Frame implements EditorLike {
    private byte[] contents = new byte[0];

    protected ScrollController scrollController = new ScrollController(0,0);

    private final Position offset = new Position(60,20);
    private int byteOffeetX = 30;
    private String currentFile;

    private int bytesPerRow = 16;

    public HexEditor() {
        super("primary",0);

        setBorderColor("secondary");
        setBorderWidth(2);
        setBorderModifier(new GraphicsBorderModifier(false,false,false,true));
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        super.drawSelf(g2);
        setupDraw(g2);

        // Calculations
        FontMetrics fm = g2.getFontMetrics(theme.getFontByName("normal"));
        //int single_char_width = fm.stringWidth(" " + cursor.getCurrrentCharsUnderCursor().charAt(0));
        int text_height = this.getLineHeight();
        int contentHeight = this.getFullByteHeight();

        AffineTransform at = new AffineTransform();
        at.translate(-scrollController.getScrollX(), -scrollController.getScrollY());

        g2.setTransform(at);


        int byteBackgroundPadding = 10;
        //offset.x = fm.stringWidth(Math.floor((double)contents.length/(double)bytesPerRow)+"") + byteBackgroundPadding;


        offset.x = getContentWidth()/2-((bytesPerRow * byteOffeetX)+byteBackgroundPadding)/2;

        this.scrollController.setMaxScrollY(Math.max(0,contentHeight+byteBackgroundPadding*2+offset.y));

        AdvancedGraphics.borderedRect(
                g2,
                this.getContentX()+offset.x-byteBackgroundPadding,
                this.getContentY()+offset.y-byteBackgroundPadding,
                (bytesPerRow * byteOffeetX)+byteBackgroundPadding,
                this.getFullByteHeight()+byteBackgroundPadding*2,
                2,
                theme.getColorByName("secondary"),
                theme.getColorByName("accent"),
                AdvancedGraphics.BORDER_FULL
        );

        int currentX, currentY, realCurrentX, realCurrentY;

        byte[] fullrow = new byte[bytesPerRow];

        Position underCursor = getByteUnderCursor();

        String byteString;
        for(int i = getFirstVisibleIndex(); i < getLastVisibleIndex(); i ++){
            byteString = String.format("%02X", contents[i]);

            //color = contents[i]+128;
            //color = (color/255);

            realCurrentX = (i%bytesPerRow);
            realCurrentY = (i/bytesPerRow);

            currentX = realCurrentX * byteOffeetX;
            currentY = realCurrentY * getLineHeight();

            fullrow[i%bytesPerRow] = contents[i];

            if(realCurrentX == underCursor.x && realCurrentY == underCursor.y){
                g2.setColor(theme.getColorByName("selection"));
                g2.fillRect(
                        this.getContentX()+offset.x+currentX,
                        this.getContentY()+offset.y+currentY-text_height+getLineHeight(),
                        fm.stringWidth(byteString),
                        text_height
                );
                g2.setColor(theme.getColorByName("text2"));
            }


            if(realCurrentX == bytesPerRow-1){
                g2.drawString(new String(fullrow),
                        this.getContentX()+offset.x+bytesPerRow*byteOffeetX+byteBackgroundPadding+10,
                        this.getContentY()+offset.y+currentY+getLineHeight()
                );

                String st = realCurrentY + "";

                g2.drawString(st, this.getContentX()+offset.x - fm.stringWidth(st)-byteBackgroundPadding*2, this.getContentY()+offset.y+currentY+getLineHeight());
            }

            //g2.setColor(new Color((int)(255*color), (int)(color*255*1.1)%255, (int)(color*255*1.2)%255));
            g2.setColor(theme.getColorByName("text1"));
            g2.drawString(byteString,
                    this.getContentX()+offset.x+currentX,
                    this.getContentY()+offset.y+currentY+getLineHeight()
            );
            g2.setColor(theme.getColorByName("text2"));
        }

        AffineTransform at2 = new AffineTransform();
        at2.translate(0 , 0);

        g2.setTransform(at2);
    }


    public int getFullByteHeight(){
        return (int)Math.floor(((double)contents.length/(double)bytesPerRow)*(double)getLineHeight())+getLineHeight();
    }

    public int getFirstVisibleIndex(){
        return Math.max(0,((scrollController.getScrollY() / getLineHeight()-1))*bytesPerRow);
    }

    public int getLastVisibleIndex(){
        return Math.min(contents.length, getFirstVisibleIndex() + this.getContentHeight()/getLineHeight()*bytesPerRow);
    }

    public Position getByteUnderCursor(){
        int x = (lastMousePosition.x-this.getContentX()-offset.x+ scrollController.getScrollX()) / byteOffeetX;
        int y = (lastMousePosition.y-this.getContentY()-offset.y+ scrollController.getScrollY()) / getLineHeight();

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
        return currentFile != null && contents.length != 0;
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
            currentFile = filename;
            onCurrentFileChanged();
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

    @Override
    public boolean dontAutoScroll() {
        return false;
    }

    @Override
    public void reload() {
        if(hasFile()){
            openFile(getCurrentFile());
        }
    }
}
