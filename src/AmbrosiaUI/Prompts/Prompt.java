package AmbrosiaUI.Prompts;

import AmbrosiaUI.Widgets.Theme;
import AmbrosiaUI.Widgets.Window;

public abstract class Prompt {

    protected PromptResult result;
    protected final Window win;
    public Prompt(Theme theme) {
        win = new Window(theme){
            @Override
            public void close() {
                super.close();
                if(result == null){
                    return;
                }
                onSubmited(result);
            }
        };
    }

    public void ask(){
        win.show();
    }

    public void onSubmited(PromptResult result){

    }
}
