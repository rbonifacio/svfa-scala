package samples;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MotivatingDF2 {
    private Integer number;

    public void cleanText(){
        normalizeIntegerNumber(); //Left
        removeZeroRight();
        removeDuplicateDigits(); //Right
    }

    private void normalizeIntegerNumber(){
        number = 10;
    }

    private void removeZeroRight(){
        Integer aux = number;
        while (aux % 10 == 0 && aux != 0) {
            aux /= 10;
        }
        number = aux;
    }

    private void removeDuplicateDigits(){
        String numeroString = String.valueOf(number);
        StringBuilder result = new StringBuilder();

        LinkedHashSet<Character> uniqueCharacters = new LinkedHashSet<>();
        for (int i = numeroString.length() - 1; i >= 0; i--) {
            char digit = numeroString.charAt(i);
            if (!uniqueCharacters.contains(digit)) {
                uniqueCharacters.add(digit);
                result.insert(0, digit);
            }
        }

        number = Integer.parseInt(result.toString());;
    }

    public Integer getText(){
        return number;
    }

    public void setText(Integer number){
        this.number = number;
    }
}