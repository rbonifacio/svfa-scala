package samples.fields;

public class FieldSample14 {
    String Field;

    public void main() {
        // The tainted object have a tainted field of a primitive type, created without 'new'
        this.Field = source();

        // Sink of the field directly
        sink(this.Field); // SINK, BAD
    }

    public String source() {
        return "secret";
    }

    public void sink(String x) {
        System.out.println(x);
    }
}
