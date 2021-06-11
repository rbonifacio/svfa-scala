package samples;
public class NestedAll1 {
    public static void main(){
        int x = 0;
        while (x != 0){
            x = 1;
            while (x == 1){
                if (x != 2){
                    x = 2;
                }
            }
            if (x == 3){
                x = 3;
                while(x == 4){
                    x = 4;
                    if (x == 5){
                        x = 5;
                    }
                    x = 6;
                }
            }else{
                x = 7;
            }
            x = 8;
        }
        x = 9;
    }
}