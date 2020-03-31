package samples;

class FooLoopback {
    int x = 10;
    int getValue() { return x; }
}
public class LogbackSample {
    private static boolean simpleTest(FooLoopback x) {
        return true;
    }

    private static boolean complexTest(FooLoopback x) {
        return false;
    }

    private static FooLoopback source() {
        return new FooLoopback();
    }

    private static int generateNumber(int x) {
        return x + 1;
    }
    public static void main(String args[]) {
        FooLoopback x = source();                    // source line

        if(simpleTest(x)) {
            throw new RuntimeException();
        }

        int y = 0;

        if(x.getValue() > 10) {                     // sink line
            y = generateNumber(x.getValue());       // sink line
        }

        generateNumber(y);
    }
}
