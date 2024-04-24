package samples.context;

public class Context41 {

    public static void main(String args[]) {

        String s1;

        ManyCallFancy41 o1, o2;

        o1 = new ManyCallFancy41();
        o2 = new ManyCallFancy41();

        s1 = source();
        o1.information = s1;
        o2.information = "acm1pt";

        sink(o1.getInformation());
        sink(o2.getInformation());
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}

class ManyCallFancy41 {

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