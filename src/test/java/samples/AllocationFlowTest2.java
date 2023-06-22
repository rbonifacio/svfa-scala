package samples;

public class AllocationFlowTest2 {
    private Point p;

    public void main() {
        Integer x = new Integer(3); //Left
        Integer y;
        y = x;
    }
}