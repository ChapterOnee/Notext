package App;

import AmbrosiaUI.Prompts.CreateFilePrompt;
import AmbrosiaUI.Prompts.FilePrompt;
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
import AmbrosiaUI.Widgets.Window;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class Root extends Window {
    private EditorLike editorInFocus;

    private final TabbedFrame editorSpace;
    private final Scrollbar scrollbar;

    private final Button save_file;

    private Window settingsWindow;

    private GridPlacement mainPlacement;
    public Root() {
        super();
        mainPlacement = new GridPlacement(theme);
        mainPlacement.setColumnTemplateFromString("20% auto 20px");
        mainPlacement.setRowTemplateFromString("auto");

        coreFrame.setChildrenPlacement(mainPlacement);

        HorizontalPlacement header_placement = new HorizontalPlacement(theme);
        coreHeader.setChildrenPlacement(header_placement);

        FolderView fw = new FolderView(theme){
            @Override
            protected void fileSelected(String file) {
                editorInFocus.openFile(file);
                Root.this.update();
            }

            @Override
            public void setPath(String path) {
                super.setPath(path);
                Root.this.update();
            }
        };
        mainPlacement.add(fw, 0,0,1,1);
        fw.initialize();

        fw.addAllowed(".*");
        fw.setPath("C:\\Users\\filah\\IdeaProjects\\TextEditor");

        Button new_file = new Button("New", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                CreateFilePrompt f = new CreateFilePrompt(theme){
                    @Override
                    public void onSubmited(PromptResult result) {
                        editorInFocus.clear();
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
                        editorInFocus = addEditor();
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
                        editorInFocus = addHexEditor();
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
                        editorInFocus = addPIconEditor();
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

        DropdownMenu openMenu = new DropdownMenu("Open..", "small", 0, 0, 4, new Size(200,30));
        openMenu.setzIndex(1);
        openMenu.setTextPlacement(AdvancedGraphics.Side.LEFT);

        header_placement.add(menu, new UnitValue(50, UnitValue.Unit.PIXELS));

        menu.addMenuItem(new DropdownMenuItem(new_file));
        menu.addMenuItem(new DropdownMenuItem()); // Spacer
        menu.addMenuItem(new DropdownMenuItem(openMenu));
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


        editorSpace = new TabbedFrame("primary", 0);
        mainPlacement.add(editorSpace,0,1,1,1);
        editorSpace.initialize();

        editorInFocus = addEditor();
        scrollbar.setController(editorInFocus.getScrollController());

        //editorSpacePlacement.add(secondaryEditor, new UnitValue(0, UnitValue.Unit.AUTO));

        mainPlacement.add(scrollbar, 0, 2, 1, 1);
        //core.resize(Size.fromDimension(this.getSize()));

        panel.setFocusTraversalKeysEnabled(false); // Stop taking away my TAB ://
        this.bindEvents();

        initializeSettings();
    }

    public TextEditor addEditor(){
        TextEditor editor = new TextEditor() {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);
                scrollbar.setController(this.getScrollController());
                Root.this.editorInFocus = this;
            }

            @Override
            public void onCurrentFileChanged() {
                super.onCurrentFileChanged();
                //System.out.println("A"+ editorInFocus.getText().hasFile());
                save_file.setDisabled(!editorInFocus.hasFile());
            }
        };
        Frame tab = editorSpace.addTab("AAAAAAAAA");
        HorizontalPlacement temp = new HorizontalPlacement(theme);
        tab.setChildrenPlacement(temp);
        temp.add(editor,new UnitValue(0, UnitValue.Unit.AUTO));

        return editor;
    }

    public HexEditor addHexEditor(){
        HexEditor editor = new HexEditor() {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);
                scrollbar.setController(this.getScrollController());
                Root.this.editorInFocus = this;
            }

            @Override
            public void onCurrentFileChanged() {
                //System.out.println("A"+ editorInFocus.getText().hasFile());
                save_file.setDisabled(!editorInFocus.hasFile());
            }
        };

        Frame tab = editorSpace.addTab("BBBBBBBB");
        HorizontalPlacement temp = new HorizontalPlacement(theme);
        tab.setChildrenPlacement(temp);
        temp.add(editor,new UnitValue(0, UnitValue.Unit.AUTO));

        return editor;
    }

    public PIconEditor addPIconEditor(){
        PIconEditor editor = new PIconEditor("primary",0) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);
                scrollbar.setController(this.getScrollController());
                Root.this.editorInFocus = this;
            }

            @Override
            public void onCurrentFileChanged() {
                //System.out.println("A"+ editorInFocus.getText().hasFile());
                save_file.setDisabled(!editorInFocus.hasFile());
            }
        };

        Frame tab = editorSpace.addTab("VVVVVVV");
        HorizontalPlacement temp = new HorizontalPlacement(theme);
        tab.setChildrenPlacement(temp);
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
                        Root.this.update();
                    }
                });


                selection.selectLast();
            }
        }

        SelectBox selection2 = new SelectBox("normal", 0,2,4);
        selection2.setItemSize(new Size(200,40));

        for(int i = 0;i < 9;i++){
            selection2.addOption(new SelectBoxOption((10+(i*5))+"%"){
                @Override
                public void onSelected() {
                    mainPlacement.getColumnTemplate().get(0).setValue((10+(index*5)));
                    update();
                }
            });
        }
        selection2.setSelected(2);

        settingsWindow.getCoreFrame().setChildrenPlacement(grid);

        Label themeLabel = new Label("Theme:", "normal", 0,0,4);
        themeLabel.setTextPlacement(AdvancedGraphics.Side.LEFT);
        themeLabel.setForegroundColor("text2");
        themeLabel.setBackgroudColor("primary");
        themeLabel.setHoverEffectDisabled(true);

        Label sidebarWidthLabel = new Label("File sidebar width:", "normal", 0,0,4);
        sidebarWidthLabel.setTextPlacement(AdvancedGraphics.Side.LEFT);
        sidebarWidthLabel.setForegroundColor("text2");
        sidebarWidthLabel.setBackgroudColor("primary");
        sidebarWidthLabel.setHoverEffectDisabled(true);

        grid.add(themeLabel,0,0,1,1);
        grid.add(selection,0,1,1,1);

        grid.add(sidebarWidthLabel,1,0,1,1);
        grid.add(selection2,1,1,1,1);
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

        Keybind closeEditor = new Keybind("CloseEditor", panel, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK)) {
            @Override
            public void activated(ActionEvent e) {
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

    @Override
    public void close() {
        destroy();
        settingsWindow.destroy();
    }

    public void openFile(String filename){
        editorInFocus.openFile(filename);
    }
}
