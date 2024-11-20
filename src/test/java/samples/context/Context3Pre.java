package samples.context;

public class Context3Pre {

    public static void main(String args[]) {

        String s1,  s1Aux;
        OnceCallFancyPre o1 = new OnceCallFancyPre();

        s1 = source();
        o1.setInformation(s1);
        s1Aux = o1.information;

        sink(s1Aux);
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}

class OnceCallFancyPre {

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