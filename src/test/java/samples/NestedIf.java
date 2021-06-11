package samples;
public class NestedIf {
    public static void main() {
        int x = 1;
        if (x < 1){
            if (x == 0){
                x = 2;
            }
            x = 3;
        }
        x = 4;
    }
}