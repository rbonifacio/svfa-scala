package samples;

public class StringGetChars {

    public static String source() {
        return "secret";
    }

    public static void sink(char[] str) {
        System.out.println(str);
    }

    public static void main(String args[]) {
        String s = source();
        char[] alias = new char[s.length()];
        s.getChars(0, s.length(), alias, 0);
        sink(alias);
    }


}
