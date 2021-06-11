package samples;
public class OneDoWhileAndWhile {
    public static void main() {
        int x = 1;
        do{
            x = 2;
            while (x != 33){
                x = 123;
            }
            x = 5;
        }while (x < 1);
        x = 4;
    }
}