package chzone.template_engine.io;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {

    public InputStream getInputStream() throws IOException;
}
