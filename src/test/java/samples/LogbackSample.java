package samples;

public class LogbackSample {

    private static boolean simpleTest(int x) {
        return true;
    }

    private static boolean complexTest(int x) {
        return false;
    }

    private static int source() {
        return 10;
    }

    private static int generateNumber(int x) {
        return x + 1;
    }
    public static void main(String args[]) {
        int x = source();                    // source line

        if(simpleTest(x)) {
            throw new RuntimeException();
        }

        int y = 0;

        if(complexTest(x)) {                // sink line
            y = generateNumber(x);          // sink line
        }

        generateNumber(y);
    }
}
