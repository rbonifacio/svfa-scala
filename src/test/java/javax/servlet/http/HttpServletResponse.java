package javax.servlet.http;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;

public interface HttpServletResponse extends ServletResponse {

    PrintWriter getWriter();

    void sendRedirect(String s);

    void setContentType(String s);
}
