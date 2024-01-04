package Widgets.Prompts;

import Widgets.Core;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class Prompt<T> extends Core {
    T return_value = null;

    @Override
    public void onFrameLoad(){
        frame.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if(return_value != null){
                    onSubmit();
                }
            }
            @Override
            public void windowClosed(WindowEvent e) {
                //window is closed
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

        });
    }

    public void ask(){
        return_value = null;
        open();
    }

    public void onSubmit(){
        System.out.println(return_value);
    }
}
