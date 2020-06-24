package samples;

public class StringConcatSample {

    public static void main(String args[]) {
        String s1 = source();
        String s2 = ":" + s1 + ":";

        sink(s1);
        sink(s2);
    }
    private static String source() {
        return "secret";
    }

    private static void sink(String s) {}
}
