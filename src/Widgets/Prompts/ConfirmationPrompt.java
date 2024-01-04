package Widgets.Prompts;

import Utility.EventStatus;
import Utility.UnitValue;
import Widgets.Button;
import Widgets.Core;
import Widgets.Frame;
import Widgets.Placements.HorizontalPlacement;
import Widgets.Placements.VerticalPlacement;

import java.awt.event.WindowEvent;
import java.util.HashMap;

public class ConfirmationPrompt extends Prompt<Boolean> {
    public ConfirmationPrompt() {
        VerticalPlacement main_placement = new VerticalPlacement(theme);
        core_frame.setChildrenPlacement(main_placement);


        Frame body = new Frame("primary");
        Frame buttons = new Frame("secondary");

        HorizontalPlacement buttons_placement = new HorizontalPlacement(theme);
        buttons.setChildrenPlacement(buttons_placement);

        Button confirm = new Button("Confirm","normal",0,1){
            @Override
            public void onClicked(EventStatus eventStatus) {
                return_value =  true;
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        };
        Button cancel = new Button("Cancel","normal",0,1){
            @Override
            public void onClicked(EventStatus eventStatus) {
                return_value =  false;
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        };

        buttons_placement.add(new Frame("primary"), new UnitValue(10, UnitValue.Unit.PIXELS));
        buttons_placement.add(confirm, new UnitValue(80, UnitValue.Unit.PIXELS));
        buttons_placement.add(new Frame("primary"), new UnitValue(0, UnitValue.Unit.AUTO));
        buttons_placement.add(cancel, new UnitValue(80, UnitValue.Unit.PIXELS));
        buttons_placement.add(new Frame("primary"), new UnitValue(10, UnitValue.Unit.PIXELS));

        main_placement.add(body, new UnitValue(0, UnitValue.Unit.AUTO));
        main_placement.add(buttons, new UnitValue(30, UnitValue.Unit.PIXELS));
        main_placement.add(new Frame("primary"), new UnitValue(10, UnitValue.Unit.PIXELS));
    }

    @Override
    public void onSubmit() {
        System.out.println(return_value);
    }
}
