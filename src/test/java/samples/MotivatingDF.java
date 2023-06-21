package samples;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MotivatingDF {
    private String text;

    public void cleanText(){
        normalizeWhiteSpace(); //Left
        removeComments();
        removeDuplicateWords(); //Right
    }

    private void normalizeWhiteSpace(){
        text = text.replaceAll("\\s+", " ");
    }

    private void removeComments(){
        String pattern = "(\".*?\"|'.*?')|(/\\*.*?\\*/|//.*?$)";
        Pattern regex = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = regex.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                matcher.appendReplacement(buffer, matcher.group(1));
            } else {
                matcher.appendReplacement(buffer, "");
            }
        }
        matcher.appendTail(buffer);
        text = buffer.toString();
    }

    private void removeDuplicateWords(){
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder(words[0]);
        for (int i = 1; i < words.length; i++) {
            if (!words[i].equals(words[i - 1])) {
                result.append(" ");
                result.append(words[i]);
            }
        }

        text = result.toString();
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }
}