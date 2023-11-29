package samples;

public class AllocationFlowTest1 {
    private Point p;

    public void main() {
        Integer x = new Integer(3); //Left
        x = null;
        Integer y;
        y = x;
    }
}