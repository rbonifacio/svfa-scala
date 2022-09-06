package samples;

public class MethodFieldSample {
    private int x;

    public void m() {
        o(); // left
        System.out.println(x); //right
    }
    public void o() {
        x=1;
    }
}