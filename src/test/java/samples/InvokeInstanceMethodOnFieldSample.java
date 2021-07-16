package samples;

class Test {
    int b = 0;

    public int getB() {
        return b;
    }
}

public class InvokeInstanceMethodOnFieldSample {

    private Test a;

    public void m() {
        a = new Test(); // left

        a.getB(); // right
    }
}
