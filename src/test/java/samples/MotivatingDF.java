package samples;

public class MotivatingDF {
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