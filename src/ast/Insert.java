package ast;

import java.util.Random;

import ast.BinaryCondition.Operator;
import ast.NamedExpr.Kind;

public class Insert implements Mutation {
	Program program;
	
	@Override
	public boolean equals(Mutation m) {
		// TODO Auto-generated method stub
		return m instanceof Insert;
	}

	@Override
	public void setProgram(Program pro) {
		// TODO Auto-generated method stub
		this.program = pro;
	}

	@Override
	public boolean getMutated() {
		
		int prosize = this.program.size();
		Random rand = new Random();
		int count = 0;
		while(count<100){
			count++;
			Node node = program.nodeAt(rand.nextInt(prosize));
			if(node.hasChildern()){
				Node child = node.getChildren();
				if(child instanceof BinaryCondition){
					Node insertNode = child.copy();
					((BinaryCondition)insertNode).l = (Condition) child.copy();
					Condition insertChild = findConditionNode();
					if(insertChild==null)
						continue;
					((BinaryCondition)insertNode).r = insertChild;
					BinaryCondition.Operator op = findConditionOper();
					((BinaryCondition)insertNode).op = op;
					node.replace(child, insertNode);
					return true;
				}
				
				if(child instanceof BinaryExpr){
					Node insertNode = child.copy();
					((BinaryExpr)insertNode).l = (Expr) child.copy();
					Expr insertNodeChild = findExprNode();
					if(insertNodeChild==null)
						continue;
					((BinaryExpr)insertNode).r = (Expr)insertNodeChild;
					BinaryExpr.Operator op = findExprOper();
					
					((BinaryExpr)insertNode).op = op;
					node.replace(child, insertNode);
					return true;
				}
				
				if(child instanceof BinaryRel){
					//use child to build a binaryCondition
					
					BinaryCondition insertNode = findBinaryConditionNode();// binaryCondition
					if(insertNode == null)
						continue;
					insertNode.l = (Condition)child.copy();
					Condition insertNodeChild = findConditionNode();
					insertNode.r = insertNodeChild;
					BinaryCondition.Operator op = findConditionOper();
					
					insertNode.op = op;
					node.replace(child, insertNode);
					return true;
				}
				
				if(child instanceof NamedUnaryExpr){
					NamedExpr.Kind kind = findNamedExprKind();
					NamedUnaryExpr insertNode = new NamedUnaryExpr(kind,(Expr)child.copy());
					node.replace(child, insertNode);
				}
	
			}
			
						
		}
		return false;
		
		// TODO Auto-generated method stub
		
	}

	private Operator findConditionOper() {
		// TODO Auto-generated method stub
		int len = BinaryCondition.Operator.values().length;
		Random ran = new Random();
		return BinaryCondition.Operator.values()[ran.nextInt(len)];
	}

	private Condition findConditionNode() {
		// TODO Auto-generated method stub
		Random rand = new Random();
		int prosize = this.program.size();
		int cnt = 0;
		while(cnt<100){
			cnt++;
			Node node = program.nodeAt(rand.nextInt(prosize));
			if(node instanceof Condition){
				return (Condition)node.copy();
			}
		}
		return null;
	}
	
	private ast.BinaryExpr.Operator findExprOper() {
		// TODO Auto-generated method stub
		int len = BinaryExpr.Operator.values().length;
		Random ran = new Random();
		return BinaryExpr.Operator.values()[ran.nextInt(len)];
	}

	private Expr findExprNode() {
		// TODO Auto-generated method stub
		Random rand = new Random();
		int prosize = this.program.size();
		int cnt = 0;
		while(cnt<100){
			cnt++;
			Node node = program.nodeAt(rand.nextInt(prosize));
			if(node instanceof Expr){
				return (Expr)node.copy();
			}
		}
		return null;
	}
	
	private BinaryCondition findBinaryConditionNode() {
		// TODO Auto-generated method stub
		Random rand = new Random();
		int prosize = this.program.size();
		int cnt = 0;
		while(cnt<100){
			cnt++;
			Node node = program.nodeAt(rand.nextInt(prosize));
			if(node instanceof BinaryCondition){
				return (BinaryCondition)node.copy();
			}
		}
		return null;
	}
	
	private Kind findNamedExprKind() {
		// TODO Auto-generated method stub
		int len = NamedExpr.Kind.values().length;
		Random ran = new Random();
		return NamedExpr.Kind.values()[ran.nextInt(len-1)];
		// chose without smell
	}

	@Override
	public boolean getMutated(Node parent, Node child) {
		// TODO Auto-generated method stub
		return false;
	}

}
