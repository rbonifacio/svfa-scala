package samples;

public class StringConcatSample {

    public static void main(String args[]) {
        String s1 = source();
        String s2 = s1.toUpperCase();
        String s3 = s2.concat(";");
        String s4 = s3.replace(';', '.');
        String s5 = ":" + s4 + ":";
        String s6 = s5.substring(s5.length() - 1);


        sink(s1);
        sink(s2);
        sink(s3);
        sink(s4);
        sink(s5);
        sink(s6);
    }
    private static String source() {
        return "secret";
    }

    private static void sink(String s) {}
}
