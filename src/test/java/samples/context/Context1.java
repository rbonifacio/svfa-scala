package samples.context;

public class Context1 {

    public static void main(String args[]) {

        String s1, s1Aux;

        IdentityClass o1 = new IdentityClass();

        s1 = source();
        s1Aux = o1.identity(s1);
        sink(s1Aux);
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}

class IdentityClass {

    public static String identity(String s) {
        return s;
    }
}