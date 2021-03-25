package javax.servlet;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public interface ServletResponse {
    PrintWriter getWriter();
}
