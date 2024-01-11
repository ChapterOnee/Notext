import App.Root;

public class Main {
    public static void main(String[] args) {
        Root root = new Root();
        root.open();
        root.openFile("testFiles/test.txt");

        //Window w = new Window();
        //w.open();
    }
}