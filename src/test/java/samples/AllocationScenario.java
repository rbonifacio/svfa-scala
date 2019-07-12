package samples;

class A1 {
    B1 field;
}

class B1 { }

public class AllocationScenario {

    public static void main(String args[]) {
        A1 a = new A1();

        A1 b = a;

        B1 c = new B1();

        a.field = c;

        B1 x = b.field;
    }
}
