package Widgets;

import Utility.*;
import Widgets.DropdownMenu.DropdownMenu;
import Widgets.DropdownMenu.DropdownMenuItem;
import Widgets.Placements.HorizontalPlacement;
import Widgets.Placements.VerticalPlacement;
import Widgets.TextEditor.EditorLine;
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
        VerticalPlacement placement = new VerticalPlacement(theme);
        core_frame.setChildrenPlacement(placement);

        Frame header = new Frame("secondary");

        HorizontalPlacement header_placement = new HorizontalPlacement(theme);
        header.setChildrenPlacement(header_placement);

        Button open_file = new Button("Open", "small", 2,0) {
            @Override
            public void onClicked(EventStatus eventStatus) {
                FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
                fd.setDirectory("C:\\");
                fd.setFile("*.*");
                fd.setVisible(true);

                String filename = fd.getDirectory() + fd.getFile();
                System.out.println(filename);
                editorInFocus.openFile(filename);

                eventStatus.setMouseDown(false);
            }
        };
        Button save_file = new Button("Save", "small", 2, 0) {
            @Override
            public void onClicked(EventStatus eventStatus) {
                editorInFocus.saveToCurrentlyOpenFile();
                editorInFocus.openFile(editorInFocus.getText().getCurrentFile());
            }
        };

        DropdownMenu menu = new DropdownMenu("File", "small",0,0, new Size(100,30));
        menu.setzIndex(1);

        save_file.setBorderModifier(new GraphicsBorderModifier(false,false,false,true));
        open_file.setBorderModifier(new GraphicsBorderModifier(false,false,false,true));

        header_placement.add(menu, new UnitValue(50, UnitValue.Unit.PIXELS));

        menu.addMenuItem(new DropdownMenuItem(open_file));
        menu.addMenuItem(new DropdownMenuItem(save_file));

        TextEditor primaryEditor = new TextEditor() {
            @Override
            public void onClicked(EventStatus eventStatus) {
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


        placement.add(header,new UnitValue(theme.getFontByName("small").getSize()+20, UnitValue.Unit.PIXELS));
        placement.add(editorSpace,new UnitValue(0, UnitValue.Unit.AUTO));

        //core.resize(Size.fromDimension(this.getSize()));

        this.setFocusTraversalKeysEnabled(false); // Stop taking away my TAB ://
        this.bindEvents();

        setFocusable(true);
    }

    public void bindEvents(){
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                //System.out.println(keyEvent.getExtendedKeyCode());

                switch (keyEvent.getKeyCode()){
                    case 38 -> editorInFocus.getCursor().up();
                    case 40 -> editorInFocus.getCursor().down();
                    case 37 -> editorInFocus.getCursor().left();
                    case 39 -> editorInFocus.getCursor().right();

                    /*
                        Enter, handle adding editor.getLines().
                    */
                    case 10 -> {
                        EditorLine current_line = editorInFocus.getLineUnderCursor();
                        String text = current_line.getText();

                        current_line.setText(text.substring(editorInFocus.getCursor().getX()));

                        editorInFocus.getText().insertNewLine(text.substring(0, editorInFocus.getCursor().getX()), editorInFocus.getCursor().getY());
                        editorInFocus.getCursor().down();
                        editorInFocus.getCursor().toLineStart();
                    }
                    /*
                        Backspace, handle removing characters.
                    */
                    case 8 -> {
                        EditorLine current_line = editorInFocus.getLineUnderCursor();

                        if(editorInFocus.getActiveSelections().size() > 0){
                            editorInFocus.getText().removeSelection(editorInFocus.getActiveSelections().get(0));
                            editorInFocus.clearSelections();
                        }

                        if (!editorInFocus.getCursor().canMove(new Position(-1, 0))) {
                            if(editorInFocus.getCursor().canMoveOnLine(-1)){
                                String text = current_line.getText();
                                editorInFocus.getText().removeLine(current_line);
                                editorInFocus.getCursor().upToLineEnd();
                                editorInFocus.getText().appendTextAt(text,editorInFocus.getCursor().getY());
                            }
                        }
                        else {
                            editorInFocus.getText().removeCharAt(editorInFocus.getCursor().getX() - 1, editorInFocus.getCursor().getY());
                            editorInFocus.getCursor().left();
                        }
                    }
                    /*
                        Delete
                     */
                    case 127 -> {
                        if(editorInFocus.getCursor().getCurrrentCharsUnderCursor().charAt(1) != 0){
                            editorInFocus.getText().removeCharAt(editorInFocus.getCursor().getX(),editorInFocus.getCursor().getY());
                        }

                        //theme.setAccentColor(theme.getColorByName("accent").darker());
                    }
                    /*
                        Tab
                    */

                    case 9 -> editorInFocus.insertStringOnCursor("    ");

                    /*
                        Shift to select
                     */
                    case 16 -> {
                        //editor.startSelection();
                    }
                    case 17, 18 -> {

                    }
                    default -> {
                        char c = keyEvent.getKeyChar();
                        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );

                        if((!Character.isISOControl(c)) &&
                                c != KeyEvent.CHAR_UNDEFINED &&
                                block != null &&
                                block != Character.UnicodeBlock.SPECIALS
                        ) {
                            editorInFocus.insertStringOnCursor(keyEvent.getKeyChar() + "");
                        }
                    }

                }
                update();
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case 16 -> {
                        editorInFocus.endSelection();
                    }
                    default -> {}
                }
                update();
            }
        });
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                editorInFocus.startSelection();
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                editorInFocus.clearSelections();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //editor.startSelection();
                update();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                editorInFocus.endSelection();
                update();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                editorInFocus.setScrollY(Math.max(0, editorInFocus.getScroll().y + e.getWheelRotation()*25));
                Root.this.update();
            }
        });

        Keybind save = new Keybind("Save", this, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)) {
            @Override
            public void activated(ActionEvent e) {
                System.out.println("Save action performed");
            }
        };

        Keybind copy = new Keybind("Copy", this, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK)){
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

        Keybind paste = new Keybind("Paste", this, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK)){
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

        Keybind revert = new Keybind("Revert", this, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK)){
            @Override
            public void activated(ActionEvent e) {
                editorInFocus.getText().revert();
            }
        };
    }
}
