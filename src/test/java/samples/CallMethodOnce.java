package samples;

public class CallMethodOnce {

    public static void main(String args[]) {

        String s1,  s1Aux;
        s1 = source();
        s1Aux = identity(s1);

        sink(s1Aux);
    }

    public static String identity(String s) {
        return s;
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}
