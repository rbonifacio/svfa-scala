package samples.fields;

public class FieldSample06 {
    public void main() {
        Foo x = source();
        Foo y = new Foo(); // Clean

        Container object1 = new Container();
        Container object2 = new Container();

        object1.setField(x);
        object2.setField(y);

        Foo tainted = object1.getField();
        Foo clean = object2.getField();

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
