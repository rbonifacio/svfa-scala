package samples;
public class NestedWhileAndIf {
    public static void main(){
        int x = 1;
        while (x!=0){
            x = 2;
            while (x!=1){
                x = 3;
            }
            x = 4;
        }
        x = 5;
        if (x >3){
            x = 6;
        }
        x = 7;
    }
}