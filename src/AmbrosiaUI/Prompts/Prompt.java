package AmbrosiaUI.Prompts;

import AmbrosiaUI.Widgets.Window;

public abstract class Prompt {
    public void ask(){
        Window win = new Window(){
            @Override
            public void close() {
                super.close();
                onSubmited();
            }
        };

        win.show();
    }

    public void onSubmited(){

    }
}
