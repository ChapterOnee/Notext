package AmbrosiaUI.Widgets.DropdownMenu;

import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Widget;

/**
 * An item for a dropdown menu, can be only a spacer
 */
public class DropdownMenuItem {
    private Widget boundWidget;

    private boolean isSpacer = false;

    public DropdownMenuItem() {
        isSpacer = true;
    }

    public DropdownMenuItem(Widget boundWidget) {
        this.boundWidget = boundWidget;
    }

    public Widget getBoundWidget() {
        return boundWidget;
    }

    public void setBoundWidget(Widget boundWidget) {
        this.boundWidget = boundWidget;
    }

    public boolean isSpacer() {
        return isSpacer;
    }
}
