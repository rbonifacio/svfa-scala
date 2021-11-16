package samples.fields;

import java.util.Random;

public class FieldSample09 {
    public void main() {
        Foo x = source();

        Container object = new Container();

        object.Field = x; // (store)

        Random r = new Random();
        boolean choice = r.nextBoolean();

        if (choice) {
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
