package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.FileInterpreter.FileInterpreter;
import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Theme extends FileInterpreter {
    /*
    private Color primaryColor = new Color(20,20,20);
    private Color secondaryColor = new Color(50,50,50);
    private Color accentColor = new Color(200,100,100);
    private Color accent2Color = new Color(100,100,255);
    private Color text1Color = new Color(220,220,220);
    private Color text2Color = new Color(0,0,0);*/

    private HashMap<String, Color> colors = new HashMap<>();
    private HashMap<String, Font> fonts = new HashMap<>();

    private HashMap<String, Font>  custom_fonts = new HashMap<>();

    /*private Font font = new Font("Monospaced", Font.PLAIN, 16);
    private Font fontSmall = new Font("Monospaced", Font.PLAIN, 14);
    private Font fontBig = new Font("Monospaced", Font.PLAIN, 28);*/

    public Theme(){
        colors.put("default" , new Color(20,20,20));
        fonts.put("default", new Font("Monospaced",Font.PLAIN,16));

        //colors.put("primary",new Color(20,20,20));
        //colors.put("secondary", new Color(50,50,50));
        //colors.put("text1", new Color(220,220,220));
        //colors.put("accent",new Color(200,100,100));

        this.addCommand(new InterpretedCommand("font", new InterpretedCommand.ArgumentType[]{
                InterpretedCommand.ArgumentType.STRING, InterpretedCommand.ArgumentType.STRING, InterpretedCommand.ArgumentType.INT}){
            @Override
            public void execute(ArrayList<String> arguments) {
                String fontName = arguments.get(1).strip();
                int fontSize = Integer.parseInt(arguments.get(2));

                if(custom_fonts.containsKey(fontName)){
                    //System.out.println(fontSize);
                    fonts.put(arguments.get(0), custom_fonts.get(fontName).deriveFont((float) fontSize));
                }
                else {
                    fonts.put(arguments.get(0), new Font(fontName, Font.PLAIN, fontSize));
                }
            }
        });

        this.addCommand(new InterpretedCommand("color", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.STRING,InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT}){
            @Override
            public void execute(ArrayList<String> arguments) {
                colors.put(arguments.get(0), new Color(
                        Integer.parseInt(arguments.get(1)),
                        Integer.parseInt(arguments.get(2)),
                        Integer.parseInt(arguments.get(3))
                ));
            }
        });

        this.addCommand(new InterpretedCommand("load_font", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.STRING, InterpretedCommand.ArgumentType.STRING}){
            @Override
            public void execute(ArrayList<String> arguments) {
                loadFont(arguments.get(0), arguments.get(1).strip());
            }
        });
    }

    public Color getColorByName(String name){
        if(colors.containsKey(name)){
            return colors.get(name);
        }

        Logger.printWarning("Color: '" + name + "' not found, setting to default instead.");
        colors.put(name, colors.get("default"));

        return colors.get(name);
    }
    public Font getFontByName(String name){
        if(fonts.containsKey(name)){
            return fonts.get(name);
        }

        Logger.printWarning("Font: '" + name + "' not found, setting to default instead.");
        fonts.put(name, fonts.get("default"));

        return fonts.get(name);
    }

    private void loadFont(String filename, String name){
        File font_file = new File(filename);
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);
            custom_fonts.put(name, font);
        } catch (FontFormatException | IOException e) {
            Logger.printError("Failed to load font '" + filename + "': " + e);
        }
    }

    public void setColor(String name, Color color){
        colors.put(name,color);
    }
}
