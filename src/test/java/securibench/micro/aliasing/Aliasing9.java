/**
    @author Benjamin Livshits <livshits@cs.stanford.edu>
    
    $Id: Aliasing6.java,v 1.1 2006/04/21 17:14:27 livshits Exp $
 */
package securibench.micro.aliasing;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import securibench.micro.BasicTestCase;
import securibench.micro.MicroTestCase;

/** 
 *  @servlet description="interprocedural aliasing in an array index"
 *  @servlet vuln_count = "1"
 *  */
public class Aliasing9 extends BasicTestCase implements MicroTestCase {
	private static final String FIELD_NAME = "name";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       String[] names = source(req);
       Object o1, o2;
       o1 = o2 = names[1];

       o2 = names[0];
              
       PrintWriter writer = resp.getWriter();
       writer.println(o1);                              /* OK */
       writer.println(o2);                              /* BAD */
    }

    public String[] source(HttpServletRequest req) {
        String[] names = new String[2];
        names[0] = req.getParameter(FIELD_NAME);
        names[1] = "Foo";

        return names;
    }
    
    public String getDescription() {
        return "aliasing with copy propagation";
    }
    
    public int getVulnerabilityCount() {
        return 7;
    }
}