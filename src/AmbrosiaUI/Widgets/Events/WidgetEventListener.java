package AmbrosiaUI.Widgets.Events;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * All the events a widget can handle
 */
public interface WidgetEventListener {
    void onMouseDragged(MouseEvent e);
    void onMouseMoved(MouseEvent e);
    void onMouseClicked(MouseEvent e);
    void onMousePressed(MouseEvent e);
    void onMouseReleased(MouseEvent e);
    void onKeyPressed(KeyEvent keyEvent);
    void onKeyReleased(KeyEvent keyEvent);
    void onPasted(String pastedData);
    void onMouseWheel(MouseWheelEvent event);
}
