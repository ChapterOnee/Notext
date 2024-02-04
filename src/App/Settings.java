package App;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Widgets.Label;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.SelectBox.SelectBox;
import AmbrosiaUI.Widgets.SelectBox.SelectBoxOption;
import AmbrosiaUI.Widgets.Theme;
import AmbrosiaUI.Widgets.Widget;
import AmbrosiaUI.Widgets.Window;

import java.io.File;

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

        File folder = new File(Config.themesPath);
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
