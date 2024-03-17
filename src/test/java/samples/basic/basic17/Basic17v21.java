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
public class Basic17v21 extends BasicTestCase implements MicroTestCase {
    public class Widget {
        String contents;

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }
    }

    private static final String FIELD_NAME = "name";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String s = req.getParameter(FIELD_NAME);
        Widget w1 = new Widget();
        w1.setContents(s);
        
        Widget w2 = new Widget();
        w2.setContents("abc");
        
        PrintWriter writer = resp.getWriter();  
        writer.println(w1.contents);                    /* BAD */
        writer.println(w2.contents);                    /* OK */
    }
    
    public String getDescription() {
        return "simple heap-allocated data structure";
    }
    
    public int getVulnerabilityCount() {
        return 1;
    }
}