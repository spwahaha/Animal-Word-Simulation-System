package ast;

import java.util.Random;

public class Transform implements Mutation {

	Program program;
	@Override
	public boolean equals(Mutation m) {
		// TODO Auto-generated method stub
		return m instanceof Transform;
	}

	@Override
	public void setProgram(Program pro) {
		// TODO Auto-generated method stub
		this.program = pro;
	}

	/**The operator of this node is replaced by a valid operator
	 * or the number is replaced by a random number
	 * or action is replaced by an random action*/
	@Override
	public boolean getMutated() {
		// TODO Auto-generated method stub
		int prosize = this.program.size();
		Random rand = new Random();
		int count = 0;
		while(count<1000){
			count++;
			Node node = program.nodeAt(rand.nextInt(prosize));
			if(node.hasChildern()){
				Node child = node.getChildren();
				boolean succ = getMutated(node, child);
				if(succ) return true;
			}

						
		}
		return false;
	}

	@Override
	public boolean getMutated(Node parent, Node child) {
		// TODO Auto-generated method stub
		Random rand = new Random();
		if(child instanceof Action){
			//if child is action there are still 2 possibilities
			// unaryaction : kind + index;
			// action : kind
			int size = Action.Kind.values().length;				
			int ran = rand.nextInt(size);
			if(child instanceof UniaryAction){
				ran = 10 + rand.nextInt(2); // 10, 11
				while(Action.Kind.values()[ran].equals(((Action)child).kind)){
					ran = 10 + rand.nextInt(2);
				}
			}else{
				ran = rand.nextInt(10); // 0~9
				while(Action.Kind.values()[ran].equals(((Action)child).kind)){
					ran = rand.nextInt(10);
				} 
			}
			Action.Kind repKind = Action.Kind.values()[ran];
			((Action) child).kind = repKind;

		}
		
		if(child instanceof BinaryCondition){
			int size = BinaryCondition.Operator.values().length;
			int ran = rand.nextInt(size);
			while(BinaryCondition.Operator.values()[ran].equals(((BinaryCondition)child).op)){
				ran = rand.nextInt(size);
			}
			((BinaryCondition)child).op = BinaryCondition.Operator.values()[ran];
			return true;
		}
		if(child instanceof BinaryExpr){
			int size = BinaryExpr.Operator.values().length;
			int ran = rand.nextInt(size);
			while(BinaryExpr.Operator.values()[ran].equals(((BinaryExpr)child).op)){
				ran = rand.nextInt(size);
			}
			((BinaryExpr)child).op = BinaryExpr.Operator.values()[ran];
			return true;
		}
		
		if(child instanceof BinaryRel){
			int size = BinaryRel.Operator.values().length;
			int ran = rand.nextInt(size);
			while(BinaryRel.Operator.values()[ran].equals(((BinaryRel)child).op)){
				ran = rand.nextInt(size);
			}
			((BinaryRel)child).op = BinaryRel.Operator.values()[ran];
			return true;
		}
		
		if(child instanceof Number){
			int del = Integer.MAX_VALUE / rand.nextInt();
			((Number)child).index = rand.nextDouble()>0.5?
					((Number)child).index + del: ((Number)child).index - del;
			return true;
		}
		
		if(child instanceof NamedExpr){
			// also 2 possibilities
			// kind + index
			// kind
			int size = NamedExpr.Kind.values().length;				
			int ran = rand.nextInt(size);
			if(child instanceof UniaryAction){
				ran = rand.nextInt(4); // 0, 1, 2, 3
				while(NamedExpr.Kind.values()[ran].equals(((NamedExpr)child).kind)){
					ran = rand.nextInt(4);
				}
			}
			Action.Kind repKind = Action.Kind.values()[ran];
			return true;
		}	
		
		return false;
	}

}
