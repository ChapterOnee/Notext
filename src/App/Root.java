package App;

import AmbrosiaUI.Prompts.FilePrompt;
import AmbrosiaUI.Prompts.PromptResult;
import AmbrosiaUI.Widgets.Button;
import AmbrosiaUI.Widgets.SelectBox.SelectBox;
import AmbrosiaUI.Widgets.SelectBox.SelectBoxOption;
import AmbrosiaUI.Widgets.Window;
import AmbrosiaUI.Utility.*;
import AmbrosiaUI.Widgets.DropdownMenu.DropdownMenu;
import AmbrosiaUI.Widgets.DropdownMenu.DropdownMenuItem;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;
import AmbrosiaUI.Widgets.Scrollbar;
import AmbrosiaUI.Widgets.TextEditor.Selection;
import AmbrosiaUI.Widgets.TextEditor.TextEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

public class Root extends Window {
    private TextEditor editorInFocus;

    private Window settingsWindow;
    public Root() {
        super();
        GridPlacement placement = new GridPlacement(theme);
        placement.setColumnTemplateFromString("auto 20px");
        placement.setRowTemplateFromString("auto");

        coreFrame.setChildrenPlacement(placement);

        HorizontalPlacement header_placement = new HorizontalPlacement(theme);
        coreHeader.setChildrenPlacement(header_placement);

        Button new_file = new Button("New", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                editorInFocus.getText().clear();
            }
        };
        new_file.setTextPlacement(AdvancedGraphics.Side.LEFT);

