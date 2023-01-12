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

//public class FieldSample3 {
//
//    public void main() {
//        Foo3 A = new Foo3();
//        Foo3 B = new Foo3();
////
//        A.setBlah(source());   // SOURCE
//        B.setBlah(999);       // SAFE
////
//        sink(A.blah); // SINK
//        sink(B.blah); // OK
//    }
//
//    int source() {
//        return 0;
//    }
//
//    void sink(int b) {
//    }
//}

//public class FieldSample3
//{
//    public void main() {
//        int a, b;
//        int ap, bp;
////
////        ap = source();
//        ap = 1;
//        bp = 2;
//
//        a = myymethod(ap);
//
//        b = myymethod(bp);
//
//        sink(a);
//        sink(b);
//    }
//
//    int myymethod(int x)
//    {
//        return x;
//    }
//
//    int source() {
//        return 0;
//    }
//
//    void sink(int b) {
//
//    }
//}

//public class FieldSample3
//{
//    Integer x;
//
//    public void main() {
//        int a;
//        a = source();
////        myMethod(a);
//        this.x = a;
//        sink(a);
//    }
//
//    void myMethod(int x)
//    {
//        this.x = x;
//    }
//
//    int source() {
//        return new Integer(0);
//    }
//
//    void sink(int b) {
//
//    }
//}

//class Z{}
//class Z1 {
//    int a, b;
//}
//class Z2 {}
//
//public class FieldSample3
//{
//    public void main() {
//        Z1 z1 = new Z1();
//        Z1 z2 = new Z1();
//        z1.a = source();
//        z2.a = 999;
//        sink(z1.a); //ERROR
//        sink(z2.a); //OK
//    }
//    Z1 myMethod(Z1 x) {
//        return x;
//    }
//
//    int source() {
//        return 0;
//    }
//
//    void sink(int b) {
//    }
//}


public class FieldSample3
{
    public void main() {
        int a, b;

//        a = source();
//        b = 1;
//        b = a;
//        sink(b);
    }

    int source() {
        return 0;
    }

    void sink(int b) {
    }
}




























