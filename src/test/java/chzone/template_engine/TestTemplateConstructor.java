package chzone.template_engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import chzone.template_engine.error.TemplateException;
import chzone.template_engine.template.AbstractTemplate;



public class TestTemplateConstructor {
	
	@Test
	public void testText() throws TemplateException {
		TemplateConstructor tc = new TemplateConstructor("text");
		AbstractTemplate at = tc.getTemplate();
		System.out.println(at.render());
	}
	/**
	 * 直接取值{{user_name}}字符串这样的简单值
	 * 待测试：{{obj.attr,obj.func()}}
	 * 未实现：{{obj.attr|format}} 过滤器
	 * @throws TemplateException
	 */
	@Test
	public void testExpr_value() throws TemplateException{
		TemplateConstructor tc = new TemplateConstructor("expr_value");
		AbstractTemplate at = tc.getTemplate();
		Map<String,Object> context = new HashMap<String,Object>();
		context.put("user_name", "呵呵");
		System.out.println(at.render(context));
		
	}
	/**{{ product.getName }}:{{ product.getPrice }}
	 * @throws TemplateException
	 */
	@Test
	public void testExpr_func() throws TemplateException{
		TemplateConstructor tc = new TemplateConstructor("expr_dot_attr");
		AbstractTemplate at = tc.getTemplate();
		Map<String,Object> context = new HashMap<String,Object>();
		Product p = new Product("Apple", 5);
		context.put("product", p);
		System.out.println(at.render(context));
		
	}
	/**products.size = 0
	 * 注意如果products为null可能出现意外的结果
	 * @throws TemplateException
	 */
	@Test
	public void testIfFalse() throws TemplateException{
		TemplateConstructor tc = new TemplateConstructor("if");
		AbstractTemplate at = tc.getTemplate();
		Map<String,Object> context = new HashMap<String,Object>();
		ArrayList<Product> products = new ArrayList<>();
		Product p = new Product("Apple", 5);
		context.put("products", products);
		System.out.println(at.render(context));
		
	}
	/**
	 * @throws TemplateException
	 */
	@Test
	public void testIfTrue() throws TemplateException{
		TemplateConstructor tc = new TemplateConstructor("if");
		AbstractTemplate at = tc.getTemplate();
		Map<String,Object> context = new HashMap<String,Object>();
		ArrayList<Product> products = new ArrayList<>();
		Product p = new Product("Apple", 5);
		products.add(p);
		context.put("products", products);
		System.out.println(at.render(context));
		
	}
	/** for
	 * @throws TemplateException
	 */
	@Test
	public void testFor() throws TemplateException{
		TemplateConstructor tc = new TemplateConstructor("for");
		AbstractTemplate at = tc.getTemplate();
		Map<String,Object> context = new HashMap<String,Object>();
		ArrayList<Product> products = new ArrayList<>();
		Product p = new Product("Apple", 5);
		products.add(p);
		products.add(p);
		context.put("products", products);
		System.out.println(at.render(context));
	}
	/** if&for
	 * @throws TemplateException
	 */
	@Test
	public void testIfFor() throws TemplateException{
		TemplateConstructor tc = new TemplateConstructor("if_for");
		AbstractTemplate at = tc.getTemplate();
		Map<String,Object> context = new HashMap<String,Object>();
		ArrayList<Product> products = new ArrayList<>();
		Product p = new Product("Apple", 5);
		products.add(p);
		products.add(p);
		context.put("products", products);
		System.out.println(at.render(context));
		
	}
	/** if&for
	 * @throws TemplateException
	 */
	@Test
	public void testForIf() throws TemplateException{
		TemplateConstructor tc = new TemplateConstructor("for_if");
		AbstractTemplate at = tc.getTemplate();
		Map<String,Object> context = new HashMap<String,Object>();
		ArrayList<Product> products = new ArrayList<>();
		Product p = new Product("Apple", 5);
		products.add(p);
		products.add(p);
		context.put("products", products);
		System.out.println(at.render(context));
		
	}
	public static void main(String[] args) throws TemplateException {
		TemplateConstructor tc = new TemplateConstructor("template.html");
		AbstractTemplate at = tc.getTemplate();
		Map<String,Object> context = new HashMap<String,Object>();
		context.put("title", "stupid toy template engine");
		List<String> syntaxList = new ArrayList<String>();
		syntaxList.add("{{value}}");
		syntaxList.add("{% if expression %} .... {% endif %}");
		syntaxList.add("{% for item in  collection %} .... {% end for %}");
		context.put("syntaxList", syntaxList);
		System.out.println(at.render(context));
	}
}
