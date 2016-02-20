package interpret;

public class OutcomeImpl implements Outcome {
	protected String action;
	protected int value;
	
	public OutcomeImpl(String action, int value){
		this.action = action;
		this.value = value;
	}
	
	public String getAction(){
		return this.action;
	}
	
	public int getValue(){
		return this.value;
	}
}
