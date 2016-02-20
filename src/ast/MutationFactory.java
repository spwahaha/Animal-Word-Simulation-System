package ast;

import java.util.Random;


/**
 * A factory that produces the public static Mutation objects corresponding to each
 * mutation
 */
public class MutationFactory {
	
	private static final int MUTATION_NUMBER = 6;
	
	public static Mutation getRandomMutation(){
		Random rand = new Random();
		int choice = rand.nextInt(MUTATION_NUMBER);
		switch(choice){
		case 0: return getRemove();
		case 1: return getSwap();
		case 2: return getReplace();
		case 3: return getTransform();
		case 4: return getInsert();
		case 5: return getDuplicate();
		}
		return getSwap();
	}
	
	
    public static Mutation getRemove() {
        // TODO Auto-generated method stub
        return new Remove();
    }

    public static Mutation getSwap() {
        // TODO Auto-generated method stub
        return new Swap();
    }

    public static Mutation getReplace() {
        // TODO Auto-generated method stub
        return new Replace();
    }

    public static Mutation getTransform() {
        // TODO Auto-generated method stub
        return new Transform();
    }

    public static Mutation getInsert() {
        // TODO Auto-generated method stub
        return new Insert();
    }

    public static Mutation getDuplicate() {
        // TODO Auto-generated method stub
        return new Duplicate();
    }
}
