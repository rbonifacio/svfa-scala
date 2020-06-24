package samples;

public class InitStringFromCharArraySample {

    public static void main(String args[]) {
        char[] s1 = source();
        String s2 = new String(s1);
        sink(s2);
    }

    private static char[] source() {
        return "secret".toCharArray();
    }

    private static void sink(String args) {

    }
}
