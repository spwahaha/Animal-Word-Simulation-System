package ast;

import java.util.ArrayList;
import java.util.Random;

/**
 * A data structure representing a critter program.
 *
 */
public class ProgramImpl implements Program,Swapable {
	protected ArrayList<Rule> rules = new ArrayList<Rule>();
	
	
	public ProgramImpl(ArrayList<Rule> rules){
		this.rules = rules;
	}
	
	public void addRule(Rule rule){
		this.rules.add(rule);
	}
	
	public ArrayList<Rule> getRules(){
		return ((ProgramImpl)this.copy()).rules;
	}
	
	public int getRuleNumber(){
		return this.rules.size();
	}
	
    @Override
    public int size() {
        // TODO Auto-generated method stub
    	int size = 1;
    	for(Rule rule:rules){
    		size += rule.size();
    	}
        return size;
    }

    @Override
    public Node nodeAt(int index) {
        // TODO Auto-generated method stub
        if (index == 0) return this;
        index--;
        for (Rule rule : rules) {
            int ruleSize = rule.size();
            if (index < ruleSize) return rule.nodeAt(index);
            index -= ruleSize;
        }
        throw new IllegalArgumentException("Index out of bounds");
    }

    
    
    @Override
    public Program mutate() {
        // TODO Auto-generated method stub
    	Random rand = new Random();
    	int choice = rand.nextInt(6);
    	Mutation mutate = null;
    	switch(choice){
    	case 0: mutate = MutationFactory.getDuplicate(); break;
    	case 1: mutate = MutationFactory.getInsert(); break;
    	case 2: mutate = MutationFactory.getRemove(); break;
    	case 3: mutate = MutationFactory.getSwap(); break;
    	case 4: mutate = MutationFactory.getTransform(); break;
    	case 5: mutate = MutationFactory.getReplace(); break;
    	}
    	mutate.setProgram(this);
    	mutate.getMutated();
    	return this;
        
    }

    @Override
    public Program mutate(int index, Mutation m) {
        // TODO Auto-generated method stub
    	m.setProgram(this);
        Node child = nodeAt(index);
        Node parent = findParent(child);
        m.getMutated(parent, child);
        return this;
    }

    private Node findParent(Node child) {
		// TODO Auto-generated method stub
		for(int i = 0; i < this.size(); i++){
			if(nodeAt(i).contains(child)){
				return nodeAt(i);
			}
		}
		return null;
	}

	@Override
    public StringBuilder prettyPrint(StringBuilder sb) {
        // TODO Auto-generated method stub
    	for(Rule rule : this.rules){
    		rule.prettyPrint(sb);
    		sb.append(";");
    		sb.append('\n');
    	}
        return sb;
    }


	@Override
	public Program copy() {
		// TODO Auto-generated method stub
		ArrayList<Rule> newRules=new ArrayList<Rule>();
		for (int i=0; i<this.rules.size(); i++){
			newRules.add((Rule)this.rules.get(i).copy());
		}
		return new ProgramImpl(newRules); 
	}

	@Override
	public boolean hasChildern() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Node getChildren() {
		// TODO Auto-generated method stub
		int size = this.rules.size();
		Random rand = new Random();
		return rules.get(rand.nextInt(size));
		}
	
	public boolean remove(Node n){
		if(rules.size()==1){
			return false;
		}
		for(Rule r:rules){
			if(r==(Rule) n){
				rules.remove(r);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean replace(Node node1, Node node2) {
		for(int i = 0; i < this.size(); i++){
			if(rules.get(i) == node1){
				rules.set(i, ((Rule)(node2).copy()));
				return true;
			}
		}
		
		return false;
		
	}

	@Override
	public boolean swap() {
		// TODO Auto-generated method stub
		Random rand = new Random();
		int n1 = rand.nextInt(rules.size());
		int n2 = rand.nextInt(rules.size());
		Rule r1 = rules.get(n1);
		Rule r2 = rules.get(n2);
		rules.set(n1, r2);
		rules.set(n2, r1);
		return true;
	}

	@Override
	public boolean contains(Node node) {
		// TODO Auto-generated method stub
		return rules.contains(node);
	}
	
	
	public Rule getRule(int n){
		if(n >= rules.size()) return null;
		return this.rules.get(n);
	}

}
