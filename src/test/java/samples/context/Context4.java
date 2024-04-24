package samples.context;

public class Context4 {

    public static void main(String args[]) {

        String s1;

        ManyCallFancyV2 o1, o2;

        o1 = new ManyCallFancyV2();
        o2 = new ManyCallFancyV2();

        s1 = source();
        o1.setInformation(s1);

        o2.setInformation("acm1pt");

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

class ManyCallFancyV2 {

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