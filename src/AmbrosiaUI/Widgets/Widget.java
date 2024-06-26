package AmbrosiaUI.Widgets;

import AmbrosiaUI.ContextMenus.ContextMenu;
import AmbrosiaUI.Utility.*;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Events.WidgetEventListener;
import AmbrosiaUI.Widgets.Placements.Placement;
import AmbrosiaUI.Widgets.Placements.PlacementCell;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The default widget structure
 */
public abstract class Widget implements Comparable<Widget>{
    protected Placement placement;

    protected Widget parent;
    protected Window window;

    protected Placement childrenPlacement;

    protected Theme theme;
    protected int placementIndex;

    protected boolean mouseOver = false;

    protected Position lastMousePosition = new Position(0,0);

    protected int zIndex = 0;

    protected int margin = 0;

    protected boolean disabled = false;

    protected Widget lockedToView;

    protected final boolean DEBUG = false;

    protected Size minimalSize = new Size(0,0);

    protected ContextMenu contextMenu;

    protected static final Canvas fontsizecanvas = new Canvas();

    protected ArrayList<WidgetEventListener> boundEvents = new ArrayList<>();

    public void addEventListener(WidgetEventListener listener){
        boundEvents.add(listener);
    }

    /**
     * A method that draws the widget and its children in the correct order
     * @param g2
     */
    public void draw(Graphics2D g2) {
        ArrayList<Widget> toBeDrawn = this.getAllChildren();

        Collections.sort(toBeDrawn);

        for(Widget w: toBeDrawn){
            w.drawSelf(g2);
        }
    }

    /**
     * A method that defines how the widget itself is drawn
     * @param g2
     */
    public void drawSelf(Graphics2D g2){
        if(DEBUG){
            if(this.childrenPlacement != null){
                this.childrenPlacement.drawDebug(g2);
            }

            g2.setFont(new Font("Monospaced", Font.PLAIN,10));
            FontMetrics fm = g2.getFontMetrics(g2.getFont());

            g2.setColor(new Color(255,0,0));

            String text = " " + this.getX() + ":" + this.getY();
            int text_x = this.getX();
            int text_y = this.getY();


            g2.drawRect(this.getX(),this.getY(),this.getWidth()-1,this.getHeight()-1);

            g2.drawLine(this.getX(),this.getY(),this.getX()+this.getWidth(),this.getY()+this.getHeight());
            g2.drawLine(this.getX()+this.getWidth(),this.getY(),this.getX(),this.getY()+this.getHeight());

            g2.setColor(new Color(0,0,0));
            g2.fillRect(text_x,text_y,fm.stringWidth(text)+10, fm.getAscent()+2);

            g2.setColor(new Color(255,0,0));
            g2.drawRect(text_x,text_y,fm.stringWidth(text)+10, fm.getAscent()+2);
            g2.drawString(text, text_x,text_y+fm.getAscent());
        }
    }

    /**
     * A function the presets the graphical context to some set defaults
     * @param g2
     */
    public void setupDraw(Graphics2D g2){
        g2.setStroke(new BasicStroke(1));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(theme.getFontByName("normal"));
        if(lockedToView != null){
            g2.setClip(lockedToView.getX(),lockedToView.getY(),lockedToView.getWidth(),lockedToView.getHeight());
        }
        else {
            g2.setClip(this.getX(),this.getY(),this.getWidth(),this.getHeight());
        }

    }
    public Position getPosition(){
        return placement.getPosition(placementIndex).getOffset(margin, margin);
    }
    public int getWidth(){
        return placement.getWidth(placementIndex) - margin*2;
    }
    public int getHeight(){
        return placement.getHeight(placementIndex) - margin*2;
    }

    public int getMinWidth(){
        return  minimalSize.width;
    }
    public int getMinHeight(){
        return  minimalSize.height;
    }

    public void setMinWidth(int width){
        minimalSize.width = width;
    }
    public void setMinHeight(int height){
        minimalSize.height = height;
    }

    public int getX(){
        return this.getPosition().x;
    }
    public int getY(){
        return this.getPosition().y;
    }

    public Position getContentPosition(){
        return this.getPosition().getOffset(margin,margin);
    }
    public int getContentX(){
        return this.getContentPosition().x;
    }
    public int getContentY(){
        return this.getContentPosition().y;
    }

    public int getContentWidth(){
        return this.getWidth()-margin*2;
    }
    public int getContentHeight(){
        return this.getHeight()-margin*2;
    }

    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public int getPlacementIndex() {
        return placementIndex;
    }

    public void setPlacementIndex(int placementIndex) {
        this.placementIndex = placementIndex;
    }

    public Placement getChildrenPlacement() {
        return childrenPlacement;
    }

    /**
     * Set a placement for the children of this widget
     * @param childrenPlacement
     */
    public void setChildrenPlacement(Placement childrenPlacement) {
        if(window == null){
            Logger.printError("Place this element: " + getClass().getName() + ", before adding a children placement to it.");
            return;
        }

        this.childrenPlacement = childrenPlacement;
        this.childrenPlacement.setParent(this);
        this.childrenPlacement.setWindow(window);
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme Theme) {
        this.theme = Theme;
    }

    /**
     * Get all direct children of the widget
     * @return An arraylist of children widgets
     */
    public ArrayList<Widget> getChildren(){
        ArrayList<Widget> output = new ArrayList<>();

        if(this.childrenPlacement != null){
            for (PlacementCell cell: this.childrenPlacement.getChildren() ){
                output.add(cell.getBoundElement());
            }
        }

        return output;
    }

