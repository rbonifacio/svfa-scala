package samples.caseStudy;

import securibench.micro.basic.Basic30;

public class SimpleObjectTaint {

    class Data {
        String value1;
        String value2;
    }

    public void main(String args[]) {
        String name = source();
        Data d = new Data();
        d.value1 = "abc";
        d.value2 = name;

        sink(d.value1);         /* OK */
        sink(d.value2);         /* BAD */
    }

    public String source() { return "my_name_is"; }
    public void sink(String value) { }

}
