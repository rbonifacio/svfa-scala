package samples;

public class CallClassMethodManyFancy {

    public static void main(String args[]) {

        String s1,  s1Aux, s2,  s2Aux;

        ManyCallFancy o1, o2;

        o1 = new ManyCallFancy();
        o2 = new ManyCallFancy();

        s1 = source();
        o1.setInformation(s1);
        s1Aux = o1.getInformation();

        s2 = "acm1pt";
        o2.setInformation(s2);
        s2Aux = o2.getInformation();

        sink(s1Aux);
        sink(s2Aux);
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}

class ManyCallFancy {

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
