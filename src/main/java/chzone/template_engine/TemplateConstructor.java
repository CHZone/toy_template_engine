package chzone.template_engine;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;

import chzone.template_engine.compile.CodeBuilder;
import chzone.template_engine.error.TemplateException;
import chzone.template_engine.template.AbstractTemplate;
import chzone.template_engine.token.AbstractToken;
import chzone.template_engine.token.CommentToken;
import chzone.template_engine.token.ForToken;
import chzone.template_engine.token.IfToken;
import chzone.template_engine.token.ValueToken;
import chzone.template_engine.utils.ConstantValue;



public class TemplateConstructor {                               
	private static final Logger logger = LoggerFactory.getLogger(TemplateConstructor.class);
	private String templateName;
	private CodeBuilder code;
	private String textSource;
	private Stack<String> opStack = new Stack<>();
	private static List<AbstractToken> tokenList = new ArrayList<>();
	
	// 加载模板支持的标记列表
	static {
		tokenList.add(new CommentToken("{#"));
		tokenList.add(new ValueToken("{{"));
		tokenList.add(new IfToken("{%"));
		tokenList.add(new ForToken("{%"));
	}
	/**构造函数，加载模板文本，解析模板文件，生成模板类源码
	 * @param templateName
	 */

	public TemplateConstructor(String templateName) {
		this.templateName = templateName;
		// 加载模板文件
		String text = loadTemplateSource(templateName);
		// 解析模板
		parsTemplate(text);
	}
	/**
	 * 读取模板文件返回模板文本,太混乱了
	 * 
	 * @param templateName
	 * @return
	 */
	public String loadTemplateSource(String templateName) {
		this.templateName = templateName;
		String path = ConstantValue.TEMPLATE_FILE_PATH;
		String location = path + File.separatorChar + templateName;
		logger.info("Looking for template in {}.", location);
		File file = new File(path, templateName);
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			logger.info("InputStream error");
			e1.printStackTrace();
		}
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			logger.info("Reader error");
			e1.printStackTrace();
		}
		String source = null;
		try {
			source = IOUtils.toString(reader);
		} catch (IOException e) {
			// throw new LoaderException("Template can not be found.");
			logger.info("get Source error");
			e.printStackTrace();
		}
		this.textSource = source;
		logger.info(ConstantValue.LINE_SEPERATOR+source);
		logger.info("=====模板文本分割===========================");
		return source;
	}

	private void parsTemplate(String text) {
		// { 需要转义，转义要用双斜线
		String patternReg = "(?s)(\\{\\{.*?\\}\\}|\\{%.*?%\\}|\\{#.*?#\\})";
		Pattern p = Pattern.compile(patternReg);
		int currIndex = 0;
		Matcher m = p.matcher(text);
		code = new CodeBuilder();
		// 添加头部代码
		addHeadCode();
		while (m.find()) {
			// 处理token前面的文本
			handleText(text.substring(currIndex, m.start()+2));
			// 处理token String
			String tokenStr = text.substring(m.start(), m.end());
			// 遍历token列表，在匹配的类型中处理token语句
			for (AbstractToken token : tokenList) {
				if(token.match(tokenStr)){
					token.handleToken(this, tokenStr);
				}
			}
			currIndex = m.end();
		}
		if(opStack.size()>0){
			logger.error("===========有if或for语句没有结束===============");
		}
		addTailCode(text.substring(currIndex));
	}
	
	/**
	 * 添加Java源码的头部信息
	 */
	public void addHeadCode() {
		code.add_line("package " + ConstantValue.COMPILED_PACKAGE_NAME + ";");
		code.add_line("import java.util.Map;");
		code.add_line("import java.util.HashMap;");
		code.add_line(String.format("public class %s extends %s{", "Template_" + templateName,
				AbstractTemplate.class.getName()));
		code.indent();
		code.add_line(String.format("public void buildContent() throws %s {", TemplateException.class.getName()));
		code.indent();
	}
	
	private void addTailCode(String text) {
		// 剩余的文本
		// code.add_line(formatText(text.substring(currIndex)));
		handleText(text);
		// 收尾
		code.dedent();
		// 方法收尾
		code.add_line("}");
		// class 收尾
		code.dedent();
		code.add_line("}");
	}
	
	/**
	 * 还是换成单行处理
	 * 纯文本处理
	 * Java中不支持多行字符串,此处需要添加4个斜线在字符串中表示2个斜线字符，并添加到模板的builder中，成为最终模板输出文本中的一个斜线
	 * 此处将换行进行转义后悔会让模板类的代码更加紧凑，紧凑处理与功能无关。
	 *  builder.append("<p>Welcome, test for!</p>\n");
     *  builder.append("<p>Products:</p>\n");
     *  builder.append("<ul>");
	 * 紧凑处理后的效果
	 * builder.append("<p>Welcome, test for!</p>\n<p>Products:</p>\n<ul>\n");
	 * @param str
	 */
	public void handleText(String str) {
		logger.error(str);
		// 1. if和for独占一行值后面的换行符需要去除
		// 2. if和for嵌套时，前面的空格也要去除
		// 许多token会独占一行，去掉token后换行符还在，会导致渲染出来的文本有多余的空行需去除
		// 针对2
//		String reg = "^\\n\\s*?\\{$";
//		Pattern pattern = Pattern.compile(reg);
//		Matcher matcher = pattern.matcher(str);
//		if(matcher.find()){
//			return;
//		}
//		if(str.startsWith(ConstantValue.LINE_SEPERATOR)&&str.endsWith("{%")){
//			return;
//		}
		// 去掉 {%或{{
		if(str.length()>2){
			str = str.substring(0, str.length()-2);
		}
		// 针对1
		if(str.startsWith(ConstantValue.LINE_SEPERATOR)){
			str = str.substring(ConstantValue.LINE_SEPERATOR.length());
		}
		str = str.replaceAll(ConstantValue.LINE_SEPERATOR, "\\\\n");
		code.add_line(inLineText(str));
	}
	/**
	 * 行内文本，不换行
	 * 构建显示纯文本Java代码
	 */
	public String inLineText(String str) {
		return String.format("builder.append(\"%s\");", str);
	}
	
	public void addCode(String codeStr){
		this.code.add_line(codeStr);
	}
	public Stack<String> getOpStack() {
		return opStack;
	}
	/**
	 * 返回编译好的模板类，以供渲染。
	 * 改进：添加模板管理功能，按照名称获取
	 * @return
	 */
	public AbstractTemplate getTemplate() {
		System.out.println(code.toString());
		return code.compileToJava(code.toString(), "Template_" + this.templateName);
	}
	public String getTextSource() {
		return textSource;
	}
	public CodeBuilder getCode() {
		return code;
	}
	
}
