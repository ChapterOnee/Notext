import Widgets.Prompts.ConfirmationPrompt;
import Widgets.Root;

public class Main {
    public static void main(String[] args) {
        //Root root = new Root();
        //root.open();

        ConfirmationPrompt cop = new ConfirmationPrompt(){
            @Override
            public void onSubmit() {
                Root root = new Root();
                root.open();
            }
        };
        cop.ask();
    }
}