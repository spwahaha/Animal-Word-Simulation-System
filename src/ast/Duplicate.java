package ast;

import java.util.ArrayList;
import java.util.Random;

public class Duplicate implements Mutation{

	Program program;
	@Override
	public boolean equals(Mutation m) {
		// TODO Auto-generated method stub
		return m instanceof Duplicate;
	}

	@Override
	public void setProgram(Program pro) {
		// TODO Auto-generated method stub
		this.program = pro;
	}

	@Override
	public boolean getMutated() {
		// TODO Auto-generated method stub
		int prosize = this.program.size();
		Random rand = new Random();
		int count = 0;
		while(count<1000){
			count++;
			Node node = program.nodeAt(rand.nextInt(prosize));
			boolean succ =  getMutated(null,node);
			if(succ) return true;
		}
	
		return false;
	}
	

	@Override
	public boolean getMutated(Node parent, Node node) {
		Random rand = new Random();
		// TODO Auto-generated method stub
		if(node instanceof Program){
			ArrayList<Rule> rules = ((ProgramImpl)node).rules;
			Rule selectedRule = rules.get(rand.nextInt(rules.size()));
			rules.add(selectedRule);
			return true;
		}
		
		if(parent instanceof Command){
			ArrayList<Update> updates = ((Command)parent).updates;
			if(updates.size()==0)
				return false;
			Update selectedUpdate = updates.get(rand.nextInt(updates.size()));
			updates.add(selectedUpdate);
			return true;
		}
		return false;
	}

}
