package App;

import AmbrosiaUI.Utility.AdvancedGraphics;
import AmbrosiaUI.Utility.FileUtil;
import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Utility.UnitValue;
import AmbrosiaUI.Widgets.Editors.TextEditor.Highlighting.Highlighter;
import AmbrosiaUI.Widgets.Frame;
import AmbrosiaUI.Widgets.Icons.Icon;
import AmbrosiaUI.Widgets.Label;
import AmbrosiaUI.Widgets.Placements.GridPlacement;
import AmbrosiaUI.Widgets.Placements.HorizontalPlacement;
import AmbrosiaUI.Widgets.Placements.VerticalPlacement;
import AmbrosiaUI.Widgets.Theme;
import AmbrosiaUI.Widgets.Window;

import java.io.File;
import java.lang.reflect.Executable;
import java.util.ArrayList;

public class ExtensionsManager extends Window {
    private Root root;

    private Frame extensionSpace;
    private GridPlacement corePlacement;
    private VerticalPlacement extensionDisplayPlacement;
    private ArrayList<Extension> loadedExtensions = new ArrayList<>();
    public ExtensionsManager(Theme theme, Root root) {
        super(theme);
        this.root = root;

        corePlacement = new GridPlacement(theme);
        corePlacement.setColumnTemplateFromString("auto");
        corePlacement.setRowTemplateFromString("auto");
        this.getCoreFrame().setChildrenPlacement(corePlacement);

        extensionSpace = new Frame("primary", 0);
        corePlacement.add(extensionSpace, 0,0,1,1);

        extensionDisplayPlacement = new VerticalPlacement(theme);
        extensionSpace.setChildrenPlacement(extensionDisplayPlacement);


        loadExtensionsFromPath(Config.extensionsPath);
        reloadExtensionsDisplay();
        update();
    }

    public void reloadExtensionsDisplay(){
        extensionDisplayPlacement.getChildren().clear();

        for (Extension extension: loadedExtensions){
            Frame temp = new Frame("secondary",2);
            extensionDisplayPlacement.add(temp, new UnitValue(60, UnitValue.Unit.PIXELS));

            HorizontalPlacement tempPlacement = new HorizontalPlacement(theme);
            temp.setChildrenPlacement(tempPlacement);

            Icon tempIcon = new Icon("secondary", "secondary", extension.getIcon());
            tempPlacement.add(tempIcon, new UnitValue(extension.getIcon().getWidth(), UnitValue.Unit.PIXELS));

            Frame descriptionNameSubPanel = new Frame("secondary", 0);
            tempPlacement.add(descriptionNameSubPanel, new UnitValue(0, UnitValue.Unit.AUTO));

            VerticalPlacement descriptionNameSubPlacement = new VerticalPlacement(theme);
            descriptionNameSubPanel.setChildrenPlacement(descriptionNameSubPlacement);

            Label tempLabelName = new Label(extension.getName(), "normal", 0, 0, 4);
            tempLabelName.setBackgroundColor("primary");
            tempLabelName.setForegroundColor("text2");
            tempLabelName.setHoverEffectDisabled(true);
            tempLabelName.setTextPlacement(AdvancedGraphics.Side.LEFT);

            Label tempLabelDescription = new Label(extension.getDescription(), "small", 0, 0, 4);
            tempLabelDescription.setBackgroundColor("primary");
            tempLabelDescription.setForegroundColor("text2");
            tempLabelDescription.setHoverEffectDisabled(true);
            tempLabelDescription.setTextPlacement(AdvancedGraphics.Side.LEFT);

            descriptionNameSubPlacement.add(tempLabelName, new UnitValue(0, UnitValue.Unit.AUTO));
            descriptionNameSubPlacement.add(tempLabelDescription, new UnitValue(0, UnitValue.Unit.AUTO));
        }
    }

    public void loadExtensionsFromPath(String directory){
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null){
            Logger.printWarning("No syntax highlighters loaded, folder is empty.");
            return;
        }

        for (File file : listOfFiles) {
            if(file.isDirectory()){
                File manifest = new File(FileUtil.joinPath(file.getAbsolutePath(), "manifest.txt"));

                if(manifest.exists()){
                    //System.out.println("File " + listOfFile.getName());
                    Extension nw = new Extension(file.getAbsolutePath(), theme);
                    nw.loadFromFile(manifest.getAbsolutePath());

                    loadedExtensions.add(nw);
                }
            }
        }
    }
}
