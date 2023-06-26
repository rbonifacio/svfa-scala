package samples;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MotivatingDF2 {
    public Integer text;

    public void cleanText(){
        normalizeWhiteSpace(); //Left
        removeComments();
        removeDuplicateWords(); //Right
    }

    private void normalizeWhiteSpace(){
        text = new Integer(3);
    }

    private void removeComments(){

    }

    private void removeDuplicateWords(){
        Integer words = text;
    }

}