package samples.fields;

public class Confluence04 {
    private int words;
    private Integer spaces;

    public int main(String args[]) {
        Confluence04 o = new Confluence04();
        o.countWhiteSpaces();
        o.countWords();
        return o.words + o.spaces;                   // SINK   (RIGHT)
    }

    private void countWhiteSpaces() {
        spaces = new Integer(20);              // SOURCE (LEFT)
    }

    private void countWords() {
        words = new Integer(5);
    }
}