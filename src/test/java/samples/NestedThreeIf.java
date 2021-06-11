package samples;
public class NestedThreeIf {
    public static void main(){
        int x = 0;
        if (x != 0){
            x = 1;
            if (x == 1){
                if (x != 43){
                    x = 2;
                }
            }
            x = 8;
        }
        x = 9;
    }
}