package chzone.template_engine.token;

import chzone.template_engine.TemplateConstructor;

public interface Token {
	public  boolean match(String testedToken);
	public abstract void handleToken(TemplateConstructor templateConstructor, String tokenStr);
	public String parseDotOperation(TemplateConstructor templateConstructor,String dotExpr);
	/**
	 * 获取token的开始标记
	 * @param tokenStr
	 * @return
	 */
	public abstract String getTokenStart(String tokenStr);
	/** 按照空格拆分表达式
	 * @param tokenStr
	 * @return
	 */
	public abstract String[] splitTokenExprBySp(String tokenStr);
}
