package samples.context;

public class Context2 {

    public static void main(String args[]) {

        String s1, s1Aux;
        String s2, s2Aux;

        IdentityClass2 o1 = new IdentityClass2();
        s1 = source();
        s1Aux = o1.identity(s1);


        IdentityClass2 o2 = new IdentityClass2();
        s2 = "abc";
        s2Aux = o2.identity(s2);

        sink(s1Aux);
        sink(s2Aux);
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}

class IdentityClass2 {

    public static String identity(String s) {
        return s;
    }
}