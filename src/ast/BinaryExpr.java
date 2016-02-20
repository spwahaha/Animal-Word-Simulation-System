package ast;


import java.util.Map;
import java.util.Random;

public class BinaryExpr extends NodeImp implements Expr, Swapable {
	
	protected Expr l;
	protected Expr r;
	protected Operator op;
	
	public BinaryExpr(Expr l, Operator op, Expr r){
		this.l = l;
		this.r = r;
		this.op = op;
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.l.size() + this.r.size() + 1;
	}

	@Override
	public Node nodeAt(int index) {
		// TODO Auto-generated method stub
        if (index == 0) return this;
        index--;
        int leftSize = l.size();
        if (index < leftSize) return l.nodeAt(index);
        index -= leftSize;
        return r.nodeAt(index);
	}

	public Expr getLeft(){
		return (Expr)this.l.copy();
	}
	
	public Expr getRight(){
		return (Expr)this.r.copy();
	}
	
	public Operator getOp(){
		return this.op;
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		// TODO Auto-generated method stub
		sb.append("(");
		l.prettyPrint(sb);
    	sb.append(" ");
		sb.append(op);
    	sb.append(" ");
		r.prettyPrint(sb);
		sb.append(")");
		return sb;
	}
	
	public enum Operator{
	    PLUS("+"),
	    MINUS("-"),
	    MUL("*"),
	    DIV("/"),
	    MOD("mod");
		
		private String rep;
		
		Operator(String op){
			this.rep = op;
		}
		
		public String toString(){
			return this.rep;
		}
		
		public static final Map<String, Operator> map = 
				NodeImp.createLookupMap(values());
	    
	}

	@Override
	public Node copy() {
		// TODO Auto-generated method stub
		return new BinaryExpr((Expr)this.l.copy(),this.op, (Expr)this.r.copy());
	}

	@Override
	public boolean hasChildern() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Node getChildren() {
		// TODO Auto-generated method stub
		Random rand = new Random();
		return rand.nextDouble()>0.5?this.l:this.r;	}

	@Override
	public boolean replace(Node node1, Node node2) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	/** swap the two children*/
	public boolean swap() {
		// TODO Auto-generated method stub
		Expr left = this.l;
		this.l = this.r;
		this.r = left;
		return true;	}

	@Override
	/**return this contains node or not*/
	public boolean contains(Node node) {
		// TODO Auto-generated method stub
		return this.l == node || this.r == node;
	}

}
