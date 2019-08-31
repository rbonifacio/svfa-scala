package samples;

class AClass {
    BClass field;
}

class BClass {}

public class BlackBoard {

    public static void main(String args[]) {
        AClass a = new AClass();
        AClass b = a;

        BClass c = new BClass();

        a.field = c;
        BClass x = b.field;
    }
}
