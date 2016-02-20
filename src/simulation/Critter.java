package simulation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import ast.Mutation;
import ast.MutationFactory;
import ast.Program;
import ast.ProgramImpl;
import ast.Rule;
import interpret.InterpreterImpl;
import interpret.Outcome;
import parse.Parser;
import parse.ParserImpl;
import parse.Tokenizer;

public class Critter implements Placeable {
	protected int[] mem;
	//0 memsize
	//1 defense
	//2 offense
	//3 size
	//4 energy
	//5 pass
	//6 tag  should be 0~99 
	//posture
	protected HexCoord position;
	protected Program rules;
	protected int Direction;
	protected World world;
	protected String name;
	protected int lastRuleIndex;
	public static final int MAX_TAG_VALUE = 99;
	protected static final int MAX_POSTURE_VALUE = 99;
	protected static final int INITIAL_SIZE = 1;
	protected static final double MUTATION_PROBABILITY = 1.0 / 4;
	protected static final int MUTATION_CATEGORY = 2;
	protected static final int MUTATION_ATTR_CATEGORY = 3;
	public static final int MAX_MEM_INEDX = 7;
	protected int id;
	protected String StrProgram;
	/**
	 * Construct a critter with initial information
	 */
	public Critter(){
		this.mem = new int[8];
		this.name = "";
		this.mem[3] = 1;
	}
	
	/**
	 * Construct a critter given the filename that contains the critter info
	 * 
	 * @param fileName the name of the file that contains the critter info
	 * @throws IOException if filename if not correct
	 */
	public Critter(String fileName) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String[] lines = new String[7];
		for(int i = 0; i < lines.length; i++){
			lines[i] = br.readLine();
		}
		System.out.println(fileName);
		String name = "";
		int memsize =0;
		int defense = 0;
		int offense = 0;
		int size = 0;
		int energy = 0;
		int posture = 0;
		
		for(int i = 0; i < lines.length; i++){
			String[] attri = lines[i].split(":");
			switch(attri[0].toLowerCase()){
			case "species": name = attri[1]; break;
			case "defense": defense = Integer.parseInt(attri[1].trim()); break;
			case "offense": offense = Integer.parseInt(attri[1].trim()); break;
			case "size":   size = Integer.parseInt(attri[1].trim()); break;
			case "energy": energy = Integer.parseInt(attri[1].trim()); break;
			case "posture": posture = Integer.parseInt(attri[1].trim()); break;

			}
		}
		
		Tokenizer t = null;
		t = new Tokenizer(br);

