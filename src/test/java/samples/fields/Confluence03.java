package samples.fields;

public class Confluence03 {
    private String text;
    private Integer words;
    private Integer spaces;

    public static void main(String args[]) {
        Confluence03 o = new Confluence03();
        o.countDupWhitespace();
        o.countComments();
        o.countDupWords();
        Integer y = o.words;
        o.share(y);                        // SINK
    }

    private void countDupWords() {
        words = 2;
    }

    private void countComments() {
        share(words);
    }

    private void countDupWhitespace() {
        words = secret();                    // SOURCE
    }

    private void share(Integer ws) {
    }

    private Integer secret() {
        return new Integer(10);
    }
}