package ast;


public class NamedExpr extends NodeImp implements Expr {
	
	protected Kind kind;
	// only Smell
	public NamedExpr(Kind kind){
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
        if (index == 0) return this;
        throw new IllegalArgumentException("Index out of bounds");
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		// TODO Auto-generated method stub
		return sb.append(kind);
	}
	
	public Kind getKind(){
		return this.kind;
	}
	
	public enum Kind{
	    NEARBY("nearby"),
	    AHEAD("ahead"),
	    RANDOM("random"),
	    MEM("mem"),
	    SMELL("smell");
		
		String rep;
		Kind(String rep){
			this.rep = rep;
		}
		
		public String toString(){
			return this.rep;
		}
	}

	@Override
	public Node copy() {
		// TODO Auto-generated method stub
		return new NamedExpr(this.kind);
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
