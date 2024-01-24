package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.*;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Icons.Icon;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;
import AmbrosiaUI.Widgets.Placements.VerticalPlacement;
import App.Root;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;

public class Window {
    protected Frame coreFrame;
    protected Frame coreHeader;

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

    private Frame innerFrame;

    protected JFrame frame;

    protected JPanel panel;

    protected Theme theme;

    private final boolean DEBUG = false;
    private boolean customFrame = false;

    private static final PathImage maximizeImage = new PathImage("icons/window/maximize.pimg");
    private static final PathImage closeImage = new PathImage("icons/window/close.pimg");
    private static final PathImage minimizeImage = new PathImage("icons/window/minimize.pimg");

    private HorizontalPlacement innerHeaderControlsPlacement;
    private final EventStatus eventStatus = new EventStatus();
    public Window() {
        theme = new Theme();
        theme.loadFromFile("themes/Light.thm");
        initialize();
    }

    public Window(Theme theme) {
        this.theme = theme;
        initialize();
    }

    public void hide(){
        frame.setVisible(false);
    }

    public void show(){
        if(frame == null){
            this.open();
        }
        frame.setVisible(true);
    }

    private void initialize(){
        if(System.getProperty("os.name").startsWith("Windows")){
            customFrame = true;
        };

        innerFrame = new Frame("primary", 0){
            @Override
            public Position getPosition() {
                return new Position(0,0);
            }

            @Override
            public int getWidth() {
                return panel.getSize().width;
            }

            @Override
            public int getHeight() {
                return panel.getSize().height;
            }
        };
        innerFrame.setTheme(theme);
        innerFrame.setWindow(this);

        VerticalPlacement hiddenCorePlacement = new VerticalPlacement(theme) {
            @Override
            public Size getRootSize() {
                return new Size(panel.getSize().width, panel.getSize().height);
            }
        };
        innerFrame.setChildrenPlacement(hiddenCorePlacement);

        Frame innerHeader = new Frame("secondary", 0);
        hiddenCorePlacement.add(innerHeader, new UnitValue(30, UnitValue.Unit.PIXELS));

        HorizontalPlacement innerHeaderPlacement = new HorizontalPlacement(theme);
        innerHeader.setChildrenPlacement(innerHeaderPlacement);

        coreFrame = new Frame("primary", 0);
        hiddenCorePlacement.add(coreFrame, new UnitValue(0, UnitValue.Unit.AUTO));

        coreFrame.setBorderWidth(2);
        coreFrame.setBorderColor("secondary");
        coreFrame.setBorderModifier(new GraphicsBorderModifier(false,true,true,true));
        element_in_focus = coreFrame;

        coreHeader = new Frame("secondary", 0);
        innerHeaderPlacement.add(coreHeader, new UnitValue(0, UnitValue.Unit.AUTO));

        Frame innerHeaderControlls = new Frame("secondary", 0);
        innerHeaderPlacement.add(innerHeaderControlls, new UnitValue(150, UnitValue.Unit.PIXELS));

        innerHeaderControlsPlacement = new HorizontalPlacement(theme);
        innerHeaderControlls.setChildrenPlacement(innerHeaderControlsPlacement);

        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                //g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
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

        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                eventStatus.getMousePosition().x = e.getX();
                eventStatus.getMousePosition().y = e.getY();
                update();

                if(element_in_focus == null){
                    return;
                }
                if(element_in_focus.isDisabled()){
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
                if(element_in_focus.isDisabled()){
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
                if(element_in_focus.isDisabled()){
                    return;
                }
                element_in_focus.onMouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                update();
                element_in_focus = innerFrame.getChildUnderMouse(eventStatus);

                eventStatus.setMouseDown(true);
                //editor.startSelection();
                update();

                if(element_in_focus == null){
                    return;
                }
                if(element_in_focus.isDisabled()){
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
                if(element_in_focus.isDisabled()){
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
                if(element_in_focus.isDisabled()){
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
                if(element_in_focus.isDisabled()){
                    return;
                }
                element_in_focus.onKeyReleased(e);
                update();
            }
        });

        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(element_in_focus == null){
                    return;
                }
                if(element_in_focus.isDisabled()){
                    return;
                }

                element_in_focus.onMouseWheel(e);
            }
        });

        Keybind copy = new Keybind("Copy", panel, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK)){
            @Override
            public void activated(ActionEvent e) {
                if(element_in_focus != null){
                    StringSelection sel = new StringSelection(element_in_focus.getSelectedContent());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(sel, sel);
                }
            }
        };

        Keybind paste = new Keybind("Paste", panel, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK)){
            @Override
            public void activated(ActionEvent e) {
                if(element_in_focus != null){
                    String data = null;
                    try {
                        data = (String) Toolkit.getDefaultToolkit()
                                .getSystemClipboard().getData(DataFlavor.stringFlavor);
                    } catch (UnsupportedFlavorException | IOException ex) {
                        System.out.println(ex);
                    }

                    element_in_focus.onPasted(data);
                }
            }
        };
    }

    private void open(){
        if(frame != null){
            return;
        }

        frame = new JFrame();
        frame.setLayout(new GridBagLayout());

        frame.addComponentListener(new ComponentListener() {
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

        //frame.setUndecorated(true);

        if(customFrame) {
            enableCustomFrame();
        }

        GridBagConstraints gb = new GridBagConstraints();
        gb.fill = GridBagConstraints.BOTH;
        gb.weightx = 1;
        gb.weighty = 1;

        frame.add(panel,gb);
        panel.setFocusable(true);

        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        Icon maximize = new Icon("secondary", "accent", maximizeImage) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);

                if(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH){
                    frame.setExtendedState(java.awt.Frame.NORMAL);
                }
                else {
                    frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                }
                Window.this.update();
            }
        };

        Icon close = new Icon("secondary", "accent", closeImage) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);

                Window.this.close();
            }
        };

        Icon minimize = new Icon("secondary", "accent", minimizeImage) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);

                frame.setState(JFrame.ICONIFIED);
            }
        };

        innerHeaderControlsPlacement.add(minimize, new UnitValue(0, UnitValue.Unit.AUTO));
        innerHeaderControlsPlacement.add(maximize, new UnitValue(0, UnitValue.Unit.AUTO));
        innerHeaderControlsPlacement.add(close, new UnitValue(0, UnitValue.Unit.AUTO));

        frame.setUndecorated(true);
    }

    public void close(){
        destroy();
    }

    public void destroy(){
        try {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    private boolean onHeader(Position pos){
        Rectangle header = new Rectangle(0,5, panel.getWidth(),20);
        return pos.inRectangle(header);
    }
    private ResizingDirection getResizing(Position pos){
        panel.setBackground(theme.getColorByName("primary"));
        frame.setBackground(theme.getColorByName("primary"));

        int grabSize = 6;

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
        innerFrame.getChildrenPlacement().update();
        innerFrame.fullUpdate(eventStatus);
        panel.repaint();
    }

    public void setSize(int width, int height){
        frame.setSize(width, height);
        update();
    }

    public Frame getCoreFrame() {
        return coreFrame;
    }

    public Frame getCoreHeader() {
        return coreHeader;
    }

    public JFrame getFrame() {
        return frame;
    }

    public Theme getTheme() {
        return theme;
    }

    public JPanel getPanel() {
        return panel;
    }
}
