package samples.fields;

public class FieldSample05 {
    public void main() {
        Foo x = source();
        Foo y = new Foo(); // Clean

        Container object1 = new Container();
        Container object2 = new Container();

        object1.Field = x;
        object2.Field = y;

        Foo tainted = object1.Field;
        Foo clean = object2.Field;

        sink(tainted); // BAD
        sink(clean); // OK
    }

    public Foo source() {
        return new Foo();
    }

    public void sink(Foo x) {
        System.out.println(x);
    }
}
