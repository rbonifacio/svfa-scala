package samples;
public class OneWhileAndNestedIfElse {
    public static void main(){
        int x = 1;
        while (x!=0){
            x = 2;
            if (x!=1){
                x = 3;
            }else{
                x = 4;
            }
            x = 5;
        }
        x = 6;
    }


}