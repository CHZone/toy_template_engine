package chzone.template_engine.token;

import chzone.template_engine.TemplateConstructor;

public class CommentToken extends AbstractToken {

	public CommentToken(String token) {
		super(token);
	}

	@Override
	public boolean match(String testedToken) {
		if("{#".startsWith(testedToken)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void handleToken( TemplateConstructor templateConstructor, String tokenStr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String parseDotOperation(TemplateConstructor templateConstructor,
			String dotExpr) {
		// TODO Auto-generated method stub
		return null;
	}




}
