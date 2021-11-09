package samples.fields;

public class FieldSample12 {
    Foo Field;

    public void main() {
        // The tainted object have a tainted field of a primitive type, created without 'new'
        this.Field = source();

        // Sink of the field directly
        sink(this.Field); // SINK, BAD
    }

    public Foo source() {
        return new Foo();
    }

    public void sink(Foo x) {
        System.out.println(x);
    }
}
