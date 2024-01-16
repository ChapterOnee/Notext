package AmbrosiaUI.Prompts;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Icons.Icon;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Icons.PathOperations.PathLine;
import AmbrosiaUI.Widgets.Icons.PathOperations.PathMove;
import AmbrosiaUI.Widgets.Icons.PathOperations.PathRectangle;
import AmbrosiaUI.Widgets.Label;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;
import AmbrosiaUI.Widgets.Placements.VerticalPlacement;
import AmbrosiaUI.Widgets.Scrollbar;
import AmbrosiaUI.Widgets.TextEditor.Highlighting.Highlighter;
import AmbrosiaUI.Widgets.Theme;
import App.Root;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class FilePrompt extends Prompt{
    private String path = System.getProperty("user.home");

    private final ArrayList<String> allowedRegex = new ArrayList<>();

    private Frame pathDisplayFrame;
    private HorizontalPlacement pathDisplayPlacement;
    private Frame filesDisplayFrame;

    private Scrollbar filesDisplayScrollbar;
    private VerticalPlacement filesDisplayPlacement;
    public FilePrompt(Theme theme) {
        super(theme);
        initializeWindow();
    }


    private void initializeWindow(){
        GridPlacement corePlacement = new GridPlacement(win.getTheme());
        corePlacement.setColumnTemplateFromString("auto 20px");
        corePlacement.setRowTemplateFromString("40px auto");

        win.getCoreFrame().setChildrenPlacement(corePlacement);

        pathDisplayFrame = new Frame("primary", 1);
        pathDisplayFrame.setHoverEffectDisabled(true);

        pathDisplayPlacement = new HorizontalPlacement(win.getTheme());
        pathDisplayFrame.setChildrenPlacement(pathDisplayPlacement);
        //pathDisplayFrame.setTextPlacement(AdvancedGraphics.Side.LEFT);

        filesDisplayFrame = new Frame("primary", 1);
        
        filesDisplayPlacement = new VerticalPlacement(win.getTheme()){
            @Override
            public void onResize() {
                System.out.println(filesDisplayFrame.getScrollController().getMaxScrollY() + " " + filesDisplayPlacement.getMinimalHeight() + " " + filesDisplayFrame.getContentHeight());
                filesDisplayFrame.getScrollController().setMaxScrollY(Math.max(filesDisplayPlacement.getMinimalHeight()-filesDisplayFrame.getContentHeight(),0));
            }
        };
        filesDisplayPlacement.setMinColumnWidth(300);
        filesDisplayFrame.setChildrenPlacement(filesDisplayPlacement);

        win.getPanel().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                filesDisplayFrame.getScrollController().setScrollY(Math.max(0, filesDisplayFrame.getScrollController().getScrollY() + e.getWheelRotation()*25));
                win.update();
                win.update(); // Idk why this works but it fixes a visual bug :>
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
        File[] listOfFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if(new File(Paths.get(dir.getAbsolutePath(),name).toString()).isDirectory()){
                    return true;
                }

                for(String s: allowedRegex){
                    if(name.toLowerCase().matches(s)){
                        return true;
                    }
                }

                return false;
            }
        });

        filesDisplayPlacement.clear();
        if(listOfFiles == null){
            return;
        }

        for (File file : listOfFiles) {
            if (!file.isHidden() && !file.getName().startsWith(".")) {
                String name = file.getName();

                Frame temp = new Frame("primary",1);

                temp.setLockedToView(filesDisplayFrame);

                HorizontalPlacement tempPlacement = new HorizontalPlacement(win.getTheme());
                temp.setChildrenPlacement(tempPlacement);

                Icon icon = new Icon("primary", "primary", null);

                Label tempLabel;

                if(file.isDirectory()){
                    tempLabel = new Label(name, "normal",0,0,4){
                        @Override
                        public void onMouseClicked(MouseEvent e) {
                            path = file.getAbsolutePath();
                            updateFiles();
                        }
                    };
                    PathImage dirImage = new PathImage(new Size(40,40));

                    // HAHAHAHAHAHA

                    dirImage.add(new PathMove(5,10));
                    dirImage.add(new PathLine(5,0,"text2",1));
                    dirImage.add(new PathLine(5,5,"text2",1));
                    dirImage.add(new PathLine(10,0,"text2",1));
                    dirImage.add(new PathLine(0,5,"text2",1));
                    dirImage.add(new PathLine(5,0,"text2",1));
                    dirImage.add(new PathLine(-5,10,"text2",1));
                    dirImage.add(new PathLine(-20,0,"text2",1));
                    dirImage.add(new PathLine(0,-20,"text2",1));
                    dirImage.add(new PathMove(0,20));
                    dirImage.add(new PathLine(5,-10,"text2",1));
                    dirImage.add(new PathLine(15,0,"text2",1));


                    icon.setImage(dirImage);
                }
                else{
                    PathImage fileImage = new PathImage(new Size(40,40));
                    tempLabel = new Label(name, "normal",0,0,4){
                        @Override
                        public void onMouseClicked(MouseEvent e) {
                            FilePrompt.this.result = new PromptResult(file.getAbsolutePath());
                            win.close();
                        }
                    };

                    fileImage.add(new PathMove(7,7));
                    fileImage.add(new PathLine(10,0,"text2",1));
                    fileImage.add(new PathLine(5,5,"text2",1));
                    fileImage.add(new PathLine(0,20,"text2",1));
                    fileImage.add(new PathLine(-15,0,"text2",1));
                    fileImage.add(new PathLine(0,-25,"text2",1));

                    fileImage.add(new PathMove(5,10));
                    fileImage.add(new PathLine(5,0,"text2",1));
                    fileImage.add(new PathMove(0,3));
                    fileImage.add(new PathLine(-5,0,"text2",1));
                    fileImage.add(new PathMove(0,3));
                    fileImage.add(new PathLine(5,0,"text2",1));

                    icon.setImage(fileImage);
                }

                icon.setLockedToView(filesDisplayFrame);
                tempLabel.setBackgroudColor("primary");
                tempLabel.setForegroundColor("text2");
                tempLabel.setLockedToView(filesDisplayFrame);
                tempLabel.setTextPlacement(AdvancedGraphics.Side.LEFT);

                tempPlacement.add(icon, new UnitValue(40, UnitValue.Unit.PIXELS));
                tempPlacement.add(tempLabel, new UnitValue(0, UnitValue.Unit.AUTO));
                //temp.setTextPlacement(AdvancedGraphics.Side.LEFT);

                filesDisplayPlacement.add(temp, new UnitValue(40, UnitValue.Unit.PIXELS));
            }
        }
        filesDisplayFrame.getScrollController().setScrollY(0);
        filesDisplayFrame.getScrollController().setMaxScrollY(filesDisplayPlacement.getMinimalHeight()-filesDisplayFrame.getContentHeight());

        win.update();
    }

    public void addAllowed(String regex){
        allowedRegex.add(regex);
    }
}
