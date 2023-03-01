package samples.fields;

public class Confluence02 {
    private int words;

    public static void main(String args[])  {
        Confluence02 o = new Confluence02();
        o.words = o.readPassword();
        share(o.words);
    }

    private int readPassword() {
        return 100;
    }

    private static void share(int words) { }
}
