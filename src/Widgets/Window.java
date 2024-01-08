package Widgets;

import Utility.*;
import Utility.Rectangle;
import Widgets.Placements.HorizontalPlacement;
import Widgets.Placements.VerticalPlacement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Window {
    protected final Frame core_frame;
    protected final Frame core_header;

    private enum ResizingDirection {
        UP,
        UP_RIGTH,
        UP_LEFT,
        RIGTH,
        DOWN,
        DOWN_RIGHT,
        DOWN_LEFT,
        LEFT,
        NONE
    }

    protected Widget element_in_focus;

    private final Frame innerFrame;

    private final Frame innerHeader;

    private final Frame innerHeaderControlls;

    protected JFrame frame;

    protected JPanel panel;

    protected Theme theme;

    private final boolean DEBUG = false;

    private final EventStatus eventStatus = new EventStatus();
    public Window() {
        theme = new Theme();
        theme.loadFromFile("themes/default.thm");


        innerFrame = new Frame("primary"){
            @Override
            public Position getPosition() {
                return new Position(0,0);
            }

            @Override
            public int getWidth() {
                return panel.getPreferredSize().width;
            }

            @Override
            public int getHeight() {
                return panel.getPreferredSize().height;
            }
        };;
        innerFrame.setTheme(theme);

        VerticalPlacement hiddenCorePlacement = new VerticalPlacement(theme);
        innerFrame.setChildrenPlacement(hiddenCorePlacement);

        innerHeader = new Frame("secondary");
        HorizontalPlacement innerHeaderPlacement = new HorizontalPlacement(theme);
        innerHeader.setChildrenPlacement(innerHeaderPlacement);

        core_frame = new Frame("primary");
        element_in_focus = core_frame;

        hiddenCorePlacement.add(innerHeader, new UnitValue(30, UnitValue.Unit.PIXELS));
        hiddenCorePlacement.add(core_frame, new UnitValue(0, UnitValue.Unit.AUTO));


        core_header = new Frame("secondary");
        innerHeaderControlls = new Frame("accent");
        innerHeaderPlacement.add(core_header, new UnitValue(0, UnitValue.Unit.AUTO));
        innerHeaderPlacement.add(innerHeaderControlls, new UnitValue(100, UnitValue.Unit.PIXELS));

        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                innerFrame.draw(g2);

                if(!DEBUG) {
                    return;
                }
                // Show what is in focus
                g2.setClip(null);
                g2.setColor(new Color(255,0,0));
                g2.drawRect(element_in_focus.getX(), element_in_focus.getY(),element_in_focus.getWidth()-1,element_in_focus.getHeight()-1);
            }
        };

        panel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                innerFrame.getChildrenPlacement().resize(Size.fromDimension(e.getComponent().getSize()));
            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {

            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {

            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {

            }
        });

        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                eventStatus.getMousePosition().x = e.getX();
                eventStatus.getMousePosition().y = e.getY();
                update();

                if(element_in_focus == null){
                    return;
                }

                element_in_focus.onMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                eventStatus.getMousePosition().x = e.getX();
                eventStatus.getMousePosition().y = e.getY();
                update();
                if(element_in_focus == null){
                    return;
                }

                element_in_focus.onMouseMoved(e);
            }
        });

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if(element_in_focus == null){
                    return;
                }
                element_in_focus.onMouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                update();
                element_in_focus = innerFrame.getChildUnderMouse();

                eventStatus.setMouseDown(true);
                //editor.startSelection();
                update();

                if(element_in_focus == null){
                    return;
                }
                element_in_focus.onMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                eventStatus.setMouseDown(false);
                update();

                if(element_in_focus == null){
                    return;
                }
                element_in_focus.onMouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(element_in_focus == null){
                    return;
                }
                element_in_focus.onKeyPressed(e);
                update();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(element_in_focus == null){
                    return;
                }
                element_in_focus.onKeyReleased(e);
                update();
            }
        });
    }

    public void open(){
        frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        //frame.setUndecorated(true);

        enableCustomFrame();

        GridBagConstraints gb = new GridBagConstraints();
        gb.fill = GridBagConstraints.BOTH;
        gb.weightx = 1;
        gb.weighty = 1;

        frame.add(panel,gb);
        panel.setFocusable(true);

        frame.setLocationRelativeTo(null);
        frame.setSize(800,500);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        onFrameLoad();
    }

    private boolean grabbed = false;
    private Position grabOffset = new Position(0,0);

    private ResizingDirection grabbedResize = ResizingDirection.NONE;

    private void enableCustomFrame(){
        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                Position mousePos = new Position(mouseEvent.getX(),mouseEvent.getY());

                if(onHeader(mousePos)){
                    grabbed = true;
                }

                grabOffset = new Position(mouseEvent.getX(),mouseEvent.getY());
                grabbedResize = getResizing(mousePos);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                grabbed = false;
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                Position mousePos = new Position(mouseEvent.getX(),mouseEvent.getY());
                ResizingDirection direction = grabbedResize;
                Point location = frame.getLocation();

                if(grabbed){
                   // System.out.println((mouseEvent.getX()) + "x" + (mouseEvent.getY()));

                    frame.setLocation((int) (mouseEvent.getX()+location.getX()- grabOffset.x), (int) (mouseEvent.getY()+location.getY()-grabOffset.y));
                }
                else if(direction != ResizingDirection.NONE){
                    int oldX = (int)location.getX();
                    int oldY = (int)location.getY();

                    int newX = oldX + mouseEvent.getX() - grabOffset.x;
                    int newY = oldY + mouseEvent.getY() - grabOffset.y;

                    int diffX = oldX - newX;
                    int diffY = oldY - newY;

                    //System.out.println(diffX + " " + diffY + " " + grabOffset);

                    int x = oldX;
                    int y = oldY;
                    int width = frame.getWidth();
                    int height = frame.getHeight();

                    switch (direction){
                        case UP -> {
                            y = newY;
                            height += diffY;
                        }
                        case LEFT -> {
                            x = newX;
                            width += diffX;
                        }
                        case DOWN -> {
                            height -= diffY;

                            grabOffset.y = mouseEvent.getY();
                        }
                        case RIGTH -> {
                            width -= diffX;

                            grabOffset.x = mouseEvent.getX();
                        }
                        case UP_LEFT -> {
                            y = newY;
                            x = newX;
                            width += diffX;
                            height += diffY;
                        }
                        case UP_RIGTH -> {
                            y = newY;
                            width -= diffX;
                            height += diffY;

                            grabOffset.x = mouseEvent.getX();
                        }
                        case DOWN_LEFT -> {
                            x = newX;
                            width += diffX;
                            height -= diffY;

                            grabOffset.y = mouseEvent.getY();
                        }
                        case DOWN_RIGHT -> {
                            width -= diffX;
                            height -= diffY;

                            grabOffset.x = mouseEvent.getX();
                            grabOffset.y = mouseEvent.getY();
                        }
                    }

                    frame.setLocation(x, y);
                    frame.setSize(width, height);


                    //frame.setLocation((int) location.getX(), (int) (mouseEvent.getY()+location.getY()-grabOffset.y));
                    //frame.setSize(frame.getWidth(), frame.getHeight()+diffY);
                }
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                //Rectangle inner = new Rectangle(grabWidth,grabWidth, panel.getWidth()-grabWidth*2, panel.getHeight()-grabWidth*2);

                //if(mousePos.inRectangle(header)){
                //    panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                //}
                //else{
                //    panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                //}
                Position mousePos = new Position(mouseEvent.getX(),mouseEvent.getY());
                ResizingDirection direction = getResizing(mousePos);

                Cursor nwcursor;
                switch (direction){
                    case UP -> nwcursor = new Cursor(Cursor.N_RESIZE_CURSOR);
                    case UP_LEFT -> nwcursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
                    case UP_RIGTH -> nwcursor = new Cursor(Cursor.NE_RESIZE_CURSOR);
                    case RIGTH -> nwcursor = new Cursor(Cursor.E_RESIZE_CURSOR);
                    case DOWN -> nwcursor = new Cursor(Cursor.S_RESIZE_CURSOR);
                    case DOWN_LEFT -> nwcursor = new Cursor(Cursor.SW_RESIZE_CURSOR);
                    case DOWN_RIGHT -> nwcursor = new Cursor(Cursor.SE_RESIZE_CURSOR);
                    case LEFT -> nwcursor = new Cursor(Cursor.W_RESIZE_CURSOR);
                    default -> nwcursor = new Cursor(Cursor.DEFAULT_CURSOR);
                }

                panel.setCursor(nwcursor);
            }
        });
        frame.setUndecorated(true);
    }

    public boolean onHeader(Position pos){
        Rectangle header = new Rectangle(0,5, panel.getWidth(),20);
        return pos.inRectangle(header);
    }
    public ResizingDirection getResizing(Position pos){
        int grabSize = 5;

        Rectangle top = new Rectangle(0,0,panel.getWidth(),grabSize);
        Rectangle bottom = new Rectangle(0,panel.getHeight()-grabSize,panel.getWidth(),grabSize);
        Rectangle left = new Rectangle(0,0,grabSize,panel.getHeight());
        Rectangle right = new Rectangle(panel.getWidth()-grabSize,0,grabSize,panel.getHeight());

        ResizingDirection direction = ResizingDirection.NONE;
        if(pos.inRectangle(top)){
            direction = ResizingDirection.UP;
        }
        else if(pos.inRectangle(bottom)){
            direction = ResizingDirection.DOWN;
        }

        if(pos.inRectangle(left)){
            if(direction == ResizingDirection.UP){
                direction = ResizingDirection.UP_LEFT;
            }
            else if(direction == ResizingDirection.DOWN){
                direction = ResizingDirection.DOWN_LEFT;
            }
            else {
                direction = ResizingDirection.LEFT;
            }
        }
        else if(pos.inRectangle(right)){
            if(direction == ResizingDirection.UP){
                direction = ResizingDirection.UP_RIGTH;
            }
            else if(direction == ResizingDirection.DOWN){
                direction = ResizingDirection.DOWN_RIGHT;
            }
            else {
                direction = ResizingDirection.RIGTH;
            }
        }

        return direction;
    }

    public void onFrameLoad(){
        innerFrame.getChildrenPlacement().resize(Size.fromDimension(panel.getSize()));
    }

    public void update(){
        innerFrame.update(eventStatus);
        panel.repaint();
    }
}
