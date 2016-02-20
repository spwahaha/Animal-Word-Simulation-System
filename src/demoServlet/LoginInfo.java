package demoServlet;

public class LoginInfo {
	String level;
	String password;
	
	public void LoginInfo(String userName, String password){
		this.level = userName;
		this.password = password;
	}
}
