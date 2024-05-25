package AmbrosiaUI.Utility.FileInterpreter;

import java.util.ArrayList;

/**
 * A segment of a configuration file for detection, predefines name and arguments of a single command
 */
public class InterpretedCommand {

    private String name;

    public enum ArgumentType{
        STRING,
        INT
    }
    private ArgumentType[] arguments;

    public InterpretedCommand(String name, ArgumentType[] arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public boolean validateArguments(ArrayList<String> args){
        if(args.size() != arguments.length){
            return false;
        }

        boolean valid = true;
        for(int i = 0;i < args.size() && valid;i++){
            switch (arguments[i]){
                case STRING -> {

                }
                case INT -> {
                    if(!args.get(i).matches("-?\\d+")){
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void execute(ArrayList<String> arguments){

    }
}
