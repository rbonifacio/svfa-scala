package samples;
public class NestedWhileDoWhileIf {
    public static void main(){
        int x = 0;
        while (x >= 1){
            do{
                x = 1;
                if (x == 0){
                    x = 2;
                }
                x = 3;
            }while (x > 3);
            x = 4;
        }
        x = 5;
    }
}