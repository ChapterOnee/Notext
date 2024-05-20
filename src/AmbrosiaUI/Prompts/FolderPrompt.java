package AmbrosiaUI.Prompts;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.*;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;

import java.awt.event.MouseEvent;
import java.nio.file.Paths;

/**
 * Prompt for getting folders
 */
public class FolderPrompt extends FilePrompt{
    protected Label pathDisplayLabelSecondary;
    protected Frame bottomFrame;

    public FolderPrompt(Theme theme) {
        super(theme);

        corePlacement.setRowTemplateFromString("auto 40px");

        bottomFrame = new Frame("primary", 1);
        corePlacement.add(bottomFrame, 1,0,1,1);

        GridPlacement bottomFramePlacement = new GridPlacement(theme);
        bottomFramePlacement.setRowTemplateFromString("auto");
        bottomFramePlacement.setColumnTemplateFromString("auto 100px");

        bottomFrame.setChildrenPlacement(bottomFramePlacement);

        pathDisplayLabelSecondary = new Label("", "normal", 0,1,4) {
            @Override
            public String getText() {
                return folderView.getPath();
            }
        };
        pathDisplayLabelSecondary.setHoverEffectDisabled(true);
        pathDisplayLabelSecondary.setTextPlacement(AdvancedGraphics.Side.RIGTH);


        Frame bottomPathFrame = new Frame("primary",0);
        bottomFramePlacement.add(bottomPathFrame, 0, 0,1,1);

        HorizontalPlacement bottomPathPlacement = new HorizontalPlacement(theme);
        bottomPathFrame.setChildrenPlacement(bottomPathPlacement);


        Button submitButton = new Button("Open", "normal", 0, 1, 4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                result = new PromptResult(folderView.getPath());
                win.close();
            }
        };

        bottomPathPlacement.add(pathDisplayLabelSecondary,new UnitValue(0, UnitValue.Unit.AUTO));
        bottomFramePlacement.add(submitButton, 0, 1,1,1);

        win.update();
    }
}
