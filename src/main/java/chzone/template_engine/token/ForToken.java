package chzone.template_engine.token;

import java.util.Stack;

import chzone.template_engine.TemplateConstructor;
import chzone.template_engine.compile.CodeBuilder;

public class ForToken extends AbstractToken {

	public ForToken(String token) {
		super(token);
	}

	@Override
	public boolean match(String testedToken) {
		String startStr = getTokenStart(testedToken);
		String[] words = splitTokenExprBySp(testedToken);
		if (words[0].indexOf("for") > -1 && token.equals(startStr)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void handleToken(TemplateConstructor templateConstructor, String tokenStr) {
		String[] words = splitTokenExprBySp(tokenStr);
		if ("for".equals(words[0])) {
			handleForStartExpr(templateConstructor, words);
		} else if ("endfor".equals(words[0])) {
			handleForEndExpr(templateConstructor);
		} else {
			logger.error("illegal 'for' expression! neigther of \"if\" or \"endif\" ");
			;
		}
	}

	private void handleForStartExpr(TemplateConstructor templateConstructor, String[] words) {
		Stack<String> opStack = templateConstructor.getOpStack();
		if (words.length != 4) {
			logger.error("illegal 'for' expression! the length of 'if' expression can only be 2");
		}
		CodeBuilder code = templateConstructor.getCode();
		String javaCodeStr = String.format("for(Object %s:(java.util.Collection)context.get(\"%s\")){",
				words[1],words[3]);
		code.add_line(javaCodeStr);
		code.indent();
		String javaCodeSaveLocalValue = String.format("context.put(\"%s\",%s);", words[1],words[1]);
		code.add_line(javaCodeSaveLocalValue);
		opStack.push("for");
	}

	private void handleForEndExpr(TemplateConstructor templateConstructor) {
		Stack<String> opStack = templateConstructor.getOpStack();
		if(opStack.size()>0 && "for".equals(opStack.get(opStack.size()-1)) ){
			CodeBuilder code = templateConstructor.getCode();
			code.dedent();
			code.add_line("}");
			opStack.pop();// pop words[1]
		}else{
			logger.error("An \"if\" token is need for this \"endif\" token !");
		}
	}
	
	
}
