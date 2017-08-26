package chzone.template_engine.io;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
    /**
     * 漏掉了throws IOException
     */
    public InputStream getInputStream() throws IOException;
}
