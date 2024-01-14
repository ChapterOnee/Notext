package AmbrosiaUI.Prompts;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Label;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;
import AmbrosiaUI.Widgets.Placements.VerticalPlacement;
import AmbrosiaUI.Widgets.Scrollbar;
import AmbrosiaUI.Widgets.TextEditor.Highlighting.Highlighter;
import App.Root;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FilePrompt extends Prompt{
    private String path = System.getProperty("user.home");

    private Frame pathDisplayFrame;
    private HorizontalPlacement pathDisplayPlacement;
    private Frame filesDisplayFrame;

    private Scrollbar filesDisplayScrollbar;
    private VerticalPlacement filesDisplayPlacement;
    public FilePrompt() {
        initializeWindow();
    }

    public FilePrompt(String path) {
        this.path = path;
        initializeWindow();
    }

    private void initializeWindow(){
        GridPlacement corePlacement = new GridPlacement(win.getTheme());
        corePlacement.setColumnTemplateFromString("auto 20px");
        corePlacement.setRowTemplateFromString("40px auto 40px");

        win.getCoreFrame().setChildrenPlacement(corePlacement);

        pathDisplayFrame = new Frame("primary", 1);
        pathDisplayFrame.setHoverEffectDisabled(true);

        pathDisplayPlacement = new HorizontalPlacement(win.getTheme());
        pathDisplayFrame.setChildrenPlacement(pathDisplayPlacement);
        //pathDisplayFrame.setTextPlacement(AdvancedGraphics.Side.LEFT);

        filesDisplayFrame = new Frame("primary", 1);
        
        filesDisplayPlacement = new VerticalPlacement(win.getTheme());
        filesDisplayFrame.setChildrenPlacement(filesDisplayPlacement);

        win.getPanel().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                filesDisplayFrame.getScrollController().setScrollY(Math.max(0, filesDisplayFrame.getScrollController().getScrollY() + e.getWheelRotation()*25));
                win.update();
            }
        });

        filesDisplayScrollbar = new Scrollbar("primary",filesDisplayFrame.getScrollController(), UnitValue.Direction.VERTICAL){
            @Override
            public void onMouseDragged(MouseEvent e) {
                win.update();
                super.onMouseDragged(e);
            }
        };

        corePlacement.add(pathDisplayFrame, 0,0,1,2);
        corePlacement.add(filesDisplayScrollbar,1,1,1,1);
        corePlacement.add(filesDisplayFrame, 1,0,1,1);

        updateFiles();
    }

    private void updatePath(){
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> paths = new ArrayList<>();
        
        File f = new File(path);
        String nm;
        while(f != null){
            names.add(f.getName());
            paths.add(f.getAbsolutePath());

            f = f.getParentFile();
        }

        Collections.reverse(names);
        Collections.reverse(paths);

        pathDisplayPlacement.clear();
        String name;
        for (int i = 0;i < names.size();i++) {
            name = "\\" + names.get(i);
            if(name.equals("\\")){
                name = paths.get(i);
            }

            Label temp = new Label(name, "normal", 0, 1, 4) {
                @Override
                public void onMouseClicked(MouseEvent e) {
                    path = paths.get(placementIndex);
                    updateFiles();
                }
            };
            pathDisplayPlacement.add(temp, new UnitValue(win.getPanel().getFontMetrics(win.getTheme().getFontByName("normal")).stringWidth(name)+10, UnitValue.Unit.PIXELS));
        }
    }

    private void updateFiles(){
        updatePath();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        filesDisplayPlacement.clear();
        if(listOfFiles == null){
            return;
        }

        for (File file : listOfFiles) {
            if (!file.isHidden() && !file.getName().startsWith(".")) {
                String name = file.getName();

                Label temp = new Label(name, "normal",0,1,4);

                if(file.isDirectory()){
                    name = ">" + name;
                    temp = new Label(name, "normal",0,1,4) {
                        @Override
                        public void onMouseClicked(MouseEvent e) {
                            path = file.getAbsolutePath();
                            updateFiles();
                        }
                    };
                }

                temp.setLockedToParrentView(true);
                temp.setTextPlacement(AdvancedGraphics.Side.LEFT);

                filesDisplayPlacement.add(temp, new UnitValue(40, UnitValue.Unit.PIXELS));
            }
        }
        filesDisplayFrame.getScrollController().setScrollY(0);
        filesDisplayFrame.getScrollController().setMaxScrollY(filesDisplayPlacement.getMinimalHeight());
        win.update();
    }
}