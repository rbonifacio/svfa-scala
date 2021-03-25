package javax.servlet;

import java.io.IOException;
import java.io.InputStream;

public abstract class ServletInputStream extends InputStream {
    public abstract int read() throws IOException;
}
