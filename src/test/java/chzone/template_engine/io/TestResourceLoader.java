package chzone.template_engine.io;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

public class TestResourceLoader {
    @Test
    public void testResouceLoader() throws IOException{
        ResourceLoader resourceLoader = new ResourceLoader();
        Resource resource = resourceLoader.getResource("template/template.html");
        InputStream is = resource.getInputStream();
        Assert.assertNotNull(is);
                
    }
}