    /**
     * Gets all children of the widget
     * @return An arraylist of children widgets
     */
    public ArrayList<Widget> getAllChildren(){
        ArrayList<Widget> output = new ArrayList<>();

        if(this.childrenPlacement != null){
            for (Widget w: this.getChildren()){
                output.add(w);
                output.addAll(w.getAllChildren());
            }
        }

        return output;
    }

    /**
     * Update events for the widget
     * @param eventStatus
     */
    public void fullUpdate(EventStatus eventStatus){
        for(Widget w: this.getAllChildren()) {
            if(w.getMouseHoverRectangles().size() > 1){
                boolean found = false;

                for(Rectangle rect: w.getMouseHoverRectangles()){
                    if(eventStatus.getMousePosition().inRectangle(rect)){
                        found = true;
                        break;
                    }
                }

                if(!found){
                    w.mouseOver = false;
                }
            }
            else {
                w.mouseOver = false;
            }
        }
        Widget underMouse = this.getChildUnderMouse(eventStatus);

        underMouse.mouseOver = true;
        this.update(eventStatus);
    }

    public void update(EventStatus eventStatus){
        lastMousePosition = eventStatus.getMousePosition();

        for(Widget w: this.getChildren()){
            w.update(eventStatus);
        }
    }

    /**
     * Get a child of the widget that is under the mouse, if there is no child return self
     * @param eventStatus
     * @return
     */
    public Widget getChildUnderMouse(EventStatus eventStatus){
        Widget output = this;

        ArrayList<Widget> children = getAllChildren();
        ArrayList<Widget> childrenUnderCursor = new ArrayList<>();

        for(Widget child: children){
            boolean found = false;

            for(Rectangle rect: child.getMouseHoverRectangles()){
                if(eventStatus.getMousePosition().inRectangle(rect)){
                    found = true;
                    break;
                }
            }

            if(found && !child.isDisabled()) {
                childrenUnderCursor.add(child);
            }
        }

        Collections.sort(childrenUnderCursor);

        if(!childrenUnderCursor.isEmpty()){
            output = childrenUnderCursor.get(childrenUnderCursor.size()-1);
        }

        return output;
    }


    /**
     * Get a rectangle that defines the bound of this widget
     * @return
     */
    public Rectangle getBoundingRect(){
        return new Rectangle(this.getPosition(),this.getWidth(),this.getHeight());
    }

    /**
     * Get all rectangles within which when the mouse is in them its consider as if its on top of the widget
     * @return
     */
    public ArrayList<Rectangle> getMouseHoverRectangles(){
        ArrayList<Rectangle> rects = new ArrayList<>();
        rects.add(this.getBoundingRect());
        return rects;
    }
    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public Size getSize(){
        return new Size(this.getWidth(),this.getHeight());
    }

    @Override
    public int compareTo(Widget o) {
        return Integer.compare(this.zIndex, o.getzIndex());
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public Widget getParent() {
        return parent;
    }

    public void setParent(Widget parent) {
        this.parent = parent;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Widget getLockedToView() {
        return lockedToView;
    }

    public void setLockedToView(Widget lockedToView) {
        this.lockedToView = lockedToView;
    }

    public static int getStringWidth(String text, Font font){
        if(text == null){
            return 0;
        }
        return fontsizecanvas.getFontMetrics(font).stringWidth(text);
    }

    @Override
    public String toString() {
        return "Widget{" +
                "placement=" + placement +
                ", childrenPlacement=" + childrenPlacement +
                ", theme=" + theme +
                ", placementIndex=" + placementIndex +
                ", mouseOver=" + mouseOver +
                ", zIndex=" + zIndex +
                '}';
    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    public ContextMenu getContextMenu(){
        return contextMenu;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public String getSelectedContent(){
        return "";
    }

    public void onMouseDragged(MouseEvent e){
        for(WidgetEventListener event: boundEvents){
            event.onMouseDragged(e);
        }
    }

    public void onMouseMoved(MouseEvent e){
        for(WidgetEventListener event: boundEvents){
            event.onMouseMoved(e);
        }
    }

    public void onMouseClickedPre(MouseEvent e){
        if(e.getButton() == 3){
            ContextMenu menu = getContextMenu();

            if (menu == null){
                onMouseClicked(e);
                return;
            }

            menu.setPosition(new Position(e.getX(),e.getY()));

            window.showContextMenu(menu);
        }
        else{
            onMouseClicked(e);
        }
    }

    public void onMouseClicked(MouseEvent e){
        for(WidgetEventListener event: boundEvents){
            event.onMouseClicked(e);
        }
    }

    public void onMousePressed(MouseEvent e){
        for(WidgetEventListener event: boundEvents){
            event.onMousePressed(e);
        }
    }

    public void onMouseReleased(MouseEvent e){
        for(WidgetEventListener event: boundEvents){
            event.onMouseReleased(e);
        }
    }

    public void onKeyPressed(KeyEvent keyEvent){
        for(WidgetEventListener event: boundEvents){
            event.onKeyPressed(keyEvent);
        }
    }

    public void onKeyReleased(KeyEvent keyEvent){
        for(WidgetEventListener event: boundEvents){
            event.onKeyReleased(keyEvent);
        }
    }

    public void onPasted(String pastedData){
        for(WidgetEventListener event: boundEvents){
            event.onPasted(pastedData);
        }
    }

    public void onMouseWheel(MouseWheelEvent e){
        for(WidgetEventListener event: boundEvents){
            event.onMouseWheel(e);
        }
    }
}