        Button save_file = new Button("Save", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                editorInFocus.saveToCurrentlyOpenFile();
                editorInFocus.openFile(editorInFocus.getText().getCurrentFile());
            }
        };
        Button open_file = new Button("Open", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                FilePrompt f = new FilePrompt(theme){
                    @Override
                    public void onSubmited(PromptResult result) {
                        editorInFocus.openFile(result.getContent());
                    }
                };
                f.addAllowed(".*\\.(txt|py|java|json)");
                f.ask();
            }
        };
        open_file.setTextPlacement(AdvancedGraphics.Side.LEFT);
        save_file.setDisabled(true);
        save_file.setTextPlacement(AdvancedGraphics.Side.LEFT);

        Button save_file_as = new Button("Save as..", "small", 0,0,4) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
                fd.setDirectory("C:\\");
                fd.setFile("*.*");
                fd.setVisible(true);

                String filename = fd.getDirectory() + fd.getFile();
                if(!filename.equals("nullnull")) {
                    editorInFocus.getText().setCurrentFile(filename);
                    editorInFocus.saveToCurrentlyOpenFile();
                }
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


        DropdownMenu menu = new DropdownMenu("File", "small",0, 0, 4, new Size(100,30));
        menu.setzIndex(1);

        header_placement.add(menu, new UnitValue(50, UnitValue.Unit.PIXELS));

        menu.addMenuItem(new DropdownMenuItem(new_file));
        menu.addMenuItem(new DropdownMenuItem(open_file));
        menu.addMenuItem(new DropdownMenuItem(save_file));
        menu.addMenuItem(new DropdownMenuItem(save_file_as));
        menu.addMenuItem(new DropdownMenuItem()); // Spacer
        menu.addMenuItem(new DropdownMenuItem(settings));

        Scrollbar scrollbar = new Scrollbar("primary", null, UnitValue.Direction.VERTICAL);

        /*TextEditor secondaryEditor = new TextEditor(){
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
                save_file.setDisabled(!editorInFocus.getText().hasFile());
            }
        };*/


        TextEditor primaryEditor = new TextEditor() {
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
                save_file.setDisabled(!editorInFocus.getText().hasFile());
            }
        };

        editorInFocus = primaryEditor;
        scrollbar.setController(editorInFocus.getScrollController());

        Frame editorSpace = new Frame("accent2", 0);

        HorizontalPlacement editorSpacePlacement = new HorizontalPlacement(theme);
        editorSpace.setChildrenPlacement(editorSpacePlacement);

        editorSpacePlacement.add(primaryEditor, new UnitValue(0, UnitValue.Unit.AUTO));
        //editorSpacePlacement.add(secondaryEditor, new UnitValue(0, UnitValue.Unit.AUTO));

        placement.add(editorSpace,0,0,1,1);
        placement.add(scrollbar, 0, 1, 1, 1);

        //core.resize(Size.fromDimension(this.getSize()));

        panel.setFocusTraversalKeysEnabled(false); // Stop taking away my TAB ://
        this.bindEvents();

        initializeSettings();
    }

    public void initializeSettings(){
        settingsWindow = new Window(theme) {
            @Override
            public void close() {
                settingsWindow.hide();
            }
        };

        GridPlacement grid = new GridPlacement(theme);
        grid.setColumnTemplateFromString("100px auto");
        grid.setRowTemplateFromString("40px auto");

        SelectBox selection = new SelectBox("normal", 0,2,4);
        selection.addOption(new SelectBoxOption("Dark"){
            @Override
            public void onSelected() {
                theme.loadFromFile("themes/default.thm");
                Root.this.update();
            }
        });
        selection.addOption(new SelectBoxOption("Light") {
            @Override
            public void onSelected() {
                theme.loadFromFile("themes/white.thm");
                Root.this.update();
            }
        });
        selection.addOption(new SelectBoxOption("Moonlight") {
            @Override
            public void onSelected() {
                theme.loadFromFile("themes/moonlight.thm");
                Root.this.update();
            }
        });
        selection.addOption(new SelectBoxOption("Ainz") {
            @Override
            public void onSelected() {
                theme.loadFromFile("themes/Ainz.thm");
                Root.this.update();
            }
        });

        selection.setSelected(3);


        settingsWindow.getCoreFrame().setChildrenPlacement(grid);

        grid.add(selection,0,0,1,1);
    }

    public void bindEvents(){
        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                editorInFocus.getScrollController().setScrollY(Math.max(0, editorInFocus.getScrollController().getScrollY() + e.getWheelRotation()*25));
                Root.this.update();
            }
        });

        Keybind save = new Keybind("Save", panel, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)) {
            @Override
            public void activated(ActionEvent e) {
                if(editorInFocus.getText().hasFile()) {
                    editorInFocus.saveToCurrentlyOpenFile();
                }
            }
        };

        Keybind copy = new Keybind("Copy", panel, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK)){
            @Override
            public void activated(ActionEvent e) {
                if(!editorInFocus.getActiveSelections().isEmpty()){
                    Selection selection = editorInFocus.getActiveSelections().get(0);
                    selection = selection.getReorganized();
                    //System.out.println(selection.getSelectedContentFormated());

                    StringSelection sel = new StringSelection(selection.getSelectedContentFormated());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(sel, sel);
                }
            }
        };

        Keybind paste = new Keybind("Paste", panel, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK)){
            @Override
            public void activated(ActionEvent e) {
                try {
                    editorInFocus.getText().storeState();
                    editorInFocus.getText().blockStoring();

                    String data = (String) Toolkit.getDefaultToolkit()
                            .getSystemClipboard().getData(DataFlavor.stringFlavor);

                    String[] formated_data = data.split("\n");

                    for(int i = 0;i < formated_data.length;i++) {
                        if (i == 0) {
                            editorInFocus.insertStringOnCursor(formated_data[i]);
                        }
                        else{
                            editorInFocus.getText().insertNewLine(formated_data[i], editorInFocus.getCursor().getY()+1);
                        }
                    }

                    editorInFocus.getCursor().setY(editorInFocus.getCursor().getY()+1);
                    editorInFocus.getCursor().upToLineEnd();

                    editorInFocus.getText().unblockStoring();
                }
                catch (Exception ex){
                    editorInFocus.getText().unblockStoring();
                    ex.printStackTrace();
                }
            }
        };

        Keybind revert = new Keybind("Revert", panel, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK)){
            @Override
            public void activated(ActionEvent e) {
                editorInFocus.getText().revert();
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
