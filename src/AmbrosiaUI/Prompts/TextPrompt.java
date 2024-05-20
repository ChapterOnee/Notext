package AmbrosiaUI.Prompts;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Widgets.*;
import AmbrosiaUI.Widgets.Placements.GridPlacement;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Prompt for text
 */
public class TextPrompt extends Prompt{
    protected GridPlacement corePlacement;

    public TextPrompt(Theme theme, String prompt) {
        super(theme, 400, 200);

        corePlacement = new GridPlacement(win.getTheme());
        corePlacement.setColumnTemplateFromString("100px auto 100px");
        corePlacement.setRowTemplateFromString("40px auto 40px auto 40px");

        win.getCoreFrame().setChildrenPlacement(corePlacement);
        win.getCoreFrame().setMargin(0);

        Label promptLabel = new Label(prompt, "normal",0,0,4);
        promptLabel.setTextPlacement(AdvancedGraphics.Side.LEFT);
        promptLabel.setBackgroundColor("primary");
        promptLabel.setForegroundColor("text2");
        promptLabel.setHoverEffectDisabled(true);

        Input mainInput = new Input("normal",0,5,4){
            @Override
            public void onKeyPressed(KeyEvent keyEvent) {
                super.onKeyPressed(keyEvent);

                if(keyEvent.getKeyCode() == 10){
                    result = new PromptResult(this.getText());
                    win.close();
                }
            }
        };
        mainInput.setTextPlacement(AdvancedGraphics.Side.LEFT);

        Button confirm = new Button("Confirm","normal",0,5,4){
            @Override
            public void onMousePressed(MouseEvent e) {
                result = new PromptResult(mainInput.getText());
                win.close();
            }
        };

        Button cancel = new Button("Cancel", "normal",0,5,4){
            @Override
            public void onMousePressed(MouseEvent e) {
                win.close();
            }
        };


        corePlacement.add(promptLabel,0,0,1,3);
        corePlacement.add(mainInput,2,0,1,3);
        corePlacement.add(confirm,4,0,1,1);
        corePlacement.add(cancel,4,2,1,1);

        win.setElement_in_focus(mainInput);

        win.update();
    }
}
