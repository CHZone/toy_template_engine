package chzone.template_engine.io;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoader {
    private static final Logger log = LoggerFactory.getLogger(ResourceLoader.class);
    public Resource getResource(String location){
        URL url = this.getClass().getClassLoader().getResource(location);
        log.info(url.toString());
        return new URLResource(url);
    }
}
