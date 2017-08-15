package chzone.template_engine.token;

import java.util.Stack;

import chzone.template_engine.TemplateConstructor;
import chzone.template_engine.compile.CodeBuilder;

public class IfToken extends AbstractToken {

	public IfToken(String token) {
		super(token);
	}

	@Override
	public boolean match(String testedToken) {
		String startStr = getTokenStart(testedToken);
		String [] words = splitTokenExprBySp(testedToken);
		if(words[0].indexOf("if")>-1 && token.equals(startStr)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void handleToken(TemplateConstructor templateConstructor, String tokenStr) {
		
		String [] words = splitTokenExprBySp(tokenStr);
		if("if".equals(words[0])){
			handleIfStartExpr(templateConstructor,  words);
		}else if ("endif".equals(words[0])){
			handleIfEndExpr(templateConstructor);
		}else{
			logger.error("illegal 'if' expression! neigther of \"if\" or \"endif\" ");;
		}
	}

	private void handleIfStartExpr(TemplateConstructor templateConstructor, String[] words) {
		Stack<String> opStack = templateConstructor.getOpStack();
		opStack.push("if");
		if (words.length != 2) {
			logger.error("illegal 'if' expression! the length of 'if' expression can only be 2");
		}
		String codeStr = null;
		if (words[1].startsWith("!")) {
			codeStr = "!getBoolValue(" + parseDotOperation(templateConstructor,words[1].substring(1));
		} else {Boolean.valueOf(null);
			codeStr = "getBoolValue(" + parseDotOperation(templateConstructor,words[1]);
		}
		CodeBuilder code = templateConstructor.getCode();
		String javaCodeStr = "if("+codeStr + ")){";
		code.add_line(javaCodeStr);
		code.indent();
	}

	private void handleIfEndExpr(TemplateConstructor templateConstructor) {
		Stack<String> opStack = templateConstructor.getOpStack();
		if(opStack.size()>0 && "if".equals(opStack.get(opStack.size()-1)) ){
			CodeBuilder code = templateConstructor.getCode();
			code.dedent();
			code.add_line("}");
			opStack.pop();
		}else{
			logger.error("An \"if\" token is need for this \"endif\" token !");
		}
	}

}
