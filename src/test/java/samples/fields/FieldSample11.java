package samples.fields;

public class FieldSample11 {
    int Field;

    public void main() {
        this.Field = source(); // SOURCE

        this.Field = 10;

        sink(this.Field); // SINK, OK
    }

    public int source() {
        return 42;
    }

    public void sink(int x) {
        System.out.println(x);
    }
}
