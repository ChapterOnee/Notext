package AmbrosiaUI.Widgets.TabbedFrame;

import AmbrosiaUI.Widgets.Widget;

public class TabbedFrameTab {
    private Widget boundElement;
    private String name;

    public TabbedFrameTab(Widget boundElement, String name) {
        this.boundElement = boundElement;
        this.name = name;
    }

    public Widget getBoundElement() {
        return boundElement;
    }

    public void setBoundElement(Widget boundElement) {
        this.boundElement = boundElement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
