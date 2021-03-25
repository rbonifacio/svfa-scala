package javax.servlet.http;

import java.util.Enumeration;

public interface HttpSession {
    public abstract void setAttribute(String name, String name1);

    public abstract Object getAttribute(String name);

    public abstract Enumeration getAttributeNames();
}
