package bundle;

public class EntityBundle {
	public int row;
	public int col;
	public String type;
	public Integer amount;
	
	public EntityBundle(int col, int row, String type){
		this.col = col;
		this.row = row;
		this.type = type;
	}
}
