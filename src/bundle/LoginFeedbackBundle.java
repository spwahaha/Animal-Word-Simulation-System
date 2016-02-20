package bundle;

public class LoginFeedbackBundle {
	
	protected int session_id;
	
	public LoginFeedbackBundle(int id){
		this.session_id = id;
	}
	

	
	public int getId(){
		return this.session_id;
	}
}
