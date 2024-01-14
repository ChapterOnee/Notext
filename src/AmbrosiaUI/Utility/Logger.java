package AmbrosiaUI.Utility;

public class Logger {

    private static final String resetCode = "\033[0m";

    public static void printError(String text){
        System.out.println(getColored("Error: " + text,200,0,0));
    }
    public static void printWarning(String text){
        System.out.println(getColored("Warning: " + text,200,200,0));
    }
    public static void printLine(String text){
        System.out.println(text);
    }
    public static void print(String text){
        System.out.print(text);
    }

    private static String getColored(String text, int r, int g, int b){
        return getForegroundColorRGB(r,g,b) + text + resetCode;
    }

    private static String getForegroundColorRGB(int r, int g, int b) {
        return "\033[38;2;" + r + ";" + g + ";" + b + "m";
    }
    private static String getBackgroundColorRGB(int r, int g, int b) {
        return "\033[48;2;" + r + ";" + g + ";" + b + "m";
    }
}
