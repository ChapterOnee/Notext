package AmbrosiaUI.Prompts;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Widgets.*;
import AmbrosiaUI.Widgets.Placements.GridPlacement;

import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateFilePrompt extends FilePrompt{

    protected Label pathDisplayLabelSecondary;
    protected Frame bottomFrame;
    private GridPlacement bottomFramePlacement;

    private Button submitButton;

    private Input filenameInput;
    public CreateFilePrompt(Theme theme) {
        super(theme);

        corePlacement.setRowTemplateFromString("40px auto 40px");

        bottomFrame = new Frame("primary", 1);
        bottomFramePlacement = new GridPlacement(theme);
        bottomFramePlacement.setRowTemplateFromString("auto");
        bottomFramePlacement.setColumnTemplateFromString("auto 100px 100px");

        bottomFrame.setChildrenPlacement(bottomFramePlacement);

        pathDisplayLabelSecondary = new Label("", "normal", 0,1,4) {
            @Override
            public String getText() {
                return path;
            }
        };
        pathDisplayLabelSecondary.setHoverEffectDisabled(true);
        pathDisplayLabelSecondary.setTextPlacement(AdvancedGraphics.Side.RIGTH);

        filenameInput = new Input("normal",0,1,4);

        submitButton = new Button("Save", "normal", 0, 1, 4){
            @Override
            public void onMouseClicked(MouseEvent e) {
                result = new PromptResult(Paths.get(path, filenameInput.getContent()).toString());
                System.out.println(result.getContent());
                win.close();
            }
        };

        bottomFramePlacement.add(pathDisplayLabelSecondary,0,0,1,1);
        bottomFramePlacement.add(filenameInput,0,1,1,1);
        bottomFramePlacement.add(submitButton, 0, 2,1,1);

        corePlacement.add(bottomFrame, 2,0,1,2);
    }

    @Override
    public void setPath(String path) {
        super.setPath(path);
    }

    @Override
    protected void fileSelected(String file) {

    }
}
