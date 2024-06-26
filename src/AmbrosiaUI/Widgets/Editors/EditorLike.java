package AmbrosiaUI.Widgets.Editors;

import AmbrosiaUI.Widgets.Placements.ScrollController;

/**
 * Anything that could be treated as an editor and all it has to have
 */
public interface EditorLike {
    String getCurrentFile();
    void setCurrentFile(String filename);
    boolean hasFile();

    void onCurrentFileChanged();

    void revert();
    void clear();

    void saveToCurrentlyOpenFile();

    void openFile(String filename);

    String getAllowedFiles();

    ScrollController getScrollController();

    boolean dontAutoScroll();

    void reload();
}
