package App;

import AmbrosiaUI.Prompts.CreateFilePrompt;
import AmbrosiaUI.Prompts.FilePrompt;
import AmbrosiaUI.Prompts.FolderPrompt;
import AmbrosiaUI.Prompts.PromptResult;
import AmbrosiaUI.Widgets.*;
import AmbrosiaUI.Widgets.Button;
import AmbrosiaUI.Widgets.Editors.HexEditor.HexEditor;
import AmbrosiaUI.Widgets.Editors.PIconEditor.PIconEditor;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Icons.PathImage;
import AmbrosiaUI.Widgets.Scrollbar;
import AmbrosiaUI.Widgets.SelectBox.SelectBox;
import AmbrosiaUI.Widgets.SelectBox.SelectBoxOption;
import AmbrosiaUI.Utility.*;
import AmbrosiaUI.Widgets.DropdownMenu.DropdownMenu;
import AmbrosiaUI.Widgets.DropdownMenu.DropdownMenuItem;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;
import AmbrosiaUI.Widgets.Editors.EditorLike;
import AmbrosiaUI.Widgets.Editors.TextEditor.TextEditor;
import AmbrosiaUI.Widgets.TabbedFrame.TabbedFrame;
import AmbrosiaUI.Widgets.TabbedFrame.TabbedFrameTab;
import AmbrosiaUI.Widgets.Window;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Root extends Window {
    private EditorLike editorInFocus;

    private final TabbedFrame editorSpace;
    private final Scrollbar scrollbar;

    private final Button save_file;

    private Window settingsWindow;

    private HorizontalPlacement mainPlacement;
    public Root() {
        super();
        mainPlacement = new HorizontalPlacement(theme);

        coreFrame.setChildrenPlacement(mainPlacement);

        HorizontalPlacement header_placement = new HorizontalPlacement(theme);
        coreHeader.setChildrenPlacement(header_placement);

        FolderView fw = new FolderView(theme){

            @Override
            protected void fileSelected(String file) {
                boolean found = false;

                for(TabbedFrameTab tab: editorSpace.getTabs()){
                    EditorLike editor = ((EditorLike) tab.getBoundElement().getChildrenPlacement().getChildren().get(0).getBoundElement());

                    if(editor.getCurrentFile() != null && editor.getCurrentFile().equals(file)){
                        Root.this.setCurrentEditor(editor);
                        editorSpace.selectTab(editorSpace.getTabs().indexOf(tab));
                        found = true;
                        break;
                    }
                }

                if(!found){
                    EditorLike newEditor;
                    if(file.matches(".*\\.pimg")){
                        newEditor = addPIconEditor();
                    }
                    else {
                        newEditor = addEditor();
                    }
                    newEditor.openFile(file);
                    editorSpace.selectTab(editorSpace.getTabs().size()-1);

                    if(newEditor.getCurrentFile() == null){
                        editorSpace.removeTab(editorSpace.getTabs().size()-1);
                        return;
                    }
                    setCurrentEditor(newEditor);
                }
                Root.this.update();
            }

            @Override
            public void setPath(String path) {
                super.setPath(path);
                Root.this.update();
            }

            @Override
            protected void updatePath() {
                pathDisplayPlacement.clear();
                String name = path;

                Label temp = new Label(name,"small",0,0,5);
                temp.setHoverEffectDisabled(true);
                pathDisplayPlacement.add(temp, new UnitValue(getStringWidth(name,theme.getFontByName("small"))+10, UnitValue.Unit.PIXELS));
            }
        };
        mainPlacement.add(fw, new UnitValue(0, UnitValue.Unit.FIT));
        fw.setCanAddFolders(false);
        fw.setNavigationOnFolderClick(false);
        fw.initialize();

        fw.addAllowed(".*");
        //fw.setPath("C:\\Users\\filah\\IdeaProjects\\TextEditor");
        fw.setPath("/home/hades/IdeaProjects/text_editor");

        Button new_file = new Button("New", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                CreateFilePrompt f = new CreateFilePrompt(theme){
                    @Override
                    public void onSubmited(PromptResult result) {
                        setCurrentEditor(addEditor());

                        editorInFocus.setCurrentFile(result.getContent());
                        editorInFocus.saveToCurrentlyOpenFile();
                        win.update();
                    }
                };
                f.setPath(fw.getPath());
                f.addAllowed(".*");
                f.ask();
            }
        };
        new_file.setTextPlacement(AdvancedGraphics.Side.LEFT);

        save_file = new Button("Save", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                editorInFocus.saveToCurrentlyOpenFile();
                editorInFocus.openFile(editorInFocus.getCurrentFile());
            }
        };
        Button open_file = new Button("In Current Editor", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                FilePrompt f = new FilePrompt(theme){
                    @Override
                    public void onSubmited(PromptResult result) {
                        editorInFocus.openFile(result.getContent());
                        Root.this.update();
                    }
                };
                f.setPath(fw.getPath());
                f.addAllowed(editorInFocus.getAllowedFiles());
                f.ask();
            }
        };
        open_file.setTextPlacement(AdvancedGraphics.Side.LEFT);

        Button open_file_in_new_editor = new Button("In New Text Editor", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                FilePrompt f = new FilePrompt(theme){
                    @Override
                    public void onSubmited(PromptResult result) {
                        Root.this.setCurrentEditor(addEditor());
                        editorInFocus.openFile(result.getContent());
                        Root.this.update();
                    }
                };
                f.setPath(fw.getPath());
                f.addAllowed(new TextEditor().getAllowedFiles());
                f.ask();
            }
        };
        open_file_in_new_editor.setTextPlacement(AdvancedGraphics.Side.LEFT);

        Button open_file_in_new_editor_hex = new Button("In New Hex Editor", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                FilePrompt f = new FilePrompt(theme){
                    @Override
                    public void onSubmited(PromptResult result) {
                        Root.this.setCurrentEditor(addHexEditor());
                        editorInFocus.openFile(result.getContent());
                        Root.this.update();
                    }
                };
                f.setPath(fw.getPath());
                f.addAllowed(new HexEditor().getAllowedFiles());
                f.ask();
            }
        };
        open_file_in_new_editor_hex.setTextPlacement(AdvancedGraphics.Side.LEFT);

        Button open_file_in_new_editor_icon = new Button("In New Icon Editor", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                FilePrompt f = new FilePrompt(theme){
                    @Override
                    public void onSubmited(PromptResult result) {
                        Root.this.setCurrentEditor(addPIconEditor());
                        editorInFocus.openFile(result.getContent());
                        Root.this.update();
                    }
                };
                f.setPath(fw.getPath());
                f.addAllowed(new PIconEditor().getAllowedFiles());
                f.ask();
            }
        };
        open_file_in_new_editor_icon.setTextPlacement(AdvancedGraphics.Side.LEFT);

        Button open_folder = new Button("Open Folder", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                FolderPrompt f = new FolderPrompt(theme){
                    @Override
                    public void onSubmited(PromptResult result) {
                        fw.setPath(result.getContent());
                        Root.this.update();
                    }
                };
                f.setPath(fw.getPath());
                f.ask();
            }
        };
        open_folder.setTextPlacement(AdvancedGraphics.Side.LEFT);

        save_file.setDisabled(true);
        save_file.setTextPlacement(AdvancedGraphics.Side.LEFT);

        Button save_file_as = new Button("Save as..", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                CreateFilePrompt f = new CreateFilePrompt(theme){
                    @Override
                    public void onSubmited(PromptResult result) {
                        editorInFocus.setCurrentFile(result.getContent());
                        editorInFocus.saveToCurrentlyOpenFile();
                    }
                };
                f.setPath(fw.getPath());
                f.addAllowed(".*");
                f.ask();
            }
        };
        save_file_as.setTextPlacement(AdvancedGraphics.Side.LEFT);
        Button settings = new Button("Settings", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                settingsWindow.show();
            }
        };
        settings.setTextPlacement(AdvancedGraphics.Side.LEFT);

        DropdownMenu menu = new DropdownMenu("File", "small",0, 0, 4, new Size(200,30));
        menu.setzIndex(1);

        DropdownMenu tools = new DropdownMenu("Tools", "small",0,0,4,new Size(200,30));
        tools.setzIndex(1);

        DropdownMenu openMenu = new DropdownMenu("Open..", "small", 0, 0, 4, new Size(200,30));
        openMenu.setzIndex(1);
        openMenu.setTextPlacement(AdvancedGraphics.Side.LEFT);

        header_placement.add(menu, new UnitValue(0, UnitValue.Unit.FIT));
        header_placement.add(tools, new UnitValue(0, UnitValue.Unit.FIT));

        menu.addMenuItem(new DropdownMenuItem(new_file));
        menu.addMenuItem(new DropdownMenuItem()); // Spacer
        menu.addMenuItem(new DropdownMenuItem(openMenu));
        menu.addMenuItem(new DropdownMenuItem(open_folder));
        menu.addMenuItem(new DropdownMenuItem()); // Spacer
        menu.addMenuItem(new DropdownMenuItem(save_file));
        menu.addMenuItem(new DropdownMenuItem(save_file_as));
        menu.addMenuItem(new DropdownMenuItem()); // Spacer
        menu.addMenuItem(new DropdownMenuItem(settings));

        openMenu.addMenuItem(new DropdownMenuItem(open_file));
        openMenu.addMenuItem(new DropdownMenuItem(open_file_in_new_editor));
        openMenu.addMenuItem(new DropdownMenuItem(open_file_in_new_editor_hex));
        openMenu.addMenuItem(new DropdownMenuItem(open_file_in_new_editor_icon));

        scrollbar = new Scrollbar("primary", null, UnitValue.Direction.VERTICAL);


        editorSpace = new TabbedFrame("primary", 0) {
            @Override
            public void selectTab(int index) {
                super.selectTab(index);

                EditorLike editor = ((EditorLike) this.getTabs().get(index).getBoundElement().getChildrenPlacement().getChildren().get(0).getBoundElement());

                if(editor != editorInFocus) {
                    setCurrentEditor(editor);
                }
                Root.this.update();
            }
        };
        mainPlacement.add(editorSpace,new UnitValue(0, UnitValue.Unit.AUTO));
        editorSpace.initialize();

        editorInFocus = addEditor();
        scrollbar.setController(editorInFocus.getScrollController());

        //editorSpacePlacement.add(secondaryEditor, new UnitValue(0, UnitValue.Unit.AUTO));

        mainPlacement.add(scrollbar, new UnitValue(20, UnitValue.Unit.PIXELS));
        //core.resize(Size.fromDimension(this.getSize()));

        panel.setFocusTraversalKeysEnabled(false); // Stop taking away my TAB ://
        this.bindEvents();

        initializeSettings();
    }

    public TextEditor addEditor(){
        TabbedFrameTab tab = editorSpace.addTab("unnamed *");

        TextEditor editor = new TextEditor() {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);
                Root.this.setCurrentEditor(this);
            }

            @Override
            public void onCurrentFileChanged() {
                super.onCurrentFileChanged();
                //System.out.println("A"+ editorInFocus.getText().hasFile());
                tab.setName(new File(getCurrentFile()).getName());
                save_file.setDisabled(!editorInFocus.hasFile());
            }
        };
        Frame tabFrame = (Frame) tab.getBoundElement();
        HorizontalPlacement temp = new HorizontalPlacement(theme);
        tabFrame.setChildrenPlacement(temp);
        temp.add(editor,new UnitValue(0, UnitValue.Unit.AUTO));

        return editor;
    }

    public HexEditor addHexEditor(){
        TabbedFrameTab tab = editorSpace.addTab("unnamed *");

        HexEditor editor = new HexEditor() {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);
                Root.this.setCurrentEditor(this);
            }

            @Override
            public void onCurrentFileChanged() {
                //System.out.println("A"+ editorInFocus.getText().hasFile());
                tab.setName(new File(getCurrentFile()).getName());
                save_file.setDisabled(!editorInFocus.hasFile());
            }
        };

        Frame tabFrame = (Frame) tab.getBoundElement();
        HorizontalPlacement temp = new HorizontalPlacement(theme);
        tabFrame.setChildrenPlacement(temp);
        temp.add(editor,new UnitValue(0, UnitValue.Unit.AUTO));

        return editor;
    }

    public PIconEditor addPIconEditor(){
        TabbedFrameTab tab = editorSpace.addTab("unnamed *");

        PIconEditor editor = new PIconEditor("primary",0) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);
                Root.this.setCurrentEditor(this);
            }

            @Override
            public void onCurrentFileChanged() {
                //System.out.println("A"+ editorInFocus.getText().hasFile());
                tab.setName(new File(getCurrentFile()).getName());
                save_file.setDisabled(!editorInFocus.hasFile());
            }
        };

        Frame tabFrame = (Frame) tab.getBoundElement();
        HorizontalPlacement temp = new HorizontalPlacement(theme);
        tabFrame.setChildrenPlacement(temp);
        temp.add(editor,new UnitValue(0, UnitValue.Unit.AUTO));

        return editor;
    }

    public void initializeSettings(){
        settingsWindow = new Window(theme) {
            @Override
            public void close() {
                settingsWindow.hide();
            }
        };

        GridPlacement grid = new GridPlacement(theme);
        grid.setColumnTemplateFromString("200px 100px auto");
        grid.setRowTemplateFromString("40px 40px auto");

        SelectBox selection = new SelectBox("normal", 0,2,4);
        selection.setItemSize(new Size(200,40));

        File folder = new File("themes");
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null){
            Logger.printWarning("No themes, folder is empty.");
            return;
        }

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && listOfFile.getName().endsWith(".thm")) {
                String nm = listOfFile.getName().split("\\.")[0];
                int w = Widget.getStringWidth(nm, theme.getFontByName(selection.getFont()));

                if(w > selection.getItemSize().width){
                    selection.getItemSize().width = w;
                }

                selection.addOption(new SelectBoxOption(nm){
                    @Override
                    public void onSelected() {
                        theme.loadFromFile(listOfFile.getAbsolutePath());
                        Window.reloadAllWindows();
                    }
                });


                selection.selectLast();
            }
        }


        settingsWindow.getCoreFrame().setChildrenPlacement(grid);

        Label themeLabel = new Label("Theme:", "normal", 0,0,4);
        themeLabel.setTextPlacement(AdvancedGraphics.Side.LEFT);
        themeLabel.setForegroundColor("text2");
        themeLabel.setBackgroudColor("primary");
        themeLabel.setHoverEffectDisabled(true);

        grid.add(themeLabel,0,0,1,1);
        grid.add(selection,0,1,1,1);
    }

    public void bindEvents(){
        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(editorInFocus.dontAutoScroll()){
                    Root.this.update();
                    return;
                }
                editorInFocus.getScrollController().setScrollY(Math.max(0, editorInFocus.getScrollController().getScrollY() + e.getWheelRotation()*25));
                Root.this.update();
            }
        });

        Keybind save = new Keybind("Save", panel, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)) {
            @Override
            public void activated(ActionEvent e) {
                if(editorInFocus.hasFile()) {
                    editorInFocus.saveToCurrentlyOpenFile();
                }
            }
        };

        Keybind reloadEditor = new Keybind("ReloadEditor", panel, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK)) {
            @Override
            public void activated(ActionEvent e) {
                if (editorInFocus != null){
                    editorInFocus.openFile(editorInFocus.getCurrentFile());
                }
                PathImage.reloadAllImagesFromFiles();
                /*if(editorSpacePlacement.getChildren().size() < 2){
                    return;
                }
                editorSpacePlacement.remove((Widget) editorInFocus);
                editorInFocus = (EditorLike) editorSpacePlacement.getChildren().get(0).getBoundElement();
                scrollbar.setController(editorInFocus.getScrollController());*/
            }
        };

        Keybind revert = new Keybind("Revert", panel, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK)){
            @Override
            public void activated(ActionEvent e) {
                editorInFocus.revert();
            }
        };
    }

    public void setCurrentEditor(EditorLike editor){
        editorInFocus = editor;
        element_in_focus = (Widget) editorInFocus;

        for(TabbedFrameTab tab: editorSpace.getTabs()){
            EditorLike editorf = ((EditorLike) tab.getBoundElement().getChildrenPlacement().getChildren().get(0).getBoundElement());

            if(editorf == editorInFocus){
                editorSpace.selectTab(editorSpace.getTabs().indexOf(tab));
                break;
            }
        }

        scrollbar.setController(editorInFocus.getScrollController());
        update();
    }

    @Override
    public void close() {
        destroy();
        settingsWindow.destroy();
    }

    public void openFile(String filename){
        editorInFocus.openFile(filename);
    }
}
