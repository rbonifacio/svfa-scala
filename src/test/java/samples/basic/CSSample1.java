package samples.basic;

public class CSSample1 {

    public String m(String value) {
        return "o" + value;
    }

    public void main(String args[]) {
        String S1 = "safe";
        String S2 = m(S1);
        sink(S2);

        String S3 = "secret";
        String S4 = m(S3);
        sink(S4);
    }

    public void sink(String data) { }

}
