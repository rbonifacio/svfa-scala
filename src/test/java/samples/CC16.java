package samples;

class Foo {
    Blah field;
}

class Blah {

}

public class CC16 {

    public static void main(String args[]) {
        Foo f = new Foo();
        Blah b = source();
        f.field = b;
        Blah r = readField(f);
        sink(r);
    }

    public static Blah readField(Foo f) {
        return f.field;
    }

    public static Blah source() {
        return new Blah();
    }

    public static void sink(Blah b) {}
}
