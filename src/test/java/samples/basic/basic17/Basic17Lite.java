/**
    @author Benjamin Livshits <livshits@cs.stanford.edu>
   
    $Id: Basic17.java,v 1.4 2006/04/04 20:00:40 livshits Exp $
 */
package samples.basic.basic17;

import securibench.micro.BasicTestCase;
import securibench.micro.MicroTestCase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/** 
 *  @servlet description="simple heap-allocated data strucure" 
 *  @servlet vuln_count = "1" 
 *  */
public class Basic17Lite {

    public static void main(String args[]) {

        String s = source();
        Lite w1 = new Lite();
        w1.setContents(s);

        Lite w2 = new Lite();
        w2.setContents("abc");

        sink(w1.getContents());                    /* BAD */
        sink(w2.getContents());                    /* OK */
    }

    public static String source() {
        return "secret";
    }

    public static void sink(String s) {
        System.out.println(s);
    }
}

class Lite {
    String contents;

    public String getContents() {
        return this.contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}