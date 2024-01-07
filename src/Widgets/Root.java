package Widgets;

import Utility.*;
import Widgets.DropdownMenu.DropdownMenu;
import Widgets.DropdownMenu.DropdownMenuItem;
import Widgets.Placements.GridPlacement;
import Widgets.Placements.HorizontalPlacement;
import Widgets.Placements.VerticalPlacement;
import Widgets.TextEditor.EditorLine;
import Widgets.TextEditor.Scrollbar;
import Widgets.TextEditor.Selection;
import Widgets.TextEditor.TextEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

public class Root extends Core {
    private TextEditor editorInFocus;
    public Root() {
        GridPlacement placement = new GridPlacement(theme);
        placement.setColumnTemplateFromString("auto 20px");
        placement.setRowTemplateFromString("40px auto");

        core_frame.setChildrenPlacement(placement);

        Frame header = new Frame("secondary");

        HorizontalPlacement header_placement = new HorizontalPlacement(theme);
        header.setChildrenPlacement(header_placement);

        Button open_file = new Button("Open", "small", 0,0) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
                fd.setDirectory("C:\\");
                fd.setFile("*.*");
                fd.setVisible(true);

                String filename = fd.getDirectory() + fd.getFile();
                System.out.println(filename);
                editorInFocus.openFile(filename);
            }
        };
        Button save_file = new Button("Save", "small", 0, 0) {
            @Override
            public void onMouseClicked(MouseEvent e) {
                editorInFocus.saveToCurrentlyOpenFile();
                editorInFocus.openFile(editorInFocus.getText().getCurrentFile());
            }
        };

        DropdownMenu menu = new DropdownMenu("File", "small",0,0, new Size(100,30));
        menu.setzIndex(1);


        header_placement.add(menu, new UnitValue(50, UnitValue.Unit.PIXELS));

        menu.addMenuItem(new DropdownMenuItem(open_file));
        menu.addMenuItem(new DropdownMenuItem(save_file));

        TextEditor primaryEditor = new TextEditor() {
            @Override
            public void onMouseClicked(MouseEvent e) {
                super.onMouseClicked(e);
                Root.this.editorInFocus = this;
            }
        };
        /*TextEditor secondaryEditor = new TextEditor(){
            @Override
            public void onClicked(EventStatus eventStatus) {
                Root.this.editorInFocus = this;
            }
        };*/

        editorInFocus = primaryEditor;

        Frame editorSpace = new Frame("accent2");

        HorizontalPlacement editorSpacePlacement = new HorizontalPlacement(theme);
        editorSpace.setChildrenPlacement(editorSpacePlacement);

        editorSpacePlacement.add(primaryEditor, new UnitValue(0, UnitValue.Unit.AUTO));
        //editorSpacePlacement.add(secondaryEditor, new UnitValue(0, UnitValue.Unit.AUTO));

        Scrollbar scrollbar = new Scrollbar("primary", editorInFocus.getScrollController(), UnitValue.Direction.VERTICAL);

        placement.add(header,0,0,1,2);
        placement.add(editorSpace,1,0,1,1);
        placement.add(scrollbar, 1, 1, 1, 1);

        //core.resize(Size.fromDimension(this.getSize()));

        panel.setFocusTraversalKeysEnabled(false); // Stop taking away my TAB ://
        this.bindEvents();
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
                System.out.println("Save action performed");
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
                    String data = (String) Toolkit.getDefaultToolkit()
                            .getSystemClipboard().getData(DataFlavor.stringFlavor);

                    String[] formated_data = data.split("\n");

                    for(int i = 0;i < formated_data.length;i++) {
                        if (i == 0) {
                            editorInFocus.insertStringOnCursor(formated_data[i]);
                        }
                        else{
                            editorInFocus.getText().insertNewLine(formated_data[i], editorInFocus.getCursor().getY()+i);
                        }

                        if(i+1 >= formated_data.length){
                            editorInFocus.getCursor().setY(editorInFocus.getCursor().getY()+i+1);
                            editorInFocus.getCursor().upToLineEnd();
                        }
                    }
                }
                catch (Exception ex){

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
}
