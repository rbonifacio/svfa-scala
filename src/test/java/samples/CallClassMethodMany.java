package samples;

public class CallClassMethodMany {

    public static void main(String args[]) {

        String s1,  s1Aux, s2,  s2Aux;
        ManyCall o1, o2;

        o1 = new ManyCall();
        o2 = new ManyCall();

        s1 = source();
        s1Aux = o1.identity(s1);

        s2 = "acm1pt";
        s2Aux = o2.identity(s2);

        sink(s1Aux); //OK
        sink(s2Aux); //no ok
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}

class ManyCall {

    public static String identity(String s) {
        return s;
    }
}
