package samples.fields;

public class FieldSample15 {
    public class Bar {
        String Field;

        void setField(String x) {
            this.Field = x;
        }

        String getField() {
            return this.Field;
        }
    }


    public void main() {
        Bar object = new Bar();
        object.setField(source()); // SOURCE

        // Sink of the object without tainted field
        String x = object.getField();

        sink(x); // SINK, BAD
    }

    public String source() {
        return "secret";
    }

    public void sink(String x) {
        System.out.println(x);
    }
}
