package AmbrosiaUI.Prompts;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.*;
import AmbrosiaUI.Widgets.Icons.Icon;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;
import AmbrosiaUI.Widgets.Placements.VerticalPlacement;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class FilePrompt extends Prompt{
    protected GridPlacement corePlacement;

    protected FolderView folderView;
    public FilePrompt(Theme theme) {
        super(theme);
        initializeWindow();
    }

    protected void initializeWindow(){
        corePlacement = new GridPlacement(win.getTheme());
        corePlacement.setColumnTemplateFromString("auto");
        corePlacement.setRowTemplateFromString("auto");

        win.getCoreFrame().setChildrenPlacement(corePlacement);

        folderView = new FolderView(win.getCoreFrame().getTheme(), 40) {
            @Override
            protected void fileSelected(String file) {
                FilePrompt.this.fileSelected(file);
            }

            @Override
            public void setPath(String path) {
                super.setPath(path);

                win.update();
            }
        };
        corePlacement.add(folderView, 0,0,1,1);
        folderView.initialize();


        win.getPanel().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                folderView.getScrollController().setScrollY(Math.max(0, folderView.getScrollController().getScrollY() + e.getWheelRotation()*25));
                win.update();
                win.update(); // Idk why this works but it fixes a visual bug :>
            }
        });

        win.update();
    }

    public void fileSelected(String file){
        FilePrompt.this.result = new PromptResult(file);
        win.close();
    }

    public void setPath(String path){
        folderView.setPath(path);
    }

    public void addAllowed(String regex){
        folderView.addAllowed(regex);
    }
}
