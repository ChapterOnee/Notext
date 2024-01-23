package AmbrosiaUI.Widgets.SelectBox;

public class SelectBoxOption {
    protected String text;
    protected int index;

    public SelectBoxOption(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void onSelected(){

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
