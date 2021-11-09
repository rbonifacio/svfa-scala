package samples.fields;

public class FieldSample07 {
    public void main() {
        Foo x = source();

        Container object = new Container();

        object.Field = x; // (store)

        if (false) {
            Foo z = new Foo();
            object.Field = z; // overwrite the Field
        }

        Foo y = object.Field; // (load)

        sink(y); // BAD
    }

    public Foo source() {
        return new Foo();
    }

    public void sink(Foo x) {
        System.out.println(x);
    }
}
