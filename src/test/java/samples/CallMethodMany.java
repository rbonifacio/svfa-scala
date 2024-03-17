package samples;

public class CallMethodMany {

    public static void main(String args[]) {

        String s1,  s1Aux, s2,  s2Aux;

        s1 = source();
        s1Aux = identity(s1);

        s2 = "acm1pt";
        s2Aux = identity(s2);

        sink(s1Aux); //OK
        sink(s2Aux); //OK
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
