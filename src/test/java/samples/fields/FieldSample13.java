package samples.fields;

public class FieldSample13 {
    Foo Field;

    public void main() {
        this.Field = source();

        this.Field = new Foo();

        // Sink of the field directly
        sink(this.Field); // SINK, OK
    }

    public Foo source() {
        return new Foo();
    }

    public void sink(Foo x) {
        System.out.println(x);
    }
}
