package AmbrosiaUI.Widgets.Icons;

import AmbrosiaUI.Utility.FileInterpreter.FileInterpreter;
import AmbrosiaUI.Utility.FileInterpreter.InterpretedCommand;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Widgets.Icons.PathOperations.*;
import AmbrosiaUI.Widgets.Theme;

import java.awt.*;
import java.util.ArrayList;

public class PathImage extends FileInterpreter {
    private ArrayList<PathDrawable> oparations = new ArrayList<>();

    private Size size = new Size(0,0);
    private Theme Theme;

    public PathImage(Size size) {
        this.size = size;
        initFileloaderCommands();
    }
    public PathImage(String path){
        initFileloaderCommands();
        loadFromFile(path);
    }

    private void initFileloaderCommands(){
        this.addCommand(new InterpretedCommand("move", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT}){
            @Override
            public void execute(ArrayList<String> arguments) {
                PathImage.this.oparations.add(new PathMove(
                        Integer.parseInt(arguments.get(0)),
                        Integer.parseInt(arguments.get(1))
                ));
            }
        });

        this.addCommand(new InterpretedCommand("lineTo", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.STRING, InterpretedCommand.ArgumentType.INT}){
            @Override
            public void execute(ArrayList<String> arguments) {
                PathImage.this.oparations.add(new PathLine(
                        Integer.parseInt(arguments.get(0)),
                        Integer.parseInt(arguments.get(1)),
                        arguments.get(2),
                        Integer.parseInt(arguments.get(3))
                ));
            }
        });

        this.addCommand(new InterpretedCommand("rect", new InterpretedCommand.ArgumentType[]{
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // Rectangle
                InterpretedCommand.ArgumentType.STRING, InterpretedCommand.ArgumentType.INT // color, width
        }){
            @Override
            public void execute(ArrayList<String> arguments) {
                PathImage.this.oparations.add(new PathRectangle(
                        arguments.get(4),
                        new Rectangle(
                                Integer.parseInt(arguments.get(0)),
                                Integer.parseInt(arguments.get(1)),
                                Integer.parseInt(arguments.get(2)),
                                Integer.parseInt(arguments.get(3))
                        ),
                        Integer.parseInt(arguments.get(5))
                ));
            }
        });

        this.addCommand(new InterpretedCommand("fillRect", new InterpretedCommand.ArgumentType[]{
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // Rectangle
                InterpretedCommand.ArgumentType.STRING // color
        }){
            @Override
            public void execute(ArrayList<String> arguments) {
                PathImage.this.oparations.add(new PathFillRectangle(
                        arguments.get(4),
                        new Rectangle(
                                Integer.parseInt(arguments.get(0)),
                                Integer.parseInt(arguments.get(1)),
                                Integer.parseInt(arguments.get(2)),
                                Integer.parseInt(arguments.get(3))
                        )
                ));
            }
        });

        this.addCommand(new InterpretedCommand("fillPoly4", new InterpretedCommand.ArgumentType[]{
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // p1
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // p2
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // p3
                InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT, // p4
                InterpretedCommand.ArgumentType.STRING // color
        }){
            @Override
            public void execute(ArrayList<String> arguments) {
                PathImage.this.oparations.add(new PathFillPoly(
                        new Position(Integer.parseInt(arguments.get(0)),Integer.parseInt(arguments.get(1))),
                        new Position(Integer.parseInt(arguments.get(2)),Integer.parseInt(arguments.get(3))),
                        new Position(Integer.parseInt(arguments.get(4)),Integer.parseInt(arguments.get(5))),
                        new Position(Integer.parseInt(arguments.get(6)),Integer.parseInt(arguments.get(7))),
                        arguments.get(8)
                ));
            }
        });

        this.addCommand(new InterpretedCommand("size", new InterpretedCommand.ArgumentType[]{InterpretedCommand.ArgumentType.INT, InterpretedCommand.ArgumentType.INT}){
            @Override
            public void execute(ArrayList<String> arguments) {
                PathImage.this.setSize(new Size(
                        Integer.parseInt(arguments.get(0)),
                        Integer.parseInt(arguments.get(1))
                ));
            }
        });
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getWidth(){
        return size.width;
    }
    public int getHeight(){
        return size.height;
    }

    public void draw(Graphics2D g2, Position startPosition){
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if(Theme == null){
            return;
        }

        //g2.setClip(startPosition.x, startPosition.y, size.width,size.height);
        Position position = new Position(startPosition.x,startPosition.y);

        for(PathDrawable operation: oparations){
            operation.draw(g2,position, Theme);
        }
    }

    public Theme getTheme() {
        return Theme;
    }

    public void setTheme(Theme Theme) {
        this.Theme = Theme;
    }

    public void add(PathDrawable drawable){
        oparations.add(drawable);
    }
}
