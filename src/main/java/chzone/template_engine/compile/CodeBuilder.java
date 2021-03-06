package chzone.template_engine.compile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import chzone.template_engine.template.AbstractTemplate;
import chzone.template_engine.utils.ConstantValue;

public class CodeBuilder {
	private int indent_level;
	private ArrayList<String> code;
	private static final Logger logger = LoggerFactory.getLogger(CodeBuilder.class);

	public CodeBuilder() {
		this.indent_level = 0;
		code = new ArrayList<String>();
	}

	public CodeBuilder(int indent) {
		this.indent_level = indent;
		code = new ArrayList<String>();
	}

	public void indent() {
		this.indent_level += 1;
	}

	public void dedent() {
		this.indent_level -= 1;
	}

	public void add_line(String codeStr) {
		// Indent
		StringBuilder oneline = new StringBuilder();
		for (int i = 0; i < this.indent_level; i++) {
			oneline.append(ConstantValue.INDENT_STEP);
		}
		// java code
		oneline.append(codeStr);
		// line sperator
		oneline.append(ConstantValue.LINE_SEPERATOR);
		this.code.add(oneline.toString());
	}

	@Override
	public String toString() {
		StringBuilder sourceCode = new StringBuilder();
		for (String line : this.code) {
			sourceCode.append(line);
		}
		return sourceCode.toString();
	}

	public AbstractTemplate compileToJava(String javaSourceCode, String className) {
		String fullClassName = ConstantValue.COMPILED_PACKAGE_NAME + "." + className;
		// 1.get java compiler
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// 2. this is used for load class
		ClassFileManager fileManager = ClassFileManager
				.getInstance(compiler.getStandardFileManager(null, Locale.getDefault(), null));
		// java source file
		List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>();
		compilationUnits.add(new StringSourceFileObject(fullClassName, javaSourceCode));
		//
		List<String> compilationOptions = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		try {
			// 指定引用代码. 类；第二个为编译后的.class 文件路径
			sb.append(System.getProperty("java.class.path")).append(File.pathSeparator).append(
					AbstractTemplate.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String classpath = sb.toString();
		compilationOptions.addAll(Arrays.asList("-classpath", classpath));
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		// 编译
		CompilationTask compilerTask = compiler.getTask(null, fileManager, diagnostics, compilationOptions, null,
				compilationUnits);
		boolean status = compilerTask.call();
		// 执行
		if (!status) {// If compilation error occurs
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				logger.error(String.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic));
			}
		}
		try {
			fileManager.close();// Close the file manager
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// ClassLoader cl = CodeBuilder.class.getClassLoader();
			ClassLoader cl = fileManager.getClassLoader(null);
			AbstractTemplate template = (AbstractTemplate) cl.loadClass(fullClassName).newInstance();
			template.setSourceCode(javaSourceCode);
			return template;
		} catch (IllegalAccessException | InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	

}
