package AmbrosiaUI.Widgets.Editors.TextEditor;

import AmbrosiaUI.Utility.Position;

import java.util.ArrayList;

public class Selection {
    private Cursor from;
    private Cursor to;

    public Selection(Cursor from, Cursor to) {
        this.from = from;
        this.to = to;
    }
    public void reorganize(){
        int temp = 0;
        if(from.getY() > to.getY()){
            temp = from.getY();
            from.setY(to.getY()); // Swap y if from is greater
            to.setY(temp);

            temp = from.getX();
            from.setX(to.getX());
            to.setX(temp);
        }

        if(from.getY() == to.getY() && from.getX() > to.getX()){
            temp = from.getX();
            from.setX(to.getX()); // Swap x if from is greater
            to.setX(temp);
        }
    }

    public Selection getReorganized(){
        if(from == null || to == null){
            return new Selection(null,null);
        }

        Cursor from2 = new Cursor(new Position(from.getX(), from.getY()));
        from2.setCurrentTextLines(from.getCurrentLines());

        Cursor to2 = new Cursor(new Position(to.getX(), to.getY()));
        to2.setCurrentTextLines(from.getCurrentLines());

        Selection out = new Selection(from2,to2);
        out.reorganize();

        return out;
    }

    public boolean valid(){
        try {
            this.getSelectedContentUnsafe();
            return true;
        }catch (IndexOutOfBoundsException ignored) {
            return false;
        }
    }

    public ArrayList<String> getSelectedContent(){
        if(!this.valid()){
            return new ArrayList<>();
        }
        return this.getSelectedContentUnsafe();
    }

    public String getSelectedContentFormated(){
        return String.join("\n",this.getSelectedContent());
    }

    public ArrayList<String> getSelectedContentUnsafe() throws IndexOutOfBoundsException{
        ArrayList<EditorLine> lines = from.getCurrentLines();
        ArrayList<String> output = new ArrayList<>();

        // First line
        if (from.getY() == to.getY()) {
            output.add(
                    lines.get(from.getY()).getText().substring(from.getX(), to.getX())
            );
            return output;
        } else {
            String line = lines.get(from.getY()).getText();
            output.add(line.substring(from.getX()));
        }

        // Lines in between
        for (int y = from.getY() + 1; y < to.getY(); y++) {
            output.add(lines.get(y).getText());
        }

        // Last line
        if (from.getY() != to.getY()) {
            String line = lines.get(to.getY()).getText();
            output.add(line.substring(0, to.getX()));
        }

        return output;
    }

    public Cursor getFrom() {
        return from;
    }

    public void setFrom(Cursor from) {
        this.from = from;
    }

    public Cursor getTo() {
        return to;
    }

    public void setTo(Cursor to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Selection{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
