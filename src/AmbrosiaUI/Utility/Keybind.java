package AmbrosiaUI.Utility;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Keybind {

    static ArrayList<String> names_in_use = new ArrayList<>();
    private String name;

    public Keybind(String name, JPanel bound_element, KeyStroke keybinds) {
        if(names_in_use.contains(name)){
            Logger.printWarning("An already existing keybind was overwritten '" + name + "'.");
        }
        this.name = name;

        AbstractAction action = new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Keybind.this.activated(e);
            }
        };

        bound_element.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keybinds, name);
        bound_element.getActionMap().put(name, action);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void activated(ActionEvent e){

    }
}
