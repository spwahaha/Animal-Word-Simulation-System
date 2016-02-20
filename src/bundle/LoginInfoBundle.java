package bundle;

import demoServlet.Authorization;

public class LoginInfoBundle {
	protected String level;
	protected String password;
	
	public LoginInfoBundle(String level, String password){
		this.level = level;
		this.password = password;
	}
	
	public String getLevel(){
		return this.level;
	}
	
	public String getPassword(){
		return this.password;
	}
}
