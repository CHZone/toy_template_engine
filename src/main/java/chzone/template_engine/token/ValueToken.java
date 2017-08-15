package chzone.template_engine.token;

import chzone.template_engine.TemplateConstructor;

public class ValueToken extends AbstractToken {

	public ValueToken(String token) {
		super(token);
	}

	@Override
	public boolean match(String testedToken) {
		String startStr = getTokenStart(testedToken);
		if(token.equals(startStr)){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 处理表达式
	 * @param str
	 * @return
	 */

	public String buildJavaExpr(TemplateConstructor templateConstructor,String str) {
		// 原始类型 没有toString()方法
		return String.format("builder.append(getString(%s));", str);
	}

	@Override
	public void handleToken(TemplateConstructor templateConstructor, String tokenStr) {
		String expr = tokenStr.substring(2,tokenStr.length()-2).trim();
		String dot2func = parseDotOperation(templateConstructor, expr);
		String javaExpr = buildJavaExpr(templateConstructor,dot2func);
		templateConstructor.addCode(javaExpr);
	}
	
}
