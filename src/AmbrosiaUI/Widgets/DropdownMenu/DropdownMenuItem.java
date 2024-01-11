package AmbrosiaUI.Widgets.DropdownMenu;

import AmbrosiaUI.Widgets.Widget;

public class DropdownMenuItem {
    private Widget boundWidget;

    public DropdownMenuItem(Widget boundWidget) {
        this.boundWidget = boundWidget;
    }

    public Widget getBoundWidget() {
        return boundWidget;
    }

    public void setBoundWidget(Widget boundWidget) {
        this.boundWidget = boundWidget;
    }
}