		Parser parser = new ParserImpl();
		Program prog = parser.parse(t);
		this.mem = new int[8];
		this.setName(name);
		this.setMem(0, memsize);
		this.setMem(1, defense);
		this.setMem(2, offense);
		this.setMem(3, size);
		this.setMem(4, energy);
		this.setMem(7, posture);
		this.setProgram(prog);
		this.setDirection(World.RAND.nextInt(World.MAX_DIRECTION));
//		System.out.println("critter success");
		t.close();
	}
	
	/**
	 * Set the position to the critter
	 * 
	 * @param posi the position of the critter
	 */
	public void setPosition(HexCoord posi){
		this.position = posi;
	}
	
	/**
	 * Set the world that critter exists
	 * 
	 * @param world the world that critter exists
	 */
	public void setWorld(World world){
		this.world = world;
	}
	
	/**
	 * Set the name of the critter
	 * 
	 * @param name the name of the critter
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Set the memory information for the critter
	 * 
	 * @param index the memory index
	 * @param n the value of the certain memory location
	 */
	public void setMem(int index, int n){
		
		if(index < 0 || index > 7) return;
		switch(index){
		case 0:{
			if(n >=8) this.mem[0] = n;
			return;
		}
		case 6:
		case 7:
			{
				if(n >=0 && n <= 99) this.mem[index] = n;
				return;
			}
		default: 
			this.mem[index] = n; return;
		}
		
	}
	
	/**
	 * Set the program of the critter
	 * 
	 * @param pro the program of the critter
	 */
	public void setProgram(Program pro){
		this.rules = pro;
	}
	
	/**
	 * Get the memory info of certain location
	 * 
	 * @param index the index of the memory info to be gotten
	 * @return the memory info
	 */
	public int getMem(int index){
		return this.mem[index];
	}
	
	
	/**
	 * set id of the critter
	 * @param n id to be set
	 */
	public void setId(int n){
		this.id = n;
	}	
	
	/**
	 * Get the direction of the critter
	 * 
	 * @return the direction of the critter
	 */
	public int getDirection(){
		return this.Direction;
	}
	
	/**
	 * Set the direction of the critter
	 * 
	 * @param dir the direction to be set
	 */
	public void setDirection(int dir){
		this.Direction = dir;
	}
	
	/**
	 * Nearby sensor
	 * 
	 * @param dir direction to be sensed
	 * @return the nearby information at certain dirction
	 */
	public int nearby(int dir){
		int c = this.position.col;
		int r = this.position.row;
		int[][] possiblePosi = {{c,r+1},
								{c+1,r+1},
								{c+1,r},
								{c,r-1},
								{c-1,r-1},
								{c-1,r}};

		HexCoord nearPosi = new HexCoord();
		int dirsum = this.Direction + dir;
		nearPosi.setCol(possiblePosi[dirsum % 6][0]); 		
		nearPosi.setCol(possiblePosi[dirsum % 6][1]);
		return getPosiInfo(nearPosi);
		
	}
	
	/**
	 * Ahead sensor
	 * 
	 * @param dist the distance to sense
	 * @return the ahead information of certain distance
	 */
	public int ahead(int dist){
		int c = this.position.col;
		int r = this.position.row;
		int[][] possiblePosi = {{c,r+1},
								{c+1,r+1},
								{c+1,r},
								{c,r-1},
								{c-1,r-1},
								{c-1,r}};
		for(int i = 0; i < possiblePosi.length; i++){
			possiblePosi[i][0] = possiblePosi[i][0] - c;
			possiblePosi[i][1] = possiblePosi[i][1] - r;			
		}
		int disc = possiblePosi[this.Direction][0];
		int disr = possiblePosi[this.Direction][1];
		
		for(int i = 0; i < dist; i++){
			c = c + disc;
			r = r + disr;
		}
		
		HexCoord aheadPosi = new HexCoord(c,r);
		return getPosiInfo(aheadPosi);

	}
	
	/**
	 * Get the world information of certain hex
	 * 
	 * @param posi the position to be inspected
	 * @return the hex information
	 */
	private int getPosiInfo(HexCoord posi){
		if(!this.world.validPosi(posi)) return World.ROCK_VALUE; // if it's out of world, it's a rock!!
		Placeable place = world.getObj(posi);
		if(place == null) return 0;
		if(place instanceof Critter){
			((Critter)place).getAppearance();
		}
		if(place instanceof Rock){
			return World.ROCK_VALUE;
		}
		if(place instanceof Food){
			return (-((Food)place).getFoodValue()) -1;
		}
		return 0;
	}
	
	/**
	 * Smell action
	 * @return the smell information
	 */
	public int smell(){
		return 0;
	}
	
	/**
	 * Get the name of the critters
	 * 
	 * @return the name of the critter
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Get the id of the critter
	 * @return critter id
	 */
	public int getId(){
		return this.id;
	}
	
	/**
	 * Print the memory information of the critter
	 */
	public StringBuilder printMem(){
		StringBuilder sb = new StringBuilder();
		sb.append("The first 8 memory is: ");
		sb.append('\n');
		sb.append("Memory size: " + this.mem[0]+'\n');
		sb.append("Defensive ability: " + this.mem[1] + '\n');
		sb.append("Offensive ability: " + this.mem[2] + '\n');
		sb.append("Size: " + this.mem[3] + '\n');
		sb.append("Energy: " + this.mem[4] + '\n');
		sb.append("Pass number: " + this.mem[5] + '\n');
		sb.append("Tag: " + this.mem[6] + '\n');
		sb.append("Posture: " + this.mem[7] + '\n');
//		System.out.println("The first 8 memory is ");
//		System.out.println("Memory size: " + this.mem[0]);
//		System.out.println("Defensive ability: " + this.mem[1]);
//		System.out.println("Offensive ability: " + this.mem[2]);
//		System.out.println("Size: " + this.mem[3]);
//		System.out.println("Energy: " + this.mem[4]);
//		System.out.println("Pass number: " + this.mem[5]);
//		System.out.println("Tag: " + this.mem[6]);
//		System.out.println("Posture: " + this.mem[7]);
		return sb;
	}
	
	/**
	 * Get the program of the critter
	 * 
	 * @return the program of the critter
	 */
	public Program getRules(){
		return this.rules;
	}
	
	/**
	 * Random action
	 * 
	 * @param n the parameter of the random action
	 * @return the result of random action
	 */
	public int random(int n){
		if(n < 2) return 0;
		return World.RAND.nextInt(n);
	}
	
	/**
	 * Get the appearance of the critter
	 * 
	 * @return the appearance
	 */
	public int getAppearance(){
		return this.mem[4] * 100000 + this.mem[6] * 1000 + mem[7] * 10;
	}
	
	/**
	 * Set the last rule index executed by the critter
	 * @param n the index of the last executed rule
	 */
	public void setLastRule(Integer n){
		if(n==null){
			this.lastRuleIndex = 0;
			return;
		}
		this.lastRuleIndex = n;
	}
	
	public void setStrProgram(String str){
		this.StrProgram = str;
	}
	
	
	/**
	 * Get the index of the last executed rule
	 * 
	 * @return the index of the last executed rule
	 */
	public int getLastRuleIndex(){
		return this.lastRuleIndex;
	}
	
	/**
	 * Get the position of the critter
	 * 
	 * @return position of the critter
	 */
	public HexCoord getPosition(){
		return this.position;
	}
	
	/**
	 * Get the complexity of the critter
	 * 
	 * @return the complexity of the critter
	 */
	public int getComplexity(){
		int r = ((ProgramImpl)this.rules).getRuleNumber();
		int offense = this.getMem(2);
		int defense = this.getMem(1);
		int complexity = r * World.RULE_COST + 
							(offense + defense) * World.ABILITY_COST;
		return complexity;
	}
	
	/**
	 * Execute the bud action
	 * 
	 * @return the child of the critter
	 */
	public Critter bud(){
		Critter child = new Critter();
		child.setMem(0, this.mem[0]);
		child.setMem(1, this.mem[1]);
		child.setMem(2, this.mem[2]);
		child.setMem(3, Critter.INITIAL_SIZE); // size
		child.setMem(4, World.INITIAL_ENERGY); // energy
		child.setMem(5, 0);
		child.setMem(6, 0); // tag
		child.setMem(7, 0); // posture
		child.rules = this.rules.copy();
		child.lastRuleIndex = 0;
		child.setName(this.name + "'s child");
		child.world = this.world;
		child.position = World.getBackPosi(this);
		child.Direction = World.RAND.nextInt(World.MAX_DIRECTION);
		// bud with mutation
		critterMutation(child);
		return child;
		
	}

	/**
	 * Mutate a critter
	 * 
	 * @param cri the critter that to be mutated
	 */
	private static void critterMutation(Critter cri) {
		// TODO Auto-generated method stub
		double prob = World.RAND.nextDouble();
		if(prob <= Critter.MUTATION_PROBABILITY){
			int category = World.RAND.nextInt(Critter.MUTATION_CATEGORY);
			switch(category){
				case 0:{ // change attribute
					// memory, defense, offense is 0, 1, 2
					int attribute = World.RAND.nextInt(Critter.MUTATION_ATTR_CATEGORY);
					int increment = World.RAND.nextInt(2) == 0? -1:+1;// 
					cri.setMem(attribute, cri.getMem(attribute) + increment);
					break;
				}
				case 1:{
					cri.rules.mutate();
					break;
				}
				default: return;
			}
			critterMutation(cri);
		}
		
	}
	
	/**
	 * Returns true if this critter want to mate
	 * 
	 * @return true if this critter want to mate
	 */
	public boolean wantMate(){
		InterpreterImpl inter = new InterpreterImpl(this,this.rules);
		Outcome out = inter.interpret(this.rules,false);//interpret but do not update info
		return out.getAction().equalsIgnoreCase("mate");
	}
	
	/**
	 * Execute mate action for 2 critters
	 * 
	 * @param cri the one critter that want to mate
	 * @param bride the other critter that want to mate
	 * @param childPosi the child position 
	 * @param world the world that these critters exist
	 * @return child critter
	 */
	public static Critter mate(Critter cri, Critter bride, HexCoord childPosi, World world){

		Critter child = new Critter();
		child.setProgram(getChildProgram(cri,bride));
		int attr = World.RAND.nextInt(2);
		switch(attr){
			case 0:{
				child.setMem(0, cri.getMem(0));
				child.setMem(1, cri.getMem(1));
				child.setMem(2, cri.getMem(2));		
				break;
			}
			case 1:{
				child.setMem(0, cri.getMem(0));
				child.setMem(1, cri.getMem(1));
				child.setMem(2, cri.getMem(2));	
				break;
			}
			default: return null;
		}
		
		child.setMem(3, Critter.INITIAL_SIZE); // size
		child.setMem(4, World.INITIAL_ENERGY); // energy
		child.setMem(5, 0);
		child.setMem(6, 0); // tag
		child.setMem(7, 0); // posture
		
		child.lastRuleIndex = 0;
		child.setName(cri.name +" and " + bride.name + "'s child");
		child.world = world;
		child.position = childPosi;
		child.Direction = World.RAND.nextInt(World.MAX_DIRECTION);
		critterMutation(child);
		
		return child;
	}
	
	/**
	 * Get the program of the new born child
	 * 
	 * @param cri the father of the child
	 * @param bride the mother of the child
	 * @return the program of child
	 */
	private static Program getChildProgram(Critter cri, Critter bride){
		ArrayList<Rule> criRules = cri.rules.getRules();
		ArrayList<Rule> brideRules = bride.rules.getRules();
		ArrayList<Rule> childRules = new ArrayList<Rule>();
		int criSize = criRules.size();
		int briSize = brideRules.size();
		int childRuleSize = World.RAND.nextInt(2)==0? criSize: briSize;
		
		ArrayList<Rule> shortRules;
		ArrayList<Rule> longRules;
		int shortSize = 0;
		int longSize = 0;
		if(criSize < briSize){
			shortRules = criRules;
			longRules = brideRules;
			shortSize = criSize;
			longSize = briSize;
		}else{
			longRules = criRules;
			shortRules = brideRules;
			longSize = criSize;
			shortSize = briSize;
		}
		
		for(int i = 0; i < shortSize; i++){
			Rule temp = World.RAND.nextInt(2)==0?shortRules.get(i):
											longRules.get(i);
			childRules.add(temp);
		}
		
		if(childRuleSize != shortSize){ 
		// childRuleSize equals to long size, then add the extra part to childRule
			for(int i = shortSize; i < longSize; i++){
				childRules.add(longRules.get(i));
			}
		}
		
		return new ProgramImpl(childRules);
	}
	
	
}
