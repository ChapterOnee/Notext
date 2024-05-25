package AmbrosiaUI.Prompts;

import AmbrosiaUI.Widgets.Theme;
import AmbrosiaUI.Widgets.Window;

/**
 * Default prompt
 */
public abstract class Prompt {

    protected PromptResult result;
    protected final Window win;
    public Prompt(Theme theme, int width, int height) {
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
        win.show();
        win.hide();
        win.setSize(width,height);
    }

    public void ask(){
        win.show();
    }

    public void onSubmited(PromptResult result){

    }
}
