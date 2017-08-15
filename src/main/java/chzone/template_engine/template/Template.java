package chzone.template_engine.template;

import java.util.Map;

import chzone.template_engine.error.TemplateException;


public interface Template {
	
	public final static String COMPILED_PACKAGE_NAME = "com.mitchellbosecke.pebble.template.compiled";
	
	public String render() throws TemplateException;

	public String render(Map<String, Object> model) throws TemplateException;
}
