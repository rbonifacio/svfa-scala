package samples;
public class NestedWhileIfWhile {
    public static void main(){
        int x = 0;
        while (x == 2){
            if (x != 43){
                x = 2;
                while (x >= 3){
                    x = 3;
                }
            }
        }
        x = 4;
    }
}