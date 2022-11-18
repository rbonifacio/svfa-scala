package samples;

class Foo2 {
    Blah2 blah;

    void setBlah(Blah2 b) {
        this.blah = b;
    }

     Blah2 getBlah() {
        return this.blah;
    }
}

class Blah2 {

}
public class FieldSample2 {





    public void main() {
        Foo2 A = new Foo2();
        Foo2 B = new Foo2();

//        A.blah = source();
//        B.blah = new Blah();
        A.setBlah(source());       // SOURCE
        B.setBlah(new Blah2());       // SAFE

//        sink(A.getBlah());           // Bad
//        sink(B.getBlah());           // Ok
        sink(A.blah);
        sink(B.blah);
    }

    Blah2 source() {
        return new Blah2();
    }

    void sink(Blah2 b) {

    }
}
