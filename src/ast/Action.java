package ast;

import java.util.Map;

public class Action implements Node {

	protected Kind kind;
	
	public Action(Kind kind){
		this.kind = kind;
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Node nodeAt(int index) {
		// TODO Auto-generated method stub
		if(index == 0) return this;
        throw new IllegalArgumentException("Index out of bounds");
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		// TODO Auto-generated method stub
		sb.append(this.kind);
		return sb;
	}
	
	public Kind getKind(){
		return this.kind;
	}
	
	public enum Kind{
	    WAIT("wait"),
	    FORWARD("forward"),
	    BACKWARD("backward"),
	    LEFT("left"),
	    RIGHT("right"),
	    EAT("eat"),
	    ATTACK("attack"),
	    GROW("grow"),
	    BUD("bud"),
	    MATE("mate"),
	    TAG("tag"),
	    SERVE("serve");
		
		private String rep;
		
		Kind(String rep){
			this.rep = rep;
		}
		
		public String toString(){
			return this.rep;
		}
		
		public static final Map<String, Kind> map = 
				NodeImp.createLookupMap(values());
	}

	@Override
	public Node copy() {
		// TODO Auto-generated method stub
		return new Action(this.kind);
	}

	@Override
	public boolean hasChildern() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Node getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean replace(Node node1, Node node2) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

}
