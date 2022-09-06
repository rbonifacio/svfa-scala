package samples.fields;

public class FieldSample04 {
    public void main() {
        Foo x = source();

        Container object = new Container();

        object.Field = x; // Direct assignment of the Field (store)

        sink(object); // Sink of base object BAD
    }

    public Foo source() {
        return new Foo();
    }

    public void sink(Container x) {
        System.out.println(x);
    }
}
