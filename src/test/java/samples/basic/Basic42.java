package samples.basic;

import br.unb.cic.graph.Sink;

public class Basic42 {

    public static void main(String args[]) {
        Object s1 = source();
        sink(s1.toString());
//        sink(s1.toString());
//        String s2 = "abc";
//        String s3 = s1.toUpperCase();
//        String s4 = s2.toUpperCase(); // not context sensitive.
//
//        sink(s3);           /* BAD */
//        sink(s1 + ";");   /* BAD */
//        sink(s4);           /* OK */
    }

    public static String source() {
        return "secret";
    }

    public static void sink(Object s) {

    }
}
