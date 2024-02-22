package samples;

import soot.jimple.parser.node.AIdentArrayRef;

public class CallClassMethodOnce {

    public static void main(String args[]) {

        String s1,  s1Aux;
        OnceCall o1 = new OnceCall();

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

class OnceCall {

    public static String identity(String s) {
        return s;
    }
}
