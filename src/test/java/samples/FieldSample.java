package samples;

public class FieldSample {
//    int x;
//
//    public void main() {
//        x = 10;                  // SOURCE
//        System.out.println(x);   // RIGHT
//    }

    public void m() {
        int x = 0;    // LEFT
        n(x);         // RIGHT
    }

    public void n(int a) {
        System.out.println(a); // RIGHT
    }

}
