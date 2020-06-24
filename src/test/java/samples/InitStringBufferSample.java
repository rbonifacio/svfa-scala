package samples;

public class InitStringBufferSample {

    public static void main(String args[]) {
        String s1 = source();
        String s2 = s1;
        String s3 = s2;
        String s4 = s3;
        StringBuffer b1 = new StringBuffer(s4);
        StringBuffer b3 = b1;
        String s5 = b3.toString();
        String s6 = s5;

        sink(s6);    // BAD
    }

    private static String source() {
        return "secret";
    }

    private static void sink(String s) {

    }
}
