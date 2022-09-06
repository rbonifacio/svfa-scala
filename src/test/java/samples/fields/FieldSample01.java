package samples.fields;

public class FieldSample01 {
    public void main() {
        Foo x = source();

        Container object = new Container();

        object.Field = x; // Direct assignment of the Field (store)

        Foo y = object.Field; // Direct access of the Field (load)

        sink(y); // BAD
    }

    public Foo source() {
        return new Foo();
    }

    public void sink(Foo x) {
        System.out.println(x);
    }
}
