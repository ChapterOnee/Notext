package AmbrosiaUI.Widgets.TextEditor;

import AmbrosiaUI.Widgets.Placements.ScrollController;

public interface EditorLike {
    String getCurrentFile();
    void setCurrentFile(String filename);
    boolean hasFile();

    void revert();
    void clear();

    void saveToCurrentlyOpenFile();

    void openFile(String filename);

    ScrollController getScrollController();
}
