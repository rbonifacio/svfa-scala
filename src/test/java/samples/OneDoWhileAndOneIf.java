package samples;
public class OneDoWhileAndOneIf {
    public static void main() {
        int x = 1;
        do{
            x = 2;
            if (x == 0){
                x = 3;
            }
            x = 5;
        }while (x < 1);
        x = 4;
    }
}