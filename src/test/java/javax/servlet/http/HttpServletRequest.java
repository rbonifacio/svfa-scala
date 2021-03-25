package javax.servlet.http;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Enumeration;
import java.util.Map;

public interface HttpServletRequest extends ServletRequest {
    public abstract String getParameter(String name);

    public abstract HttpSession getSession();

    public abstract String[] getParameterValues(String fieldName);

    public abstract Map getParameterMap();

    public abstract Enumeration getParameterNames();

    public abstract Cookie[] getCookies();

    public abstract String getHeader(String s);

    public abstract Enumeration getHeaders(String s);

    public abstract Enumeration getHeaderNames();

    public abstract boolean getProtocol();

    public abstract boolean getScheme();

    public abstract boolean getAuthType();

    public abstract boolean getQueryString();

    public abstract boolean getRemoteUser();

    public abstract boolean getRequestURL();

    ServletInputStream getInputStream();
}
