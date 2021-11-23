package samples.basic;

public class Basic16 {
    public class Widget {
        Integer contents;

        public Widget() {
            contents = new Integer(0);
        }

        public Integer getContents() {
            return contents;
        }

        public void setContents(Integer contents) {
            this.contents = contents;
        }
    }

    public void main(String args[]) {
        Integer s1 = source();

        Widget w = new Widget();
        w.setContents(s1);

        sink(w.getContents());                    /* BAD */
    }

    public static Integer source() {
        return new Integer(1);
    }

    public static void sink(Integer s) {

    }
}
