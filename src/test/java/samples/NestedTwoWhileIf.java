package samples;
public class NestedTwoWhileIf {
    public static void main(){
        int x = 0;
        while (x != 0){
            x = 1;
            while (x == 1){
                if (x != 43){
                    x = 2;
                }
            }
            x = 8;
        }
        x = 9;
    }
}