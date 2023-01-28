package samples;

class Foo3 {
    int blah;

    void setBlah(int b) {
        this.blah = b;
    }

//    int setBlah(int b) {
//       return b;
//    }

    int getBlah() {
        return this.blah;
    }
}

public class FieldSample3 {

    public void main() {
        int a, b;
        a = 999;
        b = 111;


        Foo3 A = new Foo3();
        Foo3 B = new Foo3();
//
        A.setBlah(source());   // SOURCE
//        A.setBlah(a);       // SAFE
        B.setBlah(b);       // SAFE
//
        sink(A.blah); // SINK
        sink(B.blah); // OK
    }

    int source() {
        return 0;
    }

    void sink(int b) {
    }
}

//public class FieldSample3
//{
//    public void main() {
//        int a, b;
//        int ap, bp;
////
//        ap = source();
////        ap = 1;
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
//    int g;
//
//    public void main() {
//        int a, b;
//        int x, y;
//        a = 1;
//        b = 2;
//
//        x = myMethod(a);
//        y = myMethod(b);
//
//        sink(x);
//        sink(y);
//    }
//
//    int myMethod(int x)
//    {
//        return x;
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

////class Z{}
//class Z1 {
//    int a, b;
//}
////class Z2 {}
//
//public class FieldSample3
//{
//    public void main() {
//        int x, y;
////        x = 999;
//        y = 111;
//        Z1 z1 = new Z1();
//        Z1 z2 = new Z1();
//
//        z1.a = source();
////        z1.a = x;
//        z2.a = y;
////        z2.a = source();
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


//public class FieldSample3
//{
//    public void main() {
//        int a, b, c;
//
//        a = source();
//        b = a;
//        c = 1 + b;
//        sink(c);
//    }
//
//    int source() {
//        return 0;
//    }
//
//    void sink(int b) {
//    }
//}













