package AmbrosiaUI.Prompts;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.*;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;

import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *  Prompt for creating files
 */
public class CreateFilePrompt extends FilePrompt{

    protected Label pathDisplayLabelSecondary;
    protected Frame bottomFrame;
    private GridPlacement bottomFramePlacement;

    private Button submitButton;

    private Input filenameInput;
    public CreateFilePrompt(Theme theme) {
        super(theme);

        corePlacement.setRowTemplateFromString("auto 40px");

        bottomFrame = new Frame("primary", 1);
        corePlacement.add(bottomFrame, 1,0,1,1);

        bottomFramePlacement = new GridPlacement(theme);
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

        filenameInput = new Input("normal",0,1,4) {
            @Override
            public int getMinWidth() {
                return Math.max(100,fontsizecanvas.getFontMetrics(theme.getFontByName(getFont())).stringWidth(this.getText())+10);
            }
        };

        submitButton = new Button("Save", "normal", 0, 1, 4){
            @Override
            public void onMouseClicked(MouseEvent e) {
                result = new PromptResult(Paths.get(folderView.getPath(), filenameInput.getContent()).toString());
                win.close();
            }
        };

        bottomPathPlacement.add(pathDisplayLabelSecondary,new UnitValue(0, UnitValue.Unit.AUTO));
        bottomPathPlacement.add(filenameInput,new UnitValue(0, UnitValue.Unit.FIT));

        bottomFramePlacement.add(submitButton, 0, 1,1,1);

        win.update();
    }

    @Override
    public void fileSelected(String file) {
        filenameInput.setContent(new File(file).getName());
        win.update();
    }
}
