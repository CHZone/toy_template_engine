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
import chzone.template_engine.io.Resource;
import chzone.template_engine.io.ResourceLoader;
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
		this.templateName = templateName.replaceAll("[.]", "_");
		logger.info("Template Class Name: "+this.templateName);
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
		String path = ConstantValue.TEMPLATE_FILE_PATH;
		String location = path + File.separatorChar + templateName;
		logger.info("Looking for template in {}.", location);
		InputStream is = null;
		try {
		    ResourceLoader resourceLoader = new ResourceLoader();
		    Resource urlResource = resourceLoader.getResource(location);
			is = urlResource.getInputStream();
		} catch (Exception e1) {
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
			handleText(text.substring(currIndex, m.start()));
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
		handleText(text);
		// 降低缩进级别
		code.dedent();
		// 方法结束
		code.add_line("}");
		// 降低缩进级别
		code.dedent();
		// class 结束
		code.add_line("}");
	}
	
	/**str中的换行符会使得生产的Java源码处理换行，导致编译时报语法错误
	 * 此处进行转义,至于为什么要4个'\',我还在坑里面没有爬出来。
	 * 谁想通了一定要告诉我！
	 * 转义后可以减少生成的 代码的行数。
	 * @param str
	 */
	public void handleText(String str) {
		str = str.replaceAll(ConstantValue.LINE_SEPERATOR, "\\\\n");
		code.add_line(inLineText(str));
	}
	/**
	 * str 可能包含多行，不能直接放到append中，因此拆分成多行，逐行添加
	 * 空行产生的原因复杂，一并放到渲染后的内容中处理
	 * @param str
	 */
	public void handleText2(String str) {
		String[] strList = str.split(ConstantValue.LINE_SEPERATOR);
		if (strList.length<1){
			return;
		}
		for (int i = 0; i < strList.length - 1; i++) {
			code.add_line(exclusiveLineText(strList[i]));
		}
		
		code.add_line(inLineText(strList[strList.length - 1]));
	}
	/**
	 * 文本独占一行
	 * 构建显示纯文本Java代码
	 */
	public String exclusiveLineText(String str) {
		return String.format("builder.append(\"%s\\n\");", str);
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
		logger.info("=====模板文本分割===========================");
		logger.info(ConstantValue.LINE_SEPERATOR+code.toString());
		logger.info("=====模板文本分割===========================");
		return code.compileToJava(code.toString(), "Template_" + this.templateName);
	}
	public String getTextSource() {
		return textSource;
	}
	public CodeBuilder getCode() {
		return code;
	}
	
}
