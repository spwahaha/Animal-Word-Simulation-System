package ast;

import java.util.ArrayList;
import java.util.Random;

public class Command implements Node{

	protected ArrayList<Update> updates;
	protected Action action;
	
	public Command(ArrayList<Update> updates, Action action){
		this.updates = updates;
		this.action = action;
	}
	
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		int size = 1;
		for(Update up : updates){
			size +=up.size();
		}
		return action==null?size:size+action.size();
	}
	
	public ArrayList<Update> getUpdates(){
		ArrayList<Update> updatesCopy = new ArrayList<Update>();
		for(int i = 0; i < this.updates.size(); i++){
			updatesCopy.add(i, ((Update)updates.get(i).copy()));
		}
		return updatesCopy;
	}
	
	public Action getAction(){
		if(this.action == null) return null;
		return (Action)this.action.copy();
	}
	
	@Override
	public Node nodeAt(int index) {
		// TODO Auto-generated method stub
        if (index == 0) return this;
        index--;
        if (updates != null) {
            for (Update update : updates) {
                int updateSize = update.size();
                if (index < updateSize) return update.nodeAt(index);
                index -= updateSize;
            }
        }
        if (action != null) return action.nodeAt(index);
        throw new IllegalArgumentException("Index out of bounds");

	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		// TODO Auto-generated method stub
		for(Update up: this.updates){
			up.prettyPrint(sb);
			sb.append('\n');
		}
		if(this.action!=null)
			this.action.prettyPrint(sb);
		else{
			sb.delete(sb.length()-1, sb.length());
		}
		return sb;
	}


	@Override
	public Node copy() {
		// TODO Auto-generated method stub
		if(action!=null && updates!=null)
			return new Command(new ArrayList<Update>(updates), (Action)this.action.copy());
		if(action!=null)
			return new Command(null, (Action)this.action.copy());
		if(updates!=null)
			return new Command(new ArrayList<Update>(updates), null);
		return null;
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
		if(this.updates.size()==0){
			return this.action;
		}
		if(this.action==null){
			int size = this.updates.size();
//			System.out.println("size:" + size);
			return updates.get(rand.nextInt(size));
		}
		if(rand.nextDouble()>0.5){
			return this.action;
		}else{
			int size = this.updates.size();
//			System.out.println("size:" + size);
			return updates.get(rand.nextInt(size));
		}
	}


	@Override
	public boolean replace(Node node1, Node node2) {
		// TODO Auto-generated method stub
		if(node1 == this.updates){
			this.updates = (ArrayList)node2;
			return true;
		}
		
		return false;

	}


	@Override
	public boolean contains(Node node) {
		// TODO Auto-generated method stub
		return this.action == node || this.updates.contains(node);
	}


}
