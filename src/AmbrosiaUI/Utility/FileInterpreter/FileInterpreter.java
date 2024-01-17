package AmbrosiaUI.Utility.FileInterpreter;

import AmbrosiaUI.Utility.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileInterpreter {

    private final HashMap<String, InterpretedCommand> commands = new HashMap<>();
    public void loadFromFile(String filename){
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                /*
                    Tag template:
                        color primary 255,255,255
                        font small Monospaced,16
                        [font|color] [name] [arguments,...]
                 */

                // Split data
                ArrayList<String> split_data = new ArrayList<String>();
                Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(data);
                while (m.find())
                    split_data.add(m.group(1));

                // Check if the line is valid
                if(split_data.size() < 1){
                    continue;
                }

                String command = split_data.get(0).strip();
                ArrayList<String> arguments = new ArrayList<>(split_data.subList(1,split_data.size()));

                for(int i = 0; i < arguments.size();i++){
                    arguments.set(i, clean(arguments.get(i)));
                }

                if(commands.containsKey(command)){
                    if(commands.get(command).validateArguments(arguments)) {
                        commands.get(command).execute(arguments);
                    }
                    else{
                        Logger.printError(this.getClass().getName() + "::FileLoader -> Invalid arguments for '"+command+"' when loading from file!");
                    }
                }

                /*String tag = split_data.get(0).strip();
                String name = split_data.get(1).strip();
                String arguments = split_data.get(2).strip();

                tag = clean(tag);
                name = clean(name);
                arguments = clean(arguments);


                this.handleTag(tag, name, arguments);*/

            }
            myReader.close();

        } catch (FileNotFoundException e) {
            Logger.printError("An error occurred.");
            e.printStackTrace();
        }
    }

    private String clean(String st){
        if(st.startsWith("\"") && st.endsWith("\"")){
            st = st .substring(1,st.length()-1);
        }
        return st;
    }

    public void addCommand(InterpretedCommand command){
        this.commands.put(command.getName(), command);
    }
    /*
    public void handleTag(String tag, String name, String arguments){

    }*/
}
