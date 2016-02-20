package ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A representation of a binary Boolean condition: 'and' or 'or'
 *
 */
public class BinaryCondition extends NodeImp implements Condition, Swapable {

    /**
     * Create an AST representation of l op r.
     * @param l
     * @param op
     * @param r
     */
	protected Condition l;
	protected Operator op;
	protected Condition r;
	
    public BinaryCondition(Condition l, Operator op, Condition r) {
        //TODO
    	this.l = l;
    	this.op = op;
    	this.r = r;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
    	return this.l.size() + this.r.size()+1;
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
//    	sb.append("(");
    	l.prettyPrint(sb);
    	sb.append(" ");
		sb.append(op);
    	sb.append(" ");
		r.prettyPrint(sb);
//		sb.append(")");
    	return sb;
        }
    
	public boolean replace(Node b, Node c){
		if(l==b){
			l = (Condition)c;
			return true;
		}
		if(r==b){
			r = (Condition)c;
			return true;
		}
		return false;
	}


    /**
     * An enumeration of all possible binary condition operators.
     */
    public enum Operator {
        OR("or"),
    	AND("and");
    	
    	
    	private String rep;
    	Operator(String rep){
    		this.rep = rep;
    	}
    	
    	public String toString(){
    		return this.rep;
    	}
    	
    	public static final Map<String, Operator> BinaryConditionMap = 
    			NodeImp.createLookupMap(values());
    	
    }

	@Override
	public Node copy() {
		// TODO Auto-generated method stub
		return new BinaryCondition((Condition)this.l.copy(), this.op, (Condition)this.r.copy());
	}

	public Condition getLeft(){
		return (Condition)this.l.copy();
	}
	
	public Condition getRight(){
		return (Condition) this.r.copy();
	}
	
	public Operator getOp(){
		return this.op;
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
	/** swap the two children*/
	public boolean swap() {
		// TODO Auto-generated method stub
		Condition left = this.l;
		this.l = this.r;
		this.r = left;
		return true;	}

	@Override
	/**return this contains node or not*/ 
	public boolean contains(Node node) {
		// TODO Auto-generated method stub
		return this.l==node||this.r==node;
	}
	
	
}
