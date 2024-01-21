package AmbrosiaUI.Widgets;

import AmbrosiaUI.Prompts.FilePrompt;
import AmbrosiaUI.Prompts.PromptResult;
import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.Icons.Icon;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;
import AmbrosiaUI.Widgets.Placements.ScrollController;
import AmbrosiaUI.Widgets.Placements.VerticalPlacement;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;

public class FolderView extends Frame {
    protected String path = System.getProperty("user.home");

    protected final ArrayList<String> allowedRegex = new ArrayList<>();

    protected Frame pathDisplayFrame;
    protected HorizontalPlacement pathDisplayPlacement;
    protected Frame filesDisplayFrame;
    protected GridPlacement corePlacement;

    protected Scrollbar filesDisplayScrollbar;
    protected VerticalPlacement filesDisplayPlacement;

    protected int itemHeight = 30;
    public FolderView(Theme theme) {
        super("primary",0);
        setTheme(theme);
        initialize();
    }

    protected static final PathImage dirImage = new PathImage("icons/folder.pimg");
    protected static final PathImage fileImage = new PathImage("icons/file.pimg");

    protected static final PathImage pyFileImage = new PathImage("icons/pyfile.pimg");


    protected void initialize() {
        corePlacement = new GridPlacement(theme);
        corePlacement.setColumnTemplateFromString("auto 20px");
        corePlacement.setRowTemplateFromString("40px auto");

        this.setChildrenPlacement(corePlacement);

        pathDisplayFrame = new Frame("primary", 1);
        pathDisplayFrame.setHoverEffectDisabled(true);

        pathDisplayPlacement = new HorizontalPlacement(theme);
        pathDisplayFrame.setChildrenPlacement(pathDisplayPlacement);
        //pathDisplayFrame.setTextPlacement(AdvancedGraphics.Side.LEFT);

        filesDisplayFrame = new Frame("primary", 1);

        filesDisplayPlacement = new VerticalPlacement(theme) {
            @Override
            public void onResize() {
                //System.out.println(filesDisplayFrame.getScrollController().getMaxScrollY() + " " + filesDisplayPlacement.getMinimalHeight() + " " + filesDisplayFrame.getContentHeight());
                filesDisplayFrame.getScrollController().setMaxScrollY(
                        Math.max(filesDisplayPlacement.getMinimalHeight() - filesDisplayFrame.getContentHeight(), 0)
                );
            }
        };
        filesDisplayPlacement.setMinColumnWidth(300);
        filesDisplayFrame.setChildrenPlacement(filesDisplayPlacement);

        filesDisplayScrollbar = new Scrollbar("primary", filesDisplayFrame.getScrollController(), UnitValue.Direction.VERTICAL) {
            @Override
            public void onMouseDragged(MouseEvent e) {
                super.onMouseDragged(e);
            }
        };

        corePlacement.add(pathDisplayFrame, 0, 0, 1, 2);
        corePlacement.add(filesDisplayScrollbar, 1, 1, 1, 1);
        corePlacement.add(filesDisplayFrame, 1, 0, 1, 1);

        updateFiles();
    }

    protected void updatePath(){
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
            name = names.get(i);
            if(name.isEmpty()){
                name = paths.get(i);
            }

            Label temp = new Label(name, "small", 0, 1, 4) {
                @Override
                public void onMouseClicked(MouseEvent e) {
                    setPath(paths.get(placementIndex));
                }
            };
            pathDisplayPlacement.add(temp, new UnitValue(getStringWidth(name,theme.getFontByName("small"))+10, UnitValue.Unit.PIXELS));
        }
    }

    protected void updateFiles(){
        updatePath();

        File folder = new File(path);
        File[] filesUnsorted = folder.listFiles((dir, name) -> {
            if(new File(Paths.get(dir.getAbsolutePath(),name).toString()).isDirectory()){
                return true;
            }

            for(String s: allowedRegex){
                if(name.toLowerCase().matches(s)){
                    return true;
                }
            }

            return false;
        });

        if(filesUnsorted == null){
            return;
        }

        ArrayList<File> allContents = new ArrayList<>();

        for(File f: filesUnsorted){
            if(!f.isDirectory()){
                continue;
            }
            allContents.add(f);
        }

        for(File f: filesUnsorted){
            if(f.isDirectory()){
                continue;
            }
            allContents.add(f);
        }

        filesDisplayPlacement.clear();
        if(allContents.isEmpty()){
            return;
        }

        for (File file : allContents) {
            if (!file.isHidden() && !file.getName().startsWith(".")) {
                String name = file.getName();

                Frame temp = new Frame("primary",1);

                temp.setLockedToView(filesDisplayFrame);

                HorizontalPlacement tempPlacement = new HorizontalPlacement(theme);
                temp.setChildrenPlacement(tempPlacement);

                Icon icon = new Icon("primary", "primary", new PathImage(new Size(0,0)));

                Label tempLabel;

                if(file.isDirectory()){
                    tempLabel = new Label(name, "small",0,0,4){
                        @Override
                        public void onMouseClicked(MouseEvent e) {
                            setPath(file.getAbsolutePath());
                        }
                    };

                    dirImage.setScale((double) itemHeight / dirImage.getHeight());
                    icon.setImage(dirImage);
                }
                else {
                    tempLabel = new Label(name, "small", 0, 0, 4) {
                        @Override
                        public void onMouseClicked(MouseEvent e) {
                            fileSelected(file.getAbsolutePath());
                        }
                    };

                    PathImage finalImage = fileImage;

                    if (name.matches(".*\\.py")) {
                        finalImage = pyFileImage;
                    }
                    finalImage.setScale((double) itemHeight / fileImage.getHeight());

                    icon.setImage(finalImage);
                }

                icon.setLockedToView(filesDisplayFrame);
                tempLabel.setBackgroudColor("primary");
                tempLabel.setForegroundColor("text2");
                tempLabel.setLockedToView(filesDisplayFrame);
                tempLabel.setTextPlacement(AdvancedGraphics.Side.LEFT);

                tempPlacement.add(icon, new UnitValue(itemHeight, UnitValue.Unit.PIXELS));
                tempPlacement.add(tempLabel, new UnitValue(0, UnitValue.Unit.AUTO));
                //temp.setTextPlacement(AdvancedGraphics.Side.LEFT);

                filesDisplayPlacement.add(temp, new UnitValue(itemHeight, UnitValue.Unit.PIXELS));
            }
        }
        filesDisplayFrame.getScrollController().setScrollY(0);
        filesDisplayFrame.getScrollController().setMaxScrollY(Math.max(filesDisplayPlacement.getMinimalHeight(),0));
    }

    public void setPath(String path){
        this.path = path;
        updateFiles();
    }

    protected void fileSelected(String file){

    }

    public String getPath() {
        return path;
    }

    public void addAllowed(String regex){
        allowedRegex.add(regex);
        updateFiles();
    }

    public void noColumns(){
        filesDisplayPlacement.setMinColumnWidth(-1);
    }

    public ScrollController getScrollController(){
        return filesDisplayFrame.getScrollController();
    }
}
