package bundle;

import java.util.Hashtable;

import demoServlet.Authorization;
import demoServlet.Authorization.Level;
import simulation.Critter;

public class CritterBundle {
	public Integer id;
	public String species_id;
	public String program;
	public int row;
	public int col;
	public int direction;
	public int[] mem;
	public String recently_executed_rule;
	
	public void setBundle(Critter cri, int session_id, Hashtable<Integer, Integer> critterToUser, Authorization auth){
		if(cri == null) return;
		this.id = cri.getId();
		this.species_id = cri.getName();
		this.row = cri.getPosition().getRow();
		this.col = cri.getPosition().getCol();
		this.mem = new int[8];
		for(int i = 0; i < 8; i++){
			this.mem[i] = cri.getMem(i);
		}
		this.direction = cri.getDirection();
		if(critterToUser.get(cri.getId()).equals(session_id) || auth.authorize(session_id, Level.ADMIN)){
			StringBuilder sb = new StringBuilder();
			cri.getRules().prettyPrint(sb);
			this.program = sb.toString();
			sb.delete(0, sb.length());
			cri.getRules().getRule(cri.getLastRuleIndex()).prettyPrint(sb);
			this.recently_executed_rule = sb.toString();
		}
	}
}
