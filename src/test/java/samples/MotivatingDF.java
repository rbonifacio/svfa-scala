package samples;

public class MotivatingDF {

    public Integer text;

    public static void main(){
        MotivatingDF inst = new MotivatingDF();
        inst.text = 5;
        inst.cleanText();
    }

    public void cleanText() {
        normalizeWhiteSpaces(); //Left
        removeComments();
        removeDuplicatedWords(); //Right
    }
    private void normalizeWhiteSpaces() {

        text = 1;
    }

    private void removeComments() {}

    private void removeDuplicatedWords() {
        text = text;
    }

    public void initAllocationSites(){
        text = 0;
    }
}