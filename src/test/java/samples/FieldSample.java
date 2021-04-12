package samples;

public class FieldSample {

    public void main() {
        int x = new Integer(10);              // SOURCE
        n(x);                                       // RIGHT
    }

    public void n(int a) {
        System.out.println(a);   // RIGHT
    }

}
