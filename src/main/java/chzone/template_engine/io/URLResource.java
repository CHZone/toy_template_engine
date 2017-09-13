package chzone.template_engine.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *  Use the URL Object to return an InputStream .
 */
public class URLResource implements Resource{
    private final URL url;
    public URLResource(URL url){
        this.url = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection urlConnection =  this.url.openConnection();
        urlConnection.connect();
        return urlConnection.getInputStream();
    }

}
