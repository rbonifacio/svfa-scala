package samples.basic;

public class Basic16String {
    public class Widget {
        String contents;

        public Widget() {
            contents = "safe";
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }
    }

    public void main(String args[]) {
        String s1 = source();

        Widget w = new Widget();
        w.setContents(s1);

        sink(w.getContents());                    /* BAD */
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {

    }
}
