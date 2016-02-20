package ast;

import java.util.Map;

public class UniaryAction extends Action implements Node{

	protected Expr index;
	
	public UniaryAction(Kind kind, Expr index){
		super(kind);
		this.index = index;
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.index.size() + 1;
	}

	@Override
	public Node nodeAt(int index) {
		// TODO Auto-generated method stub
		if(index ==0 ) return this;
		return this.index.nodeAt(index - 1);
	}

	public Expr getExpr(){
		return (Expr)this.index.copy();
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		// TODO Auto-generated method stub
		sb.append(this.kind);
		sb.append("[");
		index.prettyPrint(sb);
		sb.append("]");
		return sb;
	}
	
	@Override
	public Node copy() {
		// TODO Auto-generated method stub
		return new UniaryAction(this.kind, this.index);
	}
	
	

	
}
