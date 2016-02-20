package ast;

import java.util.Random;

/**
 * A representation of a critter rule.
 */
public class Rule implements Node {

	protected Condition condition;
	protected Command command;
	
	public Rule(Condition cond, Command comm){
		this.condition = cond;
		this.command = comm;
	}
    @Override
    public int size() {
        // TODO Auto-generated method stub
        return this.condition.size() + this.command.size() + 1;
    }

    public Condition getCondition(){
    	return (Condition)this.condition.copy();
    }
    
    public Command getCommand(){
    	return ((Command)this.command.copy());
    }
    
    @Override
    public Node nodeAt(int index) {
        // TODO Auto-generated method stub
    	if(index ==0)
    		return this;
    	index--;
    	int consize = this.condition.size();
    	if(index < consize){
    		return condition.nodeAt(index);
    	}else{
    		return command.nodeAt(index - consize);
    	}
    }

    @Override
    public StringBuilder prettyPrint(StringBuilder sb) {
        // TODO Auto-generated method stub
    	this.condition.prettyPrint(sb);
    	sb.append(" ");
    	sb.append("-->");
    	sb.append(" ");
    	this.command.prettyPrint(sb);
    	return sb;
    }
    

	@Override
	public Node copy() {
		// TODO Auto-generated method stub
		return new Rule((Condition)this.condition.copy(), (Command)this.command.copy());
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
		return rand.nextDouble()>0.5?this.condition:this.command;	}
	@Override
	public boolean replace(Node node1, Node node2) {
		// TODO Auto-generated method stub
		if(this.condition == node1){
			this.condition = (Condition) node2;
			return true;
		}
		if(this.command == node1){
			return command.replace(node1, node2);
		}
		return false;
	}
	@Override
	public boolean contains(Node node) {
		// TODO Auto-generated method stub
		return this.condition==node || this.command==node;
	}
	
	public boolean isActionMate(){
		if(this.command.action==null) return false;
		return this.command.action.kind == Action.Kind.MATE;
	}
}
