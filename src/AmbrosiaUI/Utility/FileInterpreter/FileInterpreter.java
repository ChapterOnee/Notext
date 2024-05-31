package AmbrosiaUI.Utility.FileInterpreter;

import AmbrosiaUI.Utility.Logger;
import AmbrosiaUI.Widgets.Editors.TextEditor.EditorLine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The code decoder for configuration files, theme files, syntax files and others, this is the core, its extensions only build on its functinality
 *
 */
public class FileInterpreter {
    private final HashMap<String, InterpretedCommand> commands = new HashMap<>();

    /**
     * Loads from a file considering the presets
     * @param filename A filename or the full path for loading
     */
    public void loadFromFile(String filename){
        try {
            InputStream stream = getClass().getResourceAsStream("/" + filename);
            BufferedReader reader;
            if(stream !=  null){
                reader = new BufferedReader(new InputStreamReader(stream));
            }
            else{
                reader = new BufferedReader(new FileReader(filename));
            }


            while (reader.ready()) {
                String data = reader.readLine();

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
            reader.close();

        } catch (FileNotFoundException e) {
            Logger.printError("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
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

    /**
     * Add a processed command
     * @param command the command
     */
    public void addCommand(InterpretedCommand command){
        this.commands.put(command.getName(), command);
    }
    /*
    public void handleTag(String tag, String name, String arguments){

    }*/
}
