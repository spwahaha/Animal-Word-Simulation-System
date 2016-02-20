package ast;

import java.util.Random;

public class Remove implements Mutation{

	protected Program program;
	
	public void setProgram(Program pro){
		this.program = pro;
	} 
	
	public boolean getMutated(){
		int prosize = this.program.size();
		Random rand = new Random();
		int count = 0;
		while(count<100){
			count++;
			int randindex = rand.nextInt(prosize);
			Node node = program.nodeAt(randindex);
			if(node.hasChildern()){
				Node child = node.getChildren();
				boolean succ = getMutated(node, child);
				if(succ) return true;
			}
		}
		
		return false;
		
		
	}
	
	@Override
	public boolean getMutated(Node node, Node child) {
		// TODO Auto-generated method stub
		if(child.hasChildern()){
			Node child2 = child.getChildren();
			if(child instanceof BinaryCondition || child instanceof BinaryExpr || child instanceof Command){
				boolean succ = node.replace(child, child2);
				if(succ) return true;
			}
		}
		if(child instanceof Rule ){
			boolean succ = ((ProgramImpl)node).remove(child);
			if(succ) return true;
		}
		return false;
	}
	
	
	@Override
	public boolean equals(Mutation m) {
		// TODO Auto-generated method stub
		return m instanceof Remove;
	}







}
