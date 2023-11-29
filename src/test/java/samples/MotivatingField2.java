package samples;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MotivatingField2 {
    public Integer number;

    public static void main(){
        MotivatingField2 inst = new MotivatingField2();
        inst.cleanText();
    }

    public void cleanText(){
        initNumber(); //Left
        printNumber();
        checkNumber(); //Right
    }

    private void initNumber(){
        number = 3;
    }

    private void printNumber(){
        System.out.println(number);
    }

    private void checkNumber(){
        Integer aux_number = number;
        if (aux_number > 10){
            System.out.println(aux_number);
        }
    }

    public Integer getText(){
        return number;
    }

    public void setText(Integer number){
        this.number = number;
    }
}