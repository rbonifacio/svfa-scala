package javax.servlet;

import java.util.Enumeration;

public interface ServletConfig {
    public abstract String getInitParameter(String arg0);

    @SuppressWarnings("rawtypes")
    public abstract Enumeration getInitParameterNames();

    public abstract ServletContext getServletContext();

    public abstract String getServletName();
}
