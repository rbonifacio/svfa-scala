package samples.fields;

public class FieldSample03 {
    public class Bar {
        Container obj;
    }

    public void main() {
        Container object = new Container();
        Bar bar = new Bar();

        object.Field = source(); // SOURCE and (store)
        bar.obj = object;

        sink(bar.obj.Field); // SINK, BAD
    }

    public Foo source() {
        return new Foo();
    }

    public void sink(Foo x) {
        System.out.println(x);
    }
}
