package samples.aliasing;

public class Alias5 {

    public static void main(String args[]) {
        Buffer buffer1 = new Buffer();
        Buffer buffer2 = buffer1;

        buffer1.setValue(source());

        sink(buffer1.getValue());   /* BAD */
        sink(buffer2.getValue());   /* BAD */
    }

    private static String source() {
        return "secret";
    }

    private static void sink(String value) { }

    static class Buffer {
        String value;

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}
