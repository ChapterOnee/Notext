package AmbrosiaUI.ContextMenus;

import AmbrosiaUI.Utility.Rectangle;

public class ContextMenuOption {
    private String text;
    private Rectangle boundingRectangle;

    public ContextMenuOption(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    protected void execute(){

    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public void setBoundingRectangle(Rectangle boundingRectangle) {
        this.boundingRectangle = boundingRectangle;
    }
}
