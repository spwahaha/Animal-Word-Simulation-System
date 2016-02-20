package ast;

import java.util.Map;
import java.util.Random;

public class BinaryRel implements Condition, Swapable{

	protected Expr l;
	protected Operator op;
	protected Expr r;
	
	public BinaryRel(Expr l, Operator op, Expr r){
		this.l = l;
		this.op = op;
		this.r = r;
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

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		// TODO Auto-generated method stub
		l.prettyPrint(sb);
    	sb.append(" ");
		sb.append(op);
    	sb.append(" ");
		r.prettyPrint(sb);
		return null;
	}
	
	public boolean replace(Node b, Node c){
		if(l==b){
			l = (Expr)c;
			return true;
		}
		if(r==b){
			r = (Expr)c;
			return true;
		}
		return false;
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
	
	
	public enum Operator{
	    LT( "<"),
	    LE( "<="),
	    EQ( "="),
	    GE( ">="),
	    GT( ">"),
	    NE( "!=");
		
		private String rep;
		
		Operator(String rep){
			this.rep = rep;
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
		return new BinaryRel((Expr)this.l.copy(),this.op,(Expr)this.r.copy());
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
		return rand.nextDouble()>0.5? this.l:this.r;
		}

	@Override
	public boolean swap() {
		// TODO Auto-generated method stub
		Expr left = this.l;
		this.l = this.r;
		this.r = left;
		return true;
	}

	@Override
	public boolean contains(Node node) {
		// TODO Auto-generated method stub
		return this.l == node || this.r == node;
	}

}
