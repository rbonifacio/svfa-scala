package samples.basic;

public class CSSample1 {

    public int m(int value) {
        return 10 + value;
    }

    public void main(String args[]) {
        int S1 = 100;
        int S2 = m(S1);
        sink(S2);

        /*int S3 = 999;
        int S4 = m(S3);
        sink(S4);*/
    }

    public void sink(int data) { }

}
