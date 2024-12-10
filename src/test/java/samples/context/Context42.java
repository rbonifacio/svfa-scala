package samples.context;

public class Context42 {

    public static void main(String args[]) {

        String s1;

        ManyCallFancy42 o1, o2;

        o1 = new ManyCallFancy42();
        o2 = new ManyCallFancy42();

        s1 = source();
        o1.setInformation(s1);
        o2.setInformation("acm1pt");

        sink(o1.information);
        sink(o2.information);
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}

class ManyCallFancy42 {

    public String information;
    public void setInformation(String _information)
    {
        this.information = _information;
    }

    public String getInformation()
    {
        return this.information;
    }
}