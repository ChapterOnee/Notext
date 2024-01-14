package AmbrosiaUI.Prompts;

import AmbrosiaUI.Widgets.Window;

public abstract class Prompt {

    protected final Window win = new Window(){
        @Override
        public void close() {
            super.close();
            onSubmited();
        }
    };

    public void ask(){
        win.show();
    }

    public void onSubmited(){

    }
}
