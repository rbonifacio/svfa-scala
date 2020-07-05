package samples.callSiteMatch;

public class CallSiteMatch3 {
    public static void main(String args[]) {
        String s1 = sourceHost();
        String s2 = "abc";

        sinkHost(s1);   /* BAD */
        sinkHost(s2);   /* OK */
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) { }

    public static String sourceHost() {
        String a = source();
        return a;
    }

    public static void sinkHost(String a) {
        String b = a;
        sink(b);
    }
}
