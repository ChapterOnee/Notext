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

public class Main {
    public static void main(String[] args) {
        //Root root = new Root();
        //root.show();

        Lexer lex = new Lexer();
        Parser parser = new Parser();

        try(BufferedReader bf = new BufferedReader(new FileReader("AegisCode/main.ag"))){
            StringBuilder allData = new StringBuilder();

            while (bf.ready()){
                allData.append(bf.readLine());
            }


            parser.parseAbstractSyntaxTrees(lex.lexData(allData.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //root.openFile("testFiles/test.txt");

        //PathImage img = new PathImage("icons/folder.pimg");
        //img.saveToFile("test.pimg");

        //Window w = new Window();
        //w.open();
    }
}
