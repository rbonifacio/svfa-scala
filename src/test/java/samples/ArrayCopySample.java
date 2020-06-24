package samples;

import java.util.Arrays;

public class ArrayCopySample {

    public static void main(String args[]) {
        String s1 = new String(source());
        String s2 = ":" + s1 + ":";
        System.out.println(s2);
        char[] s = source();
        char[] foo = Arrays.copyOfRange(s, 0, s.length);
        sink(foo);
    }

    private static class Arrays {
        public static char[] copyOfRange(char[] src, int begin, int end) {
            char[] copy = new char[end - begin];
            System.arraycopy(src, begin, copy, 0, end-begin);
            return copy;
        }
    }

    private static char[] source() {
        return "secret".toCharArray();
    }

    private static void sink(char[] s) {}

}
