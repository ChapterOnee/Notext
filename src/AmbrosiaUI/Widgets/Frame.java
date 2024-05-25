package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.GraphicsBorderModifier;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Widgets.Placements.ScrollController;

import java.awt.*;

/**
 * A frame widget
 */
public class Frame extends Widget{
    protected String backgroundColor;

    protected String onHoverBackgroundColor;

    protected GraphicsBorderModifier borderModifier = new GraphicsBorderModifier(true, true ,true ,true);

    protected int borderWidth = 0;

    protected boolean disableHoverEffect = false;

    protected String borderColor = "accent";

    protected ScrollController scrollController = new ScrollController(0,0);

    public Frame(String backgroundColor, int margin) {
        this.backgroundColor = backgroundColor;
        this.onHoverBackgroundColor = backgroundColor;
        this.margin = margin;
    }

    @Override
    public void drawSelf(Graphics2D g2) {
        setupDraw(g2);
        g2.setColor(theme.getColorByName(backgroundColor));

        //if(mouseOver){
        //    g2.setColor(new Color(255,0,0));
        //}
        Rectangle bounding_rect = this.getBoundingRect();

        String bg = backgroundColor;
        if(mouseOver && !disableHoverEffect){
            bg = onHoverBackgroundColor;
        }
        AdvancedGraphics.borderedRect(g2, bounding_rect, this.borderWidth,
                theme.getColorByName(bg),
                theme.getColorByName(borderColor),
                borderModifier
        );

        super.drawSelf(g2);
    }

    @Override
    public Position getContentPosition() {
        return super.getContentPosition().getOffset(
                borderWidth * (borderModifier.isLeft() ? 1 : 0) - scrollController.getScrollX(),
                borderWidth * (borderModifier.isTop() ? 1 : 0) - scrollController.getScrollY()
        );
    }

    @Override
    public int getContentWidth() {
        return super.getContentWidth() - ((borderModifier.isLeft() ? 1 : 0) + (borderModifier.isRight() ? 1 : 0)) * borderWidth;
    }

    @Override
    public int getContentHeight() {
        return super.getContentHeight() - ((borderModifier.isTop() ? 1 : 0) + (borderModifier.isBottom() ? 1 : 0)) * borderWidth;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getOnHoverBackgroundColor() {
        return onHoverBackgroundColor;
    }

    public void setOnHoverBackgroundColor(String onHoverBackgroundColor) {
        this.onHoverBackgroundColor = onHoverBackgroundColor;
    }

    public GraphicsBorderModifier getBorderModifier() {
        return borderModifier;
    }

    public void setBorderModifier(GraphicsBorderModifier borderModifier) {
        this.borderModifier = borderModifier;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public boolean isHoverEffectDisabled() {
        return disableHoverEffect;
    }

    public void setHoverEffectDisabled(boolean disableHoverEffect) {
        this.disableHoverEffect = disableHoverEffect;
    }

    public ScrollController getScrollController() {
        return scrollController;
    }


}
