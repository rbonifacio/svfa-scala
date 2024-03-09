package samples;

public class CallClassMethodOnceLite {

    public static void main(String args[]) {

        String s1Aux;

        OnceCallLite o1 = new OnceCallLite();
//        s1Aux = o1.identity(source());
//        sink(s1Aux);

        sink(o1.identity(source()));
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}

class OnceCallLite {

    public static String identity(String s) {
        return s;
    }
}
