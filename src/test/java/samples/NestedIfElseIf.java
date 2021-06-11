package samples;
public class NestedIfElseIf {
    public static void main() {
        int x = 1;
        if (x < 1){
            if (x == 0){
                x = 2;
            }
            x = 3;
        }else{
            if (x != 0){
                x = 4;
            }
            x = 5;
        }
        x = 6;
        if (x > 11){
            if (x != 11){
                x = 7;
            }
            x = 8;
        }
        x = 9;
    }
}