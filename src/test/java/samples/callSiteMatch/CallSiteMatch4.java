package samples.callSiteMatch;

public class CallSiteMatch4 {
    public static void main(String args[]) {
        String s1 = sourceHost();
        String s2 = "abc";
        String s3 = fakeUpperCase(s1);
        String s4 = fakeUpperCase(s2);

        sinkHost(s1);   /* BAD */
        sinkHost(s2);   /* OK */
        sinkHost(s3);   /* BAD */
        sinkHost(s4);   /* OK */
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

    public static String fakeUpperCase(String a) {
        String b = a;
        String c = b;
        return c;
    }
}
