package ast;

import java.util.Random;

public class Replace implements Mutation{

	Program program;
	@Override
	public boolean equals(Mutation m) {
		// TODO Auto-generated method stub
		return m instanceof Replace;
	}

	@Override
	public void setProgram(Program pro) {
		// TODO Auto-generated method stub
		this.program = pro;
	}

	@Override
	public boolean getMutated() {
		// success rate is too low
		// actually, it has succeed, but maybe number is replaced by another same number, 
		// so string is the same
		// eg, mem[7] != 17 --> mem[7] := 17;  the first 7 is replaced by second 7, 
		// or first mem[7] is replaced by second mem[7], actually, they have succeed!!!
		// TODO Auto-generated method stub
		int prosize = this.program.size();
		Random rand = new Random();
		int count = 0;
		while(count<100){
			count++;
			Node node = program.nodeAt(rand.nextInt(prosize));
			if(node.hasChildern()){
				Node child = node.getChildren();
				boolean succ = getMutated(node,child);
				if(succ) return true;
			}
			
		}
		return false;
	}

	@Override
	public boolean getMutated(Node parent, Node child) {
		// TODO Auto-generated method stub
		int prosize = this.program.size();
		Random rand = new Random();
		Node similarChild = null;
		int con2 = 0;
		boolean similar = false;
		while(con2<1000){
			con2++;
			similarChild = program.nodeAt(rand.nextInt(prosize));
//			similarChild = program.nodeAt(2);

			StringBuilder sb1 = new StringBuilder();					
			StringBuilder sb2 = new StringBuilder();
			if(child == similarChild) continue;
			child.prettyPrint(sb1);
			similarChild.prettyPrint(sb2);
			
			if(sb1.toString().equals(sb2.toString()))
				continue;
			
			if(child instanceof Condition && 
					similarChild instanceof Condition){
				similar = true;
				break;
			}
			
			if(child instanceof Expr && 
					similarChild instanceof Expr){
				similar = true;
				break;
			}
			if(child instanceof Update && 
					similarChild instanceof Update){
				similar = true;
				break;
			}
			if(child instanceof Action && 
					similarChild instanceof Action){
				similar = true;
				break;
			}
			
			if(child instanceof Rule && 
					similarChild instanceof Rule){
				similar = true;
				break;
			}

		}
		
		if(similar){
			parent.replace(child, similarChild.copy());
			return true;
		}
		
		return false;
	}
	
	
	
}
