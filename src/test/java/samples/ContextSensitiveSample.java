package samples;

public class ContextSensitiveSample {
    static class Document {
        String subject;
        String content;

        public Document(String subject, String content) {
            this.subject = subject;
            this.content = content;
        }

        public Document validate() {
            return this;
        }
    }

    private static Document readConfiedentialContent() {
        return new Document("secret", "secret");
    }

    private static void sink(Document d) {}

    public static void main(String args[]) {
        Document d1 = readConfiedentialContent();
        Document d2 = new Document("foo", "blah");
        d1 = d1.validate();
        d2 = d2.validate();

        sink(d1);     /* BAD */
        sink(d2);     /* OK  */
    }
}
