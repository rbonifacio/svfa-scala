package samples;

class Foo3 {
    int blah;

    void setBlah(int b) {
        this.blah = b;
    }

    int getBlah() {
        return this.blah;
    }
}

public class FieldSample3 {

    public void main() {
//        Foo3 A = new Foo3();
        Foo3 B = new Foo3();
//
//        A.setBlah(source());   // SOURCE
        B.setBlah(999);       // SAFE
//
//        sink(A.blah); // SINK
        sink(B.blah); // OK
    }

    int source() {
        return 0;
    }

    void sink(int b) {

    }
}