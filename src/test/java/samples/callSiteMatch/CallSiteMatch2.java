package samples.callSiteMatch;

public class CallSiteMatch2 {
    public static void main(String args[]) {
        String s1 = source();
        String s2 = "abc";

        sinkHost(s1);   /* BAD */
        sinkHost(s2);   /* OK */
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) { }

    public static void sinkHost(String a) {
        String b = a;
        sink(b);
    }
}
