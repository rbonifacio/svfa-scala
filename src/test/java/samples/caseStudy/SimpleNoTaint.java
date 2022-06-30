package samples.caseStudy;

public class SimpleNoTaint {

    public void main(String args[]) {
        int S1 = source();
        int S2 = S1 + 3;
        S2 = 5;
        int S3 = S2 - 1;
        sink(S3);
    }

    public int source() { return 1; }
    public void sink(int data) { }
}
