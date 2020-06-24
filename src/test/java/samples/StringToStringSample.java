package samples;

public class StringToStringSample {

    public static void main(String args[]) {
        String s = source();
 //       sink(s);
        sink(s.toLowerCase());
    }

    private static String source() {
        return "secret";
    }

    private static void sink(String s) { }
}