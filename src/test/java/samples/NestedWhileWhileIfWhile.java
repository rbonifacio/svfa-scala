package samples;
public class NestedWhileWhileIfWhile {
    public static void main(){
        int x = 1;
        while (x!=0){
            x = 2;
            while (x!=1){
                if (x == 111){
                    x = 111;
                    while (x!=123){
                        x = 123;
                    }
                    x = 1100;
                }
                x = 3;
            }
            x = 4;
        }
        x = 5;
    }
}