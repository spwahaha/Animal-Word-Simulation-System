package bundle;

public class ObjBundle {
	public String type;
	public int row;
	public int col;
	public Integer value;
	public String species_id;
	public int[] mem;
	public String program;
	public Integer recently_executed_rule;
	public Integer id;
	public Integer direction;
	
	public ObjBundle(int col, int row, String type){
		this.row = row;
		this.col = col;
		this.type = type;
	}
	
	public void setId(int n){
		this.id = n;
	}
	
	public void setSpecies(String species){
		this.species_id = species;
	}
	
	public void setMem(int[] mem){
		this.mem = new int[8];
		for(int i = 0; i <8; i++){
			this.mem[i] = mem[i];
		}
	}
	
	public void setDir(int n){
		this.direction = n;
	}
	
	public void setProgram(String pro){
		this.program = pro;
	}
	
	public void setExecutedRule(int n){
		this.recently_executed_rule = n;
	}
}
