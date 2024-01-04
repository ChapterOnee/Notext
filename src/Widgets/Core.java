package Widgets;

import Utility.EventStatus;
import Utility.Position;
import Utility.Size;
import Widgets.Placements.VerticalPlacement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Core {
    protected final Frame core_frame;
    protected JFrame frame;

    protected JPanel panel;

    protected Theme theme;

    private final EventStatus eventStatus = new EventStatus();
    public Core() {
        theme = new Theme();
        theme.loadFromFile("themes/default.thm");

        core_frame = new Frame("primary") {
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
        };
        core_frame.setTheme(theme);

        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                Core.this.core_frame.draw(g2);
            }
        };

        panel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                core_frame.getChildrenPlacement().resize(Size.fromDimension(e.getComponent().getSize()));
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
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                eventStatus.getMousePosition().x = e.getX();
                eventStatus.getMousePosition().y = e.getY();
                update();
            }
        });

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                eventStatus.setMouseDown(true);
                //editor.startSelection();
                update();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                eventStatus.setMouseDown(false);
                update();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        panel.setFocusable(true);
    }

    public void open(){
        frame = new JFrame();
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gb = new GridBagConstraints();
        gb.fill = GridBagConstraints.BOTH;
        gb.weightx = 1;
        gb.weighty = 1;

        frame.add(panel,gb);

        frame.setSize(800,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        onFrameLoad();
    }

    public void onFrameLoad(){

    }

    public void update(){
        core_frame.update(eventStatus);
        panel.repaint();
    }
}
