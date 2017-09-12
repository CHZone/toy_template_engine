package chzone.template_engine.utils;

import java.io.IOException;
import java.util.Properties;

import chzone.template_engine.io.Resource;
import chzone.template_engine.io.ResourceLoader;

public class TemplateConfigLoader {
    public static void readProperties(){
        ResourceLoader rs = new ResourceLoader();
        Resource urlResource = rs.getResource(ConstantValue.TMPLATE_CONFIG_FILE_PATH);
        Properties properties = new Properties();
        try {
            properties.load(urlResource.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String templateFilePath = properties.getProperty("tmplate_path");
        System.out.println(templateFilePath);
    }
    public static void main(String[] args) {
        TemplateConfigLoader.readProperties();
    }
}
