package samples.basic;

public class Basic31 {

    class Cookie {
        String name;
        String description;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public void main(String args[]) {
        Cookie[] cookies = source();

        String v1 = cookies[0].getName();
        String v2 = cookies[0].getDescription();
        String v3 = "safe";

        sink(v1);  // BAD
        sink(v2);  // BAD
        sink(v3);  // OK
    }

    public Cookie[] source() {
        Cookie res[] = new Cookie[1];

        Cookie tmp = new Cookie();

        tmp.name = "foo";
        tmp.description = "blah";

        res[0] = tmp;

        return res;
    }

    public void sink(String data) { }
}
