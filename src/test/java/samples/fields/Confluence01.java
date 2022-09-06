package samples.fields;

public class Confluence01 {
    private Integer words;

    public static void main(String args[])  {
        Confluence01 o = new Confluence01();
        o.words = o.readPassword();
        share(o.words);
    }

    private Integer readPassword() {
        return new Integer(100);
    }

    private static void share(int words) { }
}
