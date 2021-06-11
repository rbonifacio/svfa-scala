package samples;
public class OneWhileAndOneIfElse {
    public static void main() {
        int x = 1;
        while (x > 1) {
            x = 2;
        }
        x = 4;
        if (x<10){
            x = 5;
        }else{
            x = 6;
        }
        x = 7;
    }

}