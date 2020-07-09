package samples.callSiteMatch;

public class CallSiteMatch1 {
    public static void main(String args[]) {
        String s1 = sourceHost();

        sink(s1);   /* BAD */
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) { }

    public static String sourceHost() {
        String a = source();
        return a;
    }
}
