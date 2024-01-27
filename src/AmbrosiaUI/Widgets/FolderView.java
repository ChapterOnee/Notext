package AmbrosiaUI.Widgets;

import AmbrosiaUI.ContextMenus.ContextMenu;
import AmbrosiaUI.ContextMenus.ContextMenuOption;
import AmbrosiaUI.Prompts.PromptResult;
import AmbrosiaUI.Prompts.TextPrompt;
import AmbrosiaUI.Utility.*;
import AmbrosiaUI.Widgets.Editors.TextEditor.TextEditor;
import AmbrosiaUI.Widgets.Icons.Icon;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Placements.*;
import org.w3c.dom.Text;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class FolderView extends Frame {
    protected String path = System.getProperty("user.home");

    protected final ArrayList<String> allowedRegex = new ArrayList<>();
    protected final ArrayList<String> expandedPaths = new ArrayList<>();

    protected Frame pathDisplayFrame;
    protected HorizontalPlacement pathDisplayPlacement;
    protected Frame filesDisplayFrame;
    protected GridPlacement corePlacement;

    protected Scrollbar filesDisplayScrollbar;
    protected VerticalPlacement filesDisplayPlacement;

    private boolean foldersOnly = false;
    private boolean canAddFolders = true;

    private boolean navigationOnFolderClick = true;

    protected int itemHeight = 25;
    public FolderView(Theme theme) {
        super("primary",0);
        setTheme(theme);
    }

    public FolderView(Theme theme, int itemHeight){
        super("primary",0);
        setTheme(theme);
        this.itemHeight = itemHeight;
    }

    protected static final PathImage dirImage = new PathImage("icons/folder.pimg");
    protected static final PathImage fileImage = new PathImage("icons/file.pimg");

    protected static final PathImage pyFileImage = new PathImage("icons/pyfile.pimg");

    protected static final PathImage expandImage = new PathImage("icons/expand.pimg");
    protected static final PathImage expandedImage = new PathImage("icons/expanded.pimg");

    protected static final PathImage addFolderImage = new PathImage("icons/addFolder.pimg");
    protected static final PathImage addFileImage = new PathImage("icons/addFile.pimg");

    protected static final PathImage renameFileImage = new PathImage("icons/rename.pimg");


    public void initialize() {
        corePlacement = new GridPlacement(theme);
        corePlacement.setColumnTemplateFromString("auto 20px 20px");
        corePlacement.setRowTemplateFromString("40px auto");

        if(canAddFolders) {
            Icon addFolder = new Icon("secondary", "accent", addFolderImage) {
                @Override
                public void onMousePressed(MouseEvent e) {
                    TextPrompt f = new TextPrompt(theme, "New Folder Name:") {
                        @Override
                        public void onSubmited(PromptResult result) {
                            FileUtil.createFolder(path, result.getContent());
                            updateFiles();
                        }
                    };
                    f.ask();
                }
            };
            addFolder.setMargin(2);
            corePlacement.add(addFolder, 0, 1, 1, 2);
        }

        this.setChildrenPlacement(corePlacement);

        pathDisplayFrame = new Frame("primary", 1);
        corePlacement.add(pathDisplayFrame, 0, 0, 1, 1);
        pathDisplayFrame.setHoverEffectDisabled(true);

        pathDisplayPlacement = new HorizontalPlacement(theme);
        pathDisplayFrame.setChildrenPlacement(pathDisplayPlacement);
        //pathDisplayFrame.setTextPlacement(AdvancedGraphics.Side.LEFT);

        filesDisplayFrame = new Frame("primary", 1);
        corePlacement.add(filesDisplayFrame, 1, 0, 1, 1);

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

        corePlacement.add(filesDisplayScrollbar, 1, 2, 1, 1);

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
            pathDisplayPlacement.add(temp, new UnitValue(0, UnitValue.Unit.FIT));
        }
    }


    public void addFilesToPlacement(ArrayList<File> allContents, VerticalPlacement placement){
        for (File file : allContents) {
            if (!file.isHidden() && !file.getName().startsWith(".")) {
                generateFileWidget(file,placement);
            }
        }
    }

    protected Frame generateFileWidget(File file, VerticalPlacement placement){
        String name = file.getName();

        Frame temp = new Frame("primary",1);
        placement.add(temp, new UnitValue(0, UnitValue.Unit.FIT));
        temp.setLockedToView(filesDisplayFrame);

        GridPlacement tempPlacement = new GridPlacement(theme);
        tempPlacement.setColumnTemplateFromString(itemHeight+"px auto 40px");
        tempPlacement.setRowTemplateFromString(itemHeight+"px auto");
        temp.setChildrenPlacement(tempPlacement);

        Icon icon = new Icon("primary", "primary", new PathImage(new Size(0,0)));

        Label tempLabel;
        Icon expand = null;

        ContextMenu menu = new ContextMenu(theme);

        if(file.isDirectory()){
            tempLabel = new Label(name, "small",0,0,4){
                @Override
                public void onMouseClicked(MouseEvent e) {
                    if(navigationOnFolderClick) {
                        setPath(file.getAbsolutePath());
                    }
                }
            };

            icon.setImage(dirImage.getScaled((double) itemHeight / dirImage.getHeight()));

            boolean expanded = false;
            Frame content = new Frame("primary", 0);
            content.setBorderColor("primary");
            content.setBorderModifier(new GraphicsBorderModifier(false,false,false,true));
            content.setBorderWidth(10);
            content.setLockedToView(filesDisplayFrame);

            expand = new Icon("primary", "accent", expandImage){
                private boolean expanded;

                private Icon init(boolean var, Frame content){
                    this.expanded = var;
                    this.content = content;
                    return this;
                }

                private Frame content;
                @Override
                public void onMouseClicked(MouseEvent e) {
                    if(!expanded) {
                        VerticalPlacement contentPlacement = new VerticalPlacement(theme){
                            @Override
                            public void resize(Size new_size) {
                                super.resize(new_size);
                                temp.setMinHeight(getMinimalHeight()+itemHeight+5);
                            }
                        };
                        tempPlacement.add(content, 1, 0,1,3);
                        content.setChildrenPlacement(contentPlacement);

                        ArrayList<File> data = getAllFilesInDirectory(file.getAbsolutePath());
                        addFilesToPlacement(data,contentPlacement);

                        this.setImage(expandedImage);

                        expanded = true;

                        expandedPaths.add(file.getAbsolutePath());
                    }
                    else{
                        this.setImage(expandImage);

                        tempPlacement.remove(content);

                        temp.setMinHeight(itemHeight);
                        expanded = false;

                        expandedPaths.remove(file.getAbsolutePath());
                    }

                    window.update();
                    window.update();
                    window.update();
                    window.update();
                }
            }.init(expanded, content);

            menu.addOption(new ContextMenuOption("Rename",renameFileImage){
                @Override
                protected void execute() {
                    TextPrompt prompt = new TextPrompt(theme, "New Name:"){
                        @Override
                        public void onSubmited(PromptResult result) {
                            FileUtil.renameFile(file, result.getContent());
                            updateFiles();
                        }
                    };
                    prompt.ask();
                }
            });
            menu.addOption(new ContextMenuOption("New File",addFileImage){
                @Override
                protected void execute() {
                    TextPrompt prompt = new TextPrompt(theme, "New File Name:"){
                        @Override
                        public void onSubmited(PromptResult result) {
                            File nw = new File(Paths.get(file.getAbsolutePath(),result.getContent()).toString());
                            try {
                                nw.createNewFile();
                                updateFiles();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                    prompt.ask();
                }
            });
            menu.addOption(new ContextMenuOption("New Folder", addFolderImage){
                @Override
                protected void execute() {
                    TextPrompt f = new TextPrompt(theme, "New Folder Name:") {
                        @Override
                        public void onSubmited(PromptResult result) {
                            FileUtil.createFolder(file.getAbsolutePath(), result.getContent());
                            updateFiles();
                        }
                    };
                    f.ask();
                }
            });
        }
        else {
            tempLabel = new Label(name, "small", 0, 0, 4) {
                @Override
                public void onMouseClicked(MouseEvent e) {
                    fileSelected(file.getAbsolutePath());
                }
            };

            menu.addOption(new ContextMenuOption("Rename",renameFileImage){
                @Override
                protected void execute() {
                    TextPrompt prompt = new TextPrompt(theme, "New File Name:"){
                        @Override
                        public void onSubmited(PromptResult result) {
                            FileUtil.renameFile(file, result.getContent());
                            updateFiles();
                        }
                    };
                    prompt.ask();
                }
            });

            PathImage finalImage = fileImage;

            if (name.matches(".*\\.py")) {
                finalImage = pyFileImage;
            }

            icon.setImage(finalImage.getScaled((double) itemHeight / fileImage.getHeight()));
        }
        tempLabel.setContextMenu(menu);

        icon.setLockedToView(filesDisplayFrame);
        tempLabel.setBackgroudColor("primary");
        tempLabel.setForegroundColor("text2");
        tempLabel.setLockedToView(filesDisplayFrame);
        tempLabel.setTextPlacement(AdvancedGraphics.Side.LEFT);

        tempPlacement.add(icon, 0,0,1,1);

        if(expand != null) {
            expand.setLockedToView(filesDisplayFrame);
            tempPlacement.add(expand, 0,2,1,1);
            tempPlacement.add(tempLabel, 0,1,1,1);
        }
        else{
            tempPlacement.add(tempLabel, 0,1,1,2);
        }
        //temp.setTextPlacement(AdvancedGraphics.Side.LEFT);

        temp.setMinHeight(itemHeight);

        if(expandedPaths.contains(file.getAbsolutePath())){
            expand.onMouseClicked(null);
        }

        return temp;
    }

    protected void updateFiles(){
        updatePath();

        ArrayList<File> allContents = getAllFilesInDirectory(path);

        filesDisplayPlacement.clear();
        if(allContents.isEmpty()){
            return;
        }

        System.out.println(expandedPaths);

        addFilesToPlacement(allContents, filesDisplayPlacement);

        filesDisplayFrame.getScrollController().setScrollY(0);
        filesDisplayFrame.getScrollController().setMaxScrollY(Math.max(filesDisplayPlacement.getMinimalHeight(),0));

        //expandedPaths.clear();
        window.update();
    }

    private ArrayList<File> getAllFilesInDirectory(String path){
        ArrayList<File> allContents = new ArrayList<>();

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
            return allContents;
        }

        for(File f: filesUnsorted){
            if(!f.isDirectory()){
                continue;
            }
            allContents.add(f);
        }

        if(foldersOnly){
            return allContents;
        }

        for(File f: filesUnsorted){
            if(f.isDirectory()){
                continue;
            }
            allContents.add(f);
        }

        return allContents;
    }

    public void setPath(String path){
        this.path = path;
        expandedPaths.clear();
        updateFiles();
    }

    protected void fileSelected(String file){

    }

    @Override
    public int getMinWidth() {
        int width = 5;

        for(PlacementCell cell: pathDisplayPlacement.getChildren()){
            HorizontalPlacement.HorizontalPlacementCell cell2 = (HorizontalPlacement.HorizontalPlacementCell) cell;

            width += cell2.getWidth().toPixels(pathDisplayPlacement.getRootSize(),cell2.getBoundElement(), UnitValue.Direction.HORIZONTAL);
        }

        return Math.max(width,350);
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

    public boolean isFoldersOnly() {
        return foldersOnly;
    }

    public void setFoldersOnly(boolean foldersOnly) {
        this.foldersOnly = foldersOnly;
    }

    public boolean canAddFolders() {
        return canAddFolders;
    }

    public void setCanAddFolders(boolean canAddFolders) {
        this.canAddFolders = canAddFolders;
    }

    public boolean canNavigateOnFolderClick() {
        return navigationOnFolderClick;
    }

    public void setNavigationOnFolderClick(boolean navigationOnFolderClick) {
        this.navigationOnFolderClick = navigationOnFolderClick;
    }
}
