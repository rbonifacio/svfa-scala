package samples.caseStudy;

public class SimpleTaint {

    public static void main(String args[]) {
        int S1 = source();
        int S2 = S1 + 3;
        int S3 = S2 - 1;
        sink(S3);
    }

    public static int source() { return 1; }
    public static void sink(int data) { }

}
