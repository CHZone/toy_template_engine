package chzone.template_engine.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chzone.template_engine.error.TemplateException;





public abstract class AbstractTemplate  implements Template{
	protected final Logger logger = LoggerFactory.getLogger(AbstractTemplate.class);
	public final static String COMPILED_PACKAGE_NAME = "template";
	private String sourceCode;
	protected StringBuilder builder = new StringBuilder();
	protected Map<String, Object> context;

	public abstract void buildContent() throws TemplateException;


	public String render() throws TemplateException {
		return render(new HashMap<String, Object>());
	}

	public String render(Map<String, Object> context) throws TemplateException {
		this.context = context;
		this.builder = new StringBuilder();
		buildContent();
		return builder.toString();
	}


//	protected Object getAttribute(NodeExpressionGetAttributeOrMethod.Type type, Object object, String attribute)
//			throws AttributeNotFoundException {
//		return getAttribute(type, object, attribute, new Object[0]);
//	}

	public void setSourceCode(String source) {
		this.sourceCode = source;
	}

	public String getSourceCode() {
		return sourceCode;
	}
	
	@SuppressWarnings("unchecked")
	public Object dotfunc(Object o, String attr) {
		if(o == null){
			logger.error("null Object in \"doctfunc\"  attr:"+attr);
			return o;
		}
		Class c = o.getClass();
		Method f = null;
		try {
			f = c.getDeclaredMethod(attr);
//			f.setAccessible(true);
			try {
				return f.invoke(o);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean getBoolValue(Object o){
		if (o == null||"null".equals(String.valueOf(o))){
			return false;
		}else{
			return (boolean)o;
		}
	}
	
	public String getString(Object o){
		if("null".equals(String.valueOf(o))){
			return "";
		}else{
			return String.valueOf(o);
		}
	}
}
