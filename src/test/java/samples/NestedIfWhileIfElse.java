package samples;
public class NestedIfWhileIfElse {
    public static void main(){
        int x = 0;
        if (x == 1){
            while (x != 2){
                if (x == 3){
                    x = 3;
                }else{
                    x = 4;
                }
            }
            x = 5;
        }
        x = 6;
    }
}