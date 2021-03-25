package javax.servlet.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;

public abstract class HttpServlet {

    protected abstract void doTrace(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException;

    public abstract ServletConfig getServletConfig();
}
