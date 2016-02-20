package demoServlet;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletContext;

public class Authorization{
	// userInfo saves user name and password
	protected Hashtable<String,String> userInfo;
	// saves relation of user sessionId and level
	protected Hashtable<Integer,Level> authInfo;
	// the number of user in the authorization systems
	protected int userSize;
	
	
	public Authorization(ServletContext servletContext){
		userSize = 0;
		userInfo = new Hashtable<String,String>();
		Enumeration userNames = servletContext.getInitParameterNames();
		while(userNames.hasMoreElements()){
			String userLevel  = (String) userNames.nextElement();
			String password = servletContext.getInitParameter(userLevel);
			System.out.println(userLevel + ": " + password);
			userInfo.put(userLevel, password);
		}
		this.authInfo = new Hashtable<Integer,Level>();
		System.out.println("Authorization system initialized");
	}
	
	/**
	 * authorize the user limits authority 
	 * @param sessionId sessionId of the user
	 * @param level the input level of sessionId
	 * @return whether sessionId matches the user level
	 */
	
	public boolean authorize(int sessionId, Level level){
		Level realLevel = authInfo.get(sessionId);
		if(realLevel==null) return false;
		return realLevel.equals(level);
	}
	
	
	public int login(String userName,String password){
		String realPassword = this.userInfo.get(userName);
		if(realPassword == null) return -1;
		if(password.equals(realPassword)){
			int id = getSessionId();
			if(userName.equals("admin")){
				this.authInfo.put(id, Level.ADMIN);
			}else if(userName.equals("read")){
				this.authInfo.put(id, Level.READ);
			}else if(userName.equals("write")){
				this.authInfo.put(id, Level.WRITE);
			}
			return id;
		}
		return -1;
	}
	
	private int getSessionId() {
		// TODO Auto-generated method stub
		userSize++;
		return userSize;
	}

	public enum Level{
		ADMIN,READ,WRITE;
	}
}
