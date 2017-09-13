package chzone.template_engine.token;

import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chzone.template_engine.TemplateConstructor;

public abstract class AbstractToken implements Token {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractToken.class);
	protected String token;

	public AbstractToken(String token) {
		this.token = token;
	};

	/**
	 * 将点操作解析为dotfunc调用
	 * @return
	 */
	@Override
	public String parseDotOperation(TemplateConstructor templateConstructor, String dotExpr) {
		String codeStr = null;
		Stack<String> opStack = templateConstructor.getOpStack();
		// 链式调用转换成函数调用
		if (dotExpr.contains(".")) {
			// 多个点算一个点，以防属性名为空
			String[] dots = dotExpr.split("\\.+");
			if (dots.length > 1) {
				codeStr = "context.get(\"" + dots[0] + "\")";
				// for语句中的变量都是局部变量？
				for (int i = 1; i < dots.length; i++) {
					codeStr = String.format("dotfunc(%s,\"%s\")", codeStr.toString(), dots[i]);
				}
			} else {
				logger.error("=================dot 点表达式错误!=====================");
			}

		} else {

			if (opStack.size() > 1 && opStack.get(opStack.size() - 2).equals("for")
					&& dotExpr.equals(opStack.get(opStack.size() - 1))) {
				codeStr = dotExpr;
			} else {
				codeStr = "context.get(\"" + dotExpr + "\")";
			}

		}
		return codeStr;
	}

	@Override
	public String getTokenStart(String tokenStr) {
		return tokenStr.substring(0, 2);
	}

	@Override
	public String[] splitTokenExprBySp(String tokenStr) {
		String expr = tokenStr.substring(2, tokenStr.length() - 2).trim();
		return expr.split("\\s+");
	}
}
