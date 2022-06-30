package samples.basic;

public class CSSample1 {

    public int m(int value) {
        return 10 + value;
    }

    public void main(String args[]) {
        int S1 = source();
        int S2 = m(S1);
        sink(S2);
    }

    public void sink(int data) { }
    public int source() {  return 100; }
}
