import AegisLang.ASTreeNode;
import AegisLang.Interpreter;
import AegisLang.Lexer;
import AegisLang.Parser;
import AmbrosiaUI.Prompts.FilePrompt;
import AmbrosiaUI.Prompts.Prompt;
import AmbrosiaUI.Prompts.PromptResult;
import AmbrosiaUI.Widgets.Icons.PathImage;
import App.Root;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Root root = new Root();
        root.show();

        /*Interpreter it = new Interpreter();


        try(BufferedReader bf = new BufferedReader(new FileReader("AegisCode/main.ag"))){
            StringBuilder allData = new StringBuilder();

            while (bf.ready()){
                allData.append(bf.readLine());
            }

            it.execute(allData.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        //root.openFile("testFiles/test.txt");

        //PathImage img = new PathImage("icons/folder.pimg");
        //img.saveToFile("test.pimg");

        //Window w = new Window();
        //w.open();
    }
}
