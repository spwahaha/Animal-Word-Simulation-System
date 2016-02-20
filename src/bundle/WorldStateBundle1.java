package bundle;

/**
 * Update bundle with update since attributes
 * @author pang
 *
 */
public class WorldStateBundle1 extends WorldStateBundle {
	public int update_since;
	public WorldStateBundle1(){
		super();
	}
	
	/**
	 * set the update_since parameter
	 * @param n
	 */
	public void setUpdate(int n){
		this.update_since = n;
	}
}
