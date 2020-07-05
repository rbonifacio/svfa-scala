package samples.callSiteMatch;

public class CallSiteMatch5 {
    public static void main(String args[]) {
        String s1 = source();
        String s2 = "abc";
        String s3 = fakeUpperCase(s1);
        String s4 = fakeUpperCase(s2);

        sink(s1);   /* BAD */
        sink(s3);   /* BAD */
        sink(s4);   /* OK */
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) { }

    public static String fakeUpperCase(String a) {
        String b = a;
        String c = b;
        return c;
    }
}
