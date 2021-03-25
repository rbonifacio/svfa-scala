package javax.servlet;

import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import java.util.logging.Filter;

public interface ServletContext {
    public abstract Object getAttribute(String arg0);

    @SuppressWarnings("rawtypes")
    public abstract Enumeration getAttributeNames();

    public abstract ServletContext getContext(String arg0);

    public abstract String getInitParameter(String name);

    public abstract String getContextPath();

    public abstract int getEffectiveMajorVersion();

    public abstract int getEffectiveMinorVersion();

    public abstract boolean setInitParameter(String s, String s1);

    public abstract ServletRegistration.Dynamic addServlet(String s, String s1);

    public abstract ServletRegistration.Dynamic addServlet(String s, Servlet servlet);

    public abstract ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass);

    public abstract <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException;

    public abstract ServletRegistration getServletRegistration(String s);

    public abstract Map<String, ? extends ServletRegistration> getServletRegistrations();

    FilterRegistration.Dynamic addFilter(String s, String s1);

    FilterRegistration.Dynamic addFilter(String s, Filter filter);

    FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass);

    <T extends Filter> T createFilter(Class<T> aClass) throws ServletException;

    FilterRegistration getFilterRegistration(String s);

    Map<String, ? extends FilterRegistration> getFilterRegistrations();

    SessionCookieConfig getSessionCookieConfig();

    void setSessionTrackingModes(Set<SessionTrackingMode> set);

    Set<SessionTrackingMode> getDefaultSessionTrackingModes();

    Set<SessionTrackingMode> getEffectiveSessionTrackingModes();

    public abstract void addListener(String s);

    public abstract <T extends EventListener> void addListener(T t);

    public abstract void addListener(Class<? extends EventListener> aClass);

    public abstract <T extends EventListener> T createListener(Class<T> aClass) throws ServletException;

    JspConfigDescriptor getJspConfigDescriptor();

    public abstract ClassLoader getClassLoader();

    public abstract void declareRoles(String... strings);

    public abstract Enumeration getInitParameterNames();

    public abstract int getMajorVersion();

    public abstract String getMimeType(String arg0);

    public abstract int getMinorVersion();

    RequestDispatcher getNamedDispatcher(String arg0);

    public abstract String getRealPath(String arg0);

    RequestDispatcher getRequestDispatcher(String arg0);

    public abstract URL getResource(String arg0) throws MalformedURLException;

    public abstract InputStream getResourceAsStream(String arg0);

    @SuppressWarnings("rawtypes")
    public abstract Set getResourcePaths(String arg0);

    public abstract String getServerInfo();

    public abstract Servlet getServlet(String arg0) throws ServletException;

    public abstract String getServletContextName();

    @SuppressWarnings("rawtypes")
    public abstract Enumeration getServletNames();

    @SuppressWarnings("rawtypes")
    public abstract Enumeration getServlets();

    public abstract void log(String arg0);

    public abstract void log(Exception arg0, String arg1);

    public abstract void log(String arg0, Throwable arg1);

    public abstract void removeAttribute(String arg0);

    public abstract void setAttribute(String arg0, Object arg1);
}
