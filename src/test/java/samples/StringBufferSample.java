package samples;

public class StringBufferSample {

    public static void main(String args[]) {
        String s = source();
        String abc = abc();
        StringBuffer buffer = new StringBuffer(abc);
        buffer.append(s);
        sink(buffer.toString());
    }

    private static String abc() { return "abc"; }

    private static String source() {
        return "secret";
    }

    private static void sink(String s) {

    }
}
