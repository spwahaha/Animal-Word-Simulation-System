package ast;

import java.util.Map;

import ast.UnaryExpr.Operator;

public class UnaryExpr extends NodeImp implements Expr{
	protected Operator op;
	protected Expr expr;
	
	public UnaryExpr(Operator op, Expr expr){
		this.op = op;
		this.expr = expr;
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.expr.size() + 1;
	}

	@Override
	public Node nodeAt(int index) {
		// TODO Auto-generated method stub
		if(index ==0){
			return this;
		}
		index--;
		int exprSize = expr.size();	
		if(index < exprSize){
			return expr.nodeAt(index);
		}
		return null;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		// TODO Auto-generated method stub
		sb.append(this.op);
		this.expr.prettyPrint(sb);
		return sb;
	}
	
	public Expr getExpr(){
		return (Expr)this.expr.copy();
	}
	
	public Operator getOp(){
		return this.op;
	}
	
	
	public enum Operator{
		NEG("-");
		String rep;
		Operator(String rep){
			this.rep = rep;
		}
		
		public String toString(){
			return this.rep;
		}
        private static final Map<String, Operator> stringToOpMap =
                NodeImp.createLookupMap(values());

        public static Operator getOperatorFromString(final String rep) {
            return stringToOpMap.get(rep);
        }
		
		
	}

	@Override
	public Node copy() {
		// TODO Auto-generated method stub
		return new UnaryExpr(this.op, (Expr)this.expr.copy());
	}

	@Override
	public boolean hasChildern() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Node getChildren() {
		// TODO Auto-generated method stub
		return this.expr;
	}

	@Override
	public boolean replace(Node node1, Node node2) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Node node) {
		// TODO Auto-generated method stub
		return this.expr == node;
	}

}
