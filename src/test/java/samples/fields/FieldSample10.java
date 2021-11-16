package samples.fields;

public class FieldSample10 {
    int Field;

    public void main() {
        this.Field = source(); // SOURCE

        // Sink of the field directly
        sink(this.Field); // SINK, BAD
    }

    public int source() {
        return 42;
    }

    public void sink(int x) {
        System.out.println(x);
    }
}
