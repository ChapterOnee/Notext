package Widgets;

import Utility.*;
import Utility.Rectangle;
import Widgets.Placements.HorizontalPlacement;
import Widgets.Placements.VerticalPlacement;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class Core {
    protected final Frame core_frame;
    protected final Frame core_header;

    public enum RESIZING_DIRECTIONS{
        UP,
        UP_RIGTH,
        UP_LEFT,
        RIGTH,
        DOWN,
        DOWN_RIGHT,
        DOWN_LEFT,
        LEFT
    }

    protected Widget element_in_focus;

    private final Frame hiddenCoreFrame;

    protected JFrame frame;

    protected JPanel panel;

    protected Theme theme;

    private final boolean DEBUG = false;

    private final EventStatus eventStatus = new EventStatus();
    public Core() {
        theme = new Theme();
        theme.loadFromFile("themes/default.thm");


        hiddenCoreFrame = new Frame("primary"){
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
        hiddenCoreFrame.setTheme(theme);

        VerticalPlacement hiddenCorePlacement = new VerticalPlacement(theme);
        hiddenCoreFrame.setChildrenPlacement(hiddenCorePlacement);

        core_header = new Frame("secondary");
        core_frame = new Frame("primary");
        element_in_focus = core_frame;

        hiddenCorePlacement.add(core_header, new UnitValue(20, UnitValue.Unit.PIXELS));
        hiddenCorePlacement.add(core_frame, new UnitValue(0, UnitValue.Unit.AUTO));

        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                hiddenCoreFrame.draw(g2);

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
                hiddenCoreFrame.getChildrenPlacement().resize(Size.fromDimension(e.getComponent().getSize()));
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
                element_in_focus = hiddenCoreFrame.getChildUnderMouse();

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
                    grabOffset = new Position(mouseEvent.getX(),mouseEvent.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                Position mousePos = new Position(mouseEvent.getX(),mouseEvent.getY());

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
                if(grabbed){
                   // System.out.println((mouseEvent.getX()) + "x" + (mouseEvent.getY()));

                    Point location = frame.getLocation();
                    frame.setLocation((int) (mouseEvent.getX()+location.getX()- grabOffset.x), (int) (mouseEvent.getY()+location.getY()-grabOffset.y));
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
            }
        });
        frame.setUndecorated(true);
    }

    public boolean onHeader(Position pos){
        Rectangle header = new Rectangle(0,0, panel.getWidth(),20);
        return pos.inRectangle(header);
    }

    public void onFrameLoad(){
        hiddenCoreFrame.getChildrenPlacement().resize(Size.fromDimension(panel.getSize()));
    }

    public void update(){
        hiddenCoreFrame.update(eventStatus);
        panel.repaint();
    }
}
