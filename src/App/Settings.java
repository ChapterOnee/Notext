package App;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Widgets.Editors.TextEditor.Hinter.KeywordDictionary;
import AmbrosiaUI.Widgets.Label;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.SelectBox.SelectBox;
import AmbrosiaUI.Widgets.SelectBox.SelectBoxOption;
import AmbrosiaUI.Widgets.Theme;
import AmbrosiaUI.Widgets.Widget;
import AmbrosiaUI.Widgets.Window;

import java.io.File;
import java.net.URL;

/**
 * Settings, currently can only set themes
 */
public class Settings extends Window {

    private Root root;
    public Settings(Theme theme, Root root) {
        super(theme);
        this.root = root;

        GridPlacement grid = new GridPlacement(theme);
        grid.setColumnTemplateFromString("200px 100px auto");
        grid.setRowTemplateFromString("40px 40px auto");

        SelectBox selection = new SelectBox("normal", 0,2,4);
        selection.setItemSize(new Size(200,40));


        for (String path: Config.themes) {
            String nm = path.split("\\.")[0];
            int w = Widget.getStringWidth(nm, theme.getFontByName(selection.getFont()));

            if(w > selection.getItemSize().width){
                selection.getItemSize().width = w;
            }

            selection.addOption(new SelectBoxOption(nm){
                @Override
                public void onSelected() {
                    theme.loadFromFile(Config.themesPath + "/" + path);
                    Window.reloadAllWindows();
                }
            });


            selection.selectLast();
        }


        this.getCoreFrame().setChildrenPlacement(grid);

        Label themeLabel = new Label("Theme:", "normal", 0,0,4);
        themeLabel.setTextPlacement(AdvancedGraphics.Side.LEFT);
        themeLabel.setForegroundColor("text2");
        themeLabel.setBackgroundColor("primary");
        themeLabel.setHoverEffectDisabled(true);

        grid.add(themeLabel,0,0,1,1);
        grid.add(selection,0,1,1,1);
    }
}
