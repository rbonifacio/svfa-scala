package samples;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MotivatingField {
    public Integer text;

    public static void main(){
        MotivatingField inst = new MotivatingField();
        inst.cleanText();
    }

    public void cleanText(){
        normalizeWhiteSpace(); //Left
        removeComments();
        removeDuplicateWords(); //Right
    }

    private void normalizeWhiteSpace(){
        this.text = text;
    }

    private void removeComments(){
//        String pattern = "(\".*?\"|'.*?')|(/\\*.*?\\*/|//.*?$)";
//        Pattern regex = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL);
//        Matcher matcher = regex.matcher(text.toString());
//        StringBuffer buffer = new StringBuffer();
//        while (matcher.find()) {
//            if (matcher.group(1) != null) {
//                matcher.appendReplacement(buffer, matcher.group(1));
//            } else {
//                matcher.appendReplacement(buffer, "");
//            }
//        }
//        matcher.appendTail(buffer);
        this.text = 1;
    }

    private void removeDuplicateWords(){
        Integer words = this.text;
//        StringBuilder result = new StringBuilder(words[0]);
//        for (int i = 1; i < words.length; i++) {
//            if (!words[i].equals(words[i - 1])) {
//                result.append(" ");
//                result.append(words[i]);
//            }
//        }

        text = 1;
    }

    public Integer getText(){
        return text;
    }

    public void setText(Integer text){
        this.text = text;
    }
}