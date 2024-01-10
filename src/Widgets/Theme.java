package Widgets;

import Utility.FileLoader;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Theme extends FileLoader {
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
    }

    public Color getColorByName(String name){
        if(colors.containsKey(name)){
            return colors.get(name);
        }

        System.out.println("Color: '" + name + "' not found, setting to default instead.");
        colors.put(name, colors.get("default"));

        return colors.get(name);
    }
    public Font getFontByName(String name){
        if(fonts.containsKey(name)){
            return fonts.get(name);
        }

        System.out.println("Font: '" + name + "' not found, setting to default instead.");
        fonts.put(name, fonts.get("default"));

        return fonts.get(name);
    }

    private void loadFont(String filename, String name){
        File font_file = new File(filename);
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);
            custom_fonts.put(name, font);
        } catch (FontFormatException | IOException e) {
            System.out.println("Failed to load font '" + filename + "': " + e);
        }
    }

    @Override
    public void handleTag(String tag, String name, String args) {
        String[] arguments = args.split(",");
        switch (tag){
            case "font" -> {
                System.out.println(Arrays.toString(arguments));
                if(arguments.length < 2 || !arguments[1].matches("\\d+")){
                    System.out.println("Invalid arguments for font.");
                    return;
                }

                String fontName = arguments[0].strip();
                int fontSize = Integer.parseInt(arguments[1]);

                if(custom_fonts.containsKey(fontName)){
                    System.out.println(fontSize);
                    fonts.put(name, custom_fonts.get(fontName).deriveFont((float) fontSize));
                }
                else {
                    fonts.put(name, new Font(fontName, Font.PLAIN, fontSize));
                }
            }
            case "color" -> {
                if(arguments.length < 3
                        || !arguments[0].matches("\\d+")
                        || !arguments[1].matches("\\d+")
                        || !arguments[2].matches("\\d+")
                ){
                    System.out.println("Invalid arguments for color.");
                    return;
                }

                colors.put(name, new Color(
                        Integer.parseInt(arguments[0]),
                        Integer.parseInt(arguments[1]),
                        Integer.parseInt(arguments[2])
                ));
            }
            case "load_font" -> {
                if(arguments.length < 1){
                    System.out.println("Invalid arguments for load_font.");
                    return;
                }

                loadFont(name, arguments[0].strip());
            }
            default -> {}
        }
    }
}
