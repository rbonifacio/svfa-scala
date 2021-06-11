package samples;
public class NestedThreeWhile {
    public static void main(){
        int x = 0;
        while (x >= 1){
            while (x < 20){
                while (x > 3){
                    x = 3;
                }
            }
            x = 4;
        }
        x = 5;
    }
}