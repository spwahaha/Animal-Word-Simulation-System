package simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ast.ProgramImpl;
import ast.Rule;
import interpret.InterpreterImpl;
import interpret.Outcome;

public class World {
	
	protected static int BASE_DAMAGE;
	protected static double DAMAGE_INC;
	protected static int ENERGY_PER_SIZE;
	protected static int FOOD_PER_SIZE;
	protected static int MAX_SMELL_DISTANCE;
	protected static int ROCK_VALUE;
	protected static int COLUMNS;
	protected static int ROWS;
	public static int MAX_RULES_PER_TURN;
	protected static int SOLAR_FLUX;
	protected static int MOVE_COST;
	protected static int ATTACK_COST;
	protected static int GROW_COST;
	protected static int BUD_COST;
	protected static int MATE_COST;
	protected static int RULE_COST;
	protected static int ABILITY_COST;
	protected static int INITIAL_ENERGY;
	protected static int MIN_MEMORY;
	protected static final int MAX_DIRECTION = 6;
	private static final String RANDOM_WORLD_NAME = "random world";
	private static final double RANDOM_ROCK_FACTOR = 1.0/5;
	
	protected int critterId;
	protected Hashtable<HexCoord, Placeable> map;
	protected int Col; // the col and row number of the world, 0 ~ col - 1, 0 ~ row-1
	protected int Row; // a hexcoord maybe out scope 
	protected ArrayList<Critter> critters = new ArrayList<Critter>();
	protected String name;
	protected String constantFileName = "D:/workspace/cs2112/12A7/examples/constants.txt";
	public static Random RAND = new Random();
	protected int steps;
	int size; // The object number in the world
	int maxSize;
	protected HashSet<Critter> dieCritters = new HashSet<Critter>();
	protected HashSet<HexCoord> changedHex = new HashSet<HexCoord>();
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

	
	/**
	 * Construct a random world with some rocks in world
	 */
	public World() throws IOException{
		// load the constant variable in the world from file
		//    w.lock();
		loadConstant(constantFileName);
		loadRandomWorld();
		//    w.unlock();
	}
	
	/**
	 * Get name of the world
	 * @return
	 */
	public String getName(){
		r.lock();
		try{
			return this.name;	
		}
		finally{
			r.unlock();
		}

	}
	
	/** 
	 * Construct the world from the world file
	 * @param filename the filename of the world file,
	 * 		  which contains the world information
	 */ 
	public World(String filename) throws IOException{
		//    w.lock();
		loadConstant(constantFileName);
		loadWorldFromFile(filename);
		System.out.println("load world successfully");
		//    w.unlock();
	}
	
	/**
	 * Load a random word, with some rocks in the world
	 */
	private void loadRandomWorld() {
		// TODO Auto-generated method stub
		// new world with some random rocks
		//    w.lock();
		this.map = new Hashtable<HexCoord,Placeable>();
		this.Row = World.ROWS;
		this.Col = World.COLUMNS;
		this.name = World.RANDOM_WORLD_NAME;
		steps = 0;
		this.maxSize = this.Row * this.Col / 2;
		System.out.println(this.maxSize);
		this.size = 0;
		int rockNumber =  (int) (this.Row * this.Col * World.RANDOM_ROCK_FACTOR);
		System.out.println(this.Row+"  " + this.Col + "    " + World.RANDOM_ROCK_FACTOR);
		System.out.print("put " + rockNumber +" rocks");
		// pub the rock in the world
		for(int i = 0; i < rockNumber; i++){
			int r = World.RAND.nextInt(this.Row);
			int c = World.RAND.nextInt(this.Col);
//			System.out.println("r:" + r + "  c:" + c);
			this.addObj(new Rock(), new HexCoord(c,r));
		}
		//    w.unlock();
	}

	/**
	 * Load the world from the world file
	 * 
	 * @param filename the name of the file that contains the world information
	 */
	private void loadWorldFromFile(String filename) throws IOException {
		// TODO Auto-generated method stub
		//    w.lock();
		this.map = new Hashtable<HexCoord,Placeable>();
		steps = 0;
		// read world file and many object by world file
		File worldFile = new File(filename);
		String absolutePath = worldFile.getAbsolutePath();
		String path = worldFile.getParent();
		System.out.println(path);
		BufferedReader br = new BufferedReader(new FileReader(worldFile));
		String line = br.readLine();
//		World world = new World();
		while(line!=null){
			if(line.contains("//")){
				line = br.readLine();
				continue;
			}
			line = line.toLowerCase();
			// set name of the world
			if(line.startsWith("name")){
				String name = line.substring(5, line.length());
				this.setName(name.trim());
			}
			// set size of the world
			if(line.startsWith("size")){
				String[] sizeAtr = line.split(" ");
				int col = Integer.parseInt(sizeAtr[1]);
				int row = Integer.parseInt(sizeAtr[2]);
				this.setSize(col, row);
			}
			// add rock to the world
			if(line.startsWith("rock")){
				String[] posiAtr = line.split(" ");
				int col = Integer.parseInt(posiAtr[1]);
				int row = Integer.parseInt(posiAtr[2]);
				HexCoord posi = new HexCoord(col, row);
				this.addObj((new Rock()), posi);
			}
			// add food to the world
			if(line.startsWith("food")){
				String[] posiAtr = line.split(" ");
				int col = Integer.parseInt(posiAtr[1]);
				int row = Integer.parseInt(posiAtr[2]);
				int foodValue = Integer.parseInt(posiAtr[3]);
				HexCoord posi = new HexCoord(col, row);
				this.addObj((new Food(foodValue)), posi);
			}
			// add critter to the world
			if(line.startsWith("critter")){
				String[] critterAtr = line.split(" ");
				String critterFilename = critterAtr[1];
				critterFilename = path + "/" + critterFilename;
				int col = Integer.parseInt(critterAtr[2]);
				int row = Integer.parseInt(critterAtr[3]);
				int dir = Integer.parseInt(critterAtr[4]);
				HexCoord posi = new HexCoord(col, row);
				Critter cri1 = new Critter(critterFilename);
				cri1.setPosition(posi);
				cri1.setDirection(dir % 6);
				cri1.setWorld(this);
				this.addObj(cri1, posi);
			}
			line = br.readLine();
		}
		br.close();
//		this.maxSize = this.Row * this.Col / 2;
		this.size = 0;
		//    w.unlock();
	}

	/**
	 * Load world constant variable from file
	 * 
	 * @param filename the name of the file that contains the 
	 * 				   constant variable of the world
	 */
	private void loadConstant(String filename) throws IOException {
		// TODO Auto-generated method stub
		//    w.lock();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		while(line!=null){
			String[] info = line.split(" ");
			switch(info[0]){
			case "BASE_DAMAGE": World.BASE_DAMAGE = Integer.parseInt(info[1]);
								break;
			case "DAMAGE_INC": World.DAMAGE_INC = Double.parseDouble(info[1]);
								break;
			case "ENERGY_PER_SIZE": World.ENERGY_PER_SIZE = Integer.parseInt(info[1]);
									break;
			case "FOOD_PER_SIZE": World.FOOD_PER_SIZE = Integer.parseInt(info[1]);
									break;
			case "MAX_SMELL_DISTANCE": World.MAX_SMELL_DISTANCE = Integer.parseInt(info[1]);
									break;
			case "ROCK_VALUE": World.ROCK_VALUE = Integer.parseInt(info[1]);
								break;
			case "COLUMNS": World.COLUMNS = Integer.parseInt(info[1]);
								break;
			case "ROWS": World.ROWS = Integer.parseInt(info[1]);
									break;
			case "MAX_RULES_PER_TURN": World.MAX_RULES_PER_TURN = Integer.parseInt(info[1]);
									break;
			case "SOLAR_FLUX": World.SOLAR_FLUX = Integer.parseInt(info[1]);
									break;
			case "MOVE_COST": World.MOVE_COST = Integer.parseInt(info[1]);
									break;
			case "ATTACK_COST": World.ATTACK_COST = Integer.parseInt(info[1]);
									break;
			case "GROW_COST": World.GROW_COST = Integer.parseInt(info[1]);
									break;
			case "BUD_COST": World.BUD_COST = Integer.parseInt(info[1]);
									break;
			case "MATE_COST": World.MATE_COST = Integer.parseInt(info[1]);
									break;
			case "RULE_COST": World.RULE_COST = Integer.parseInt(info[1]);
									break;
			case "ABILITY_COST": World.ABILITY_COST = Integer.parseInt(info[1]);
									break;
			case "INITIAL_ENERGY": World.INITIAL_ENERGY = Integer.parseInt(info[1]);
									break;
			case "MIN_MEMORY": World.MIN_MEMORY = Integer.parseInt(info[1]);
									break;
			default: System.out.println("Unknown constant" + info[0]);
			
			}
			
			line = br.readLine();
		}
		//    w.unlock();
		
	}
	
	/**
	 * get the row number of the world
	 * 
	 * @return the row number of the world
	 * 
	 */
	public int getRow(){
		r.lock();
		try{
			return this.Row;
		}finally{
			r.unlock();
		}

	}
	
	/**
	 * get the column number of the world
	 * 
	 * @return
	 */
	public int getCol(){
		r.lock();
		try{
			return this.Col;
		}finally{
			r.unlock();
		}
		
	}
	
	/**
	 * set the world's name
	 * 
	 * @param name the name of the world
	 */
	public void setName(String name){
		//    w.lock();
		this.name = name;
		//    w.unlock();
	}
	
	
	/**
	 * Set the size of the world
	 * 
	 * @param col the column number of the world
	 * @param row the row number of the world
	 */
	public void setSize(int col, int row){
		//    w.lock();
		this.Col = col;
		this.Row = row;
		this.maxSize = this.Col * this.Row / 2;
		//    w.unlock();
	}
	
	/**
	 * Add critter, food or rock in the world
	 * if the placeObj is a critter, put it in the critter arrayList
	 * 
	 * @param placeObj the object that to be placed in the world
	 * @param posi the position that the object to be placed
	 * @return true if the object is placed successfully, otherwise return false
	 */
	public boolean addObj(Placeable placeObj, HexCoord posi){
		//    w.lock();
		try{
			if(!this.validPosi(posi)) return false; // try to place on invalid place
			if(map.get(posi)!=null) return false; // there is something in this position posi
			if(this.size >= this.maxSize){
				System.out.println("The world is too crowded");
				return false;
			}
			System.out.println(placeObj.getClass());
			if(placeObj instanceof Critter){
				critterId++;
				Critter cri = (Critter)placeObj;
				cri.setId(critterId);
				System.out.println("critter id:  " + critterId);
				critters.add(cri);
				((Critter)placeObj).position = posi;
				((Critter)placeObj).setWorld(this);
				System.out.println("add one critter");
			}
			map.put(posi, placeObj);
			changedHex.add(posi);
			System.out.println(posi);
			size ++;
			return true;
		}finally{
			//    w.unlock();
		}
	}
	
	public boolean removeCritter(int id){
		//    w.lock();
		try{
			Critter critter = null;
			for(Critter cri : this.critters){
				if(cri.getId() == id){
					critter = cri;
					break;
				}
			}
			if(critter == null) return false;
			HexCoord posi = critter.position;
			return removeObj(critter, posi);	
		}finally{
			//    w.unlock();
		}
		
	}
	
	public boolean removeObj(Placeable placeObj, HexCoord posi){
		//    w.lock();
		try{
			if(placeObj == null || posi == null) return false;
			if(placeObj instanceof Food
					|| placeObj instanceof Rock){
				this.map.remove(posi);
				return true;
			}else if(placeObj instanceof Critter){
				Critter cri = (Critter)placeObj;
				this.map.remove(posi);
				this.critters.remove(cri);
				return true;
			}
			return false;
		}finally{
			//    w.unlock();
		}
	}
	
	
	/**
	 * Get the object at the specific location
	 * 
	 * @param posi the position of the object that need to be gotten
	 * @return the object at the given location, null if there is noting
	 */
	public Placeable getObj(HexCoord posi){
		r.lock();
		try{
			return this.map.get(posi);	
		}finally{
			r.unlock();
		}

	}
	
	/**
	 * Get the number of object in the world
	 * 
	 * @return the number of the object in the world
	 */
	public int getObjNumber(){
		r.lock();
		try{
			return this.map.size();
		}finally{
			r.unlock();
		}

	}
	
	/**
	 * Judge whether a position is valid in the world
	 * @param posi the position to be judged
	 * @return the validness of the position, true if the position is valid
	 */
	public boolean validPosi(HexCoord posi){
		r.lock();
		try{
			int c = posi.col;
			int r = posi.row;
			if( c < 0 || r < 0) return false;
			if(c >= this.Col || r >= this.Row) return false;
			if(2 * r - c < 0 || 2 * r - c >= 2 * this.Row - this.Col) return false;
			return true;
		}finally{
			r.unlock();
		}
		
	}
	
	/**
	 * Execute the world for certain steps
	 * 
	 * @param times the step to be executed
	 */
	public void execute(int times){
//		//    w.lock();
		int i = times;
//		changedHex.clear();
		while(i>0){
			this.dieCritters.clear();
			i--;
			InterpreterImpl interpreter = null;
			for(int j = 0; j < critters.size(); j++){
				Critter cri = critters.get(j);
				interpreter = new InterpreterImpl(cri,cri.rules);
				Outcome outcome = interpreter.interpret();
				if(outcome == null) continue;
//				System.out.println(cri.name + "     "+ outcome.getAction());
				executeOutcome(cri, outcome);
			}
			for(Critter cri:this.dieCritters){
				this.critters.remove(cri);
			}

		}
		steps += times;
//		//    w.unlock();
	}
	
	/**
	 * Get the number of steps that world has been executed
	 * 
	 * @return the number of the steps that the world has been executed
	 */
	public int getSteps(){
		r.lock();
		try{
			return this.steps;
		}finally{
			r.unlock();
		}

	}

	/**
	 * Get the number of the critters in the world
	 * 
	 * @return the critter number
	 */
	public int getCritterNumber(){
		r.lock();
		try{
			return this.critters.size();
		}finally{
			r.unlock();
		}
		
	}
	
	/**
	 * Get all the critters in the world
	 * @return
	 */
	public ArrayList<Critter> getCritters(){
		r.lock();
		try{
			return this.critters;
		}finally{
			r.unlock();
		}
		
	}

	/**
	 * Execute the outcome for a critter
	 * 
	 * @param cri the critter that executes the outcome
	 * @param outcome the outcome action to be executed
	 */
	private void executeOutcome(Critter cri, Outcome outcome) {
		// TODO Auto-generated method stub
		//    //    w.lock();
		String actionName = outcome.getAction().toLowerCase();
		int value = outcome.getValue();
		switch(actionName){
		case "wait":{
			cri.setMem(4, cri.getMem(4) + cri.getMem(3) * World.SOLAR_FLUX);
			break;
		}
		case "forward":{
			moveForward(cri);
			break;
		}
		case "backward":{
			moveBackward(cri);
			break;
		}
		case "left":{
			turnLeft(cri);
			break;
		}
		case "right":{
			turnRight(cri);
			break;
		}
		case "eat":{
			eat(cri);
			break;
		}
		case "serve":{
			serve(cri,value);
			break;
		}
		case "attack":{
			attack(cri);
			break;
		}
		case "tag":{
			tag(cri, value);
			break;
		}
		case "grow":{
			grow(cri);
			break;
		}
		case "bud":{
			bud(cri);
		}
		case "mate":{
			mate(cri);
		}
		
		}
		//    w.unlock();
	}

	/**
	 * Make a critter die in the world
	 * 
	 * @param cri the critter that need to die
	 */
	private void die(Critter cri) {
		//    w.lock();
		// TODO Auto-generated method stub
		// remove from arraylist
//		this.critters.remove(cri);
		this.dieCritters.add(cri);
		//become a food value = size * food_per_size
		Food food = new Food(cri.getMem(3) * World.FOOD_PER_SIZE);
		// update the map info
		this.map.put(cri.position, food);
		changedHex.add(cri.position);
		//    w.unlock();
	}

	/**
	 * Move forward a critter
	 * 
	 * @param cri the critter that moves forward
	 */
	private void moveForward(Critter cri) {
		// TODO Auto-generated method stub
		//    w.lock();
		int cost = World.MOVE_COST * cri.getMem(3);// cost is movecost * size
		cri.setMem(4, cri.getMem(4) - cost);
		// test whether critter dies
		if(cri.getMem(4) <=0){
			// critter dies
			die(cri);			
			return;
		}
		
		HexCoord forwardPosi = getFrontPosi(cri);
		if(!validPosi(forwardPosi)) return;
		moveTo(cri, forwardPosi);
		//    w.unlock();
	} 


	/**
	 * Move back a critter
	 * 
	 * @param cri the critter that moves back
	 */
	private void moveBackward(Critter cri) {
		// TODO Auto-generated method stub
		//    w.lock();
		int cost = World.MOVE_COST * cri.getMem(3);// cost is movecost * size
		cri.setMem(4, cri.getMem(4) - cost);
		//test whether dies
		if(cri.getMem(4) <=0){
			// cri die
			// delete it from arraylist and map
			die(cri);
			return;
		}
		
		HexCoord backwardPosi = getBackPosi(cri);
		if(!validPosi(backwardPosi)) return;
		moveTo(cri, backwardPosi);
		//    w.unlock();
	}

	/**
	 * Move a critter to a certain location
	 * 
	 * @param cri the critter that moves 
	 * @param destiny the location that the critter should be moved to
	 * @return true if the critter moves successfully
	 */
	private boolean moveTo(Critter cri, HexCoord destiny) {
		// TODO Auto-generated method stub
		//    w.lock();
		try{
			if(!this.validPosi(destiny)) return false; // if the forward position is invalid, move unsuccessfully
			// update energy
			if(this.map.get(destiny) == null){
				// if the there is nothing in the forwardposition
				// then move forward
				// set the original position null
				System.out.println("move: " + destiny);
				this.map.remove(cri.position);
				changedHex.add(cri.position);
				// update the position of critter
				cri.setPosition(destiny);
				// update the world
				this.map.put(destiny, cri);
				cri.setPosition(destiny);
				changedHex.add(destiny);
				return true;
			}
			return false;
		}finally{
			//    w.unlock();
		}
		
	}

	/**
	 * Make a critter turn left
	 * 
	 * @param cri the critter that make the turn
	 */
	private void turnLeft(Critter cri) {
		// TODO Auto-generated method stub
		//    w.lock();
		int cost = cri.getMem(3);// cost is size
		cri.setMem(4, cri.getMem(4) - cost);
		if(cri.getMem(4) <= 0){
			die(cri);
			return;
		}
		cri.Direction = (cri.Direction - 1) % World.MAX_DIRECTION;
		if(cri.Direction < 0) cri.Direction +=6;
		changedHex.add(cri.position);
		//    w.unlock();
	}

	/**
	 * Make the critter turn right
	 * 
	 * @param cri the critter that turns right
	 */
	private void turnRight(Critter cri) {
		// TODO Auto-generated method stub
		//    w.lock();
		int cost = cri.getMem(3);// cost is size
		cri.setMem(4, cri.getMem(4) - cost);
		if(cri.getMem(4) <= 0){
			die(cri);
			return;
		}
		cri.Direction = (cri.Direction + 1) % World.MAX_DIRECTION;
		if(cri.Direction < 0) cri.Direction +=6;
		changedHex.add(cri.position);
		//    w.unlock();
	}

	/**
	 * Execute the eat action for a critter
	 * 
	 * @param cri the critter that executes the eat action
	 */
	private void eat(Critter cri) {
		// TODO Auto-generated method stub
		// find the ahead info, whether there is some food
		// if there is some food then eat, if not, return;
		//    w.lock();
		int cost = cri.getMem(3);// cost is size
		cri.setMem(4, cri.getMem(4) - cost);
		if(cri.getMem(4) <= 0){
			die(cri);
			return;
		}
		int aheadInfo = cri.ahead(1); // 
		if(aheadInfo >= -1) return;

		HexCoord forwardPosi = getFrontPosi(cri);
		if(!validPosi(forwardPosi)) return;
		Food food = (Food)this.map.get(forwardPosi); // get the food in front of the critter
		int foodValue = food.value;
		int energy = cri.getMem(4);
		int maxEnergy = cri.getMem(3) * World.ENERGY_PER_SIZE;
		if(foodValue + energy < maxEnergy){
			// eat all the food and so remove food from the world
			cri.setMem(4, foodValue + energy);
			this.map.remove(forwardPosi);
		}else{
			foodValue = energy + foodValue - maxEnergy;
			cri.setMem(4, maxEnergy);
			food.setFoodValue(foodValue);
		}
		changedHex.add(cri.position);
		changedHex.add(forwardPosi);
		//    w.unlock();
	}


	/**
	 * A critter may convert some of its own energy into food added to 
	 * the hex in front of it, if that hex is either empty or already 
	 * contains some food.
	 * 
	 * @param cri the critter that execute the action
	 * @param value the value need to be served
	 */
	private void serve(Critter cri, int value) {
		// TODO Auto-generated method stub
		// 1 front must be food or empty to serve successfully
		// 2 energy cost is value + cri.size
		// 3 critter may be killed by serve, then the served value should
		//   critter.energy - cost, and set current hex to food
		//    w.lock();
		int cost = cri.getMem(3);// cost is size
		cri.setMem(4, cri.getMem(4) - cost);
		if(cri.getMem(4) <= 0){
			die(cri);
			return;
		}
		
		HexCoord frontPosi = getFrontPosi(cri);
		if(!validPosi(frontPosi)) return;
		if(this.map.get(frontPosi) == null 
				|| this.map.get(frontPosi) instanceof Food){
			// front is food or empty, so can serve
			Food food = null;
			if(this.map.get(frontPosi) == null){
				// front is empty
				food = new Food();
			}else{
				food = (Food)this.map.get(frontPosi);
			}
			
			if(value >= cri.getMem(4)){
				// served value is larger than cri.energy
				food.setFoodValue(food.getFoodValue() + cri.getMem(4));
				cri.setMem(4, 0);
			}else{
				food.setFoodValue(food.getFoodValue() + value);
				cri.setMem(4, cri.getMem(4) - value);
			}
			this.map.put(frontPosi, food); // put food in the world
			changedHex.add(frontPosi);
			if(cri.getMem(4) <= 0){
				die(cri);
			}
		}
		//    w.unlock();
		
	}

	/**
	 * Make a critter execute attack action
	 * 
	 * @param cri the critter that execute the attack action
	 */
	private void attack(Critter cri) {
		// TODO Auto-generated method stub
		//    w.lock();
		int cost = World.ATTACK_COST * cri.getMem(3);// cost is attackcost * size
		cri.setMem(4, cri.getMem(4) - cost);
		// test whether critter dies
		if(cri.getMem(4) <=0){
			// critter dies
			die(cri);			
			return;
		}
		
		HexCoord frontPosi = getFrontPosi(cri);
		if(!validPosi(frontPosi)) return;
		if(!(this.map.get(frontPosi) instanceof Critter)) return;
		Critter victim = (Critter)this.map.get(frontPosi);
		int s1 = cri.getMem(3);
		int s2 = victim.getMem(3);
		int o1 = cri.getMem(2);
		int d2 = victim.getMem(1);
		double x = World.DAMAGE_INC * (s1 * o1 - s2 * d2);
		double p = 1.0/(1 + Math.exp(-x));
		int hurt = (int) (World.BASE_DAMAGE * s1 * p);
		victim.setMem(4, victim.getMem(4) - hurt);
		changedHex.add(frontPosi);
		changedHex.add(victim.position);
		if(victim.getMem(4) <= 0){
			die(victim);
		}
		//    w.unlock();
		
	}
	
	/**
	 * Calculate and get the front position of a critter
	 * 
	 * @param cri the critter we calculate for
	 * @return the front position of the critter
	 */
	public static HexCoord getFrontPosi(Critter cri){
		int c = cri.position.col;
		int r = cri.position.row;
		int[][] possiblePosi = {{0,1},
								{1,1},
								{1,0},
								{0,-1},
								{-1,-1},
								{-1,0}};
		c += possiblePosi[cri.Direction][0];
		r += possiblePosi[cri.Direction][1];
		HexCoord front = new HexCoord(c,r);
		return new HexCoord(c,r);
	}

	/**
	 * Calculate and get the back position of a critter
	 * 
	 * @param cri the critter we calculate for
	 * @return the back position of the critter
	 */
	public static HexCoord getBackPosi(Critter cri){
		int c = cri.position.col;
		int r = cri.position.row;
		int[][] possiblePosi = {{0,1},
								{1,1},
								{1,0},
								{0,-1},
								{-1,-1},
								{-1,0}};
		int length = possiblePosi.length;
		c += possiblePosi[(cri.Direction + length/2) % length][0];
		r += possiblePosi[(cri.Direction + length/2) % length][1];
		HexCoord backwardPosi = new HexCoord(c,r);
		return backwardPosi;
	}
	
	/**
	 * Execute tag action for a critter
	 * 
	 * @param cri the critter that executes the tag action
	 * @param value the value that the critter tags
	 */
	private void tag(Critter cri, int value) {
		// TODO Auto-generated method stub
		//    w.lock();
		int cost = cri.getMem(3);// cost is size
		cri.setMem(4, cri.getMem(4) - cost);
		// test whether critter dies
		if(cri.getMem(4) <=0){
			// critter dies
			die(cri);			
			return;
		}
		if(value > Critter.MAX_TAG_VALUE || value < 0) return; // tag value should be valid
		
		HexCoord frontPosi = getFrontPosi(cri);
		if(!validPosi(frontPosi)) return;
		if(!(this.map.get(frontPosi) instanceof Critter)) return;
		Critter victim = (Critter)this.map.get(frontPosi);
		victim.setMem(6, value); // set victim tag
		changedHex.add(cri.position);
		changedHex.add(victim.position);
		//    w.unlock();
	}


	/**
	 * Execute the grow action for a critter
	 * 
	 * @param cri the critter that execute the grow action
	 */
	private void grow(Critter cri) {
		// TODO Auto-generated method stub
		//    w.lock();
		int cost = cri.getMem(3) * cri.getComplexity() * World.GROW_COST;
		cri.setMem(4, cri.getMem(4) - cost);
		if(cri.getMem(4) <= 0){
			die(cri);
			return;
		}
		cri.setMem(3, cri.getMem(3) + 1);
		changedHex.add(cri.position);
		//    w.unlock();
	}

	/**
	 * Execute bud action for one critter
	 * 
	 * @param cri the critter that execute bud action
	 */
	private void bud(Critter cri) {
		// TODO Auto-generated method stub
		
		//    w.lock();
		int cost = World.BUD_COST * cri.getComplexity();
		cri.setMem(4, cri.getMem(4) - cost);
		if(cri.getMem(4) <= 0){
			die(cri);
			return;
		}
		
		HexCoord backPosi = getBackPosi(cri);
		// invalid position, so child cannot be created 
		if(!this.validPosi(backPosi)) return; 
		//there is something on that position, so child cannont be created
		if(this.map.get(backPosi)!=null) return; 
		 // get the child
		Critter child = cri.bud(); 
		child.setPosition(backPosi);
		// update the map and critters arraylist
		this.map.put(backPosi, child);
		this.critters.add(child);
		changedHex.add(cri.position);
		changedHex.add(backPosi);
		//    w.unlock();
	}

	/**
	 * Execute the mate action for a critter
	 * 
	 * @param cri the critter that execute mate action
	 */
	private void mate(Critter cri) {
		// TODO Auto-generated method stub
		// mate is different in consuming energy
		// if unsuccessful, only cost energy of turn
		//    w.lock();
		int cost = 0;
		boolean succ = false;
		HexCoord frontPosi = getFrontPosi(cri);
		if(!(this.map.get(frontPosi) instanceof Critter)){
			succ = false;

		}else{
			// front is a critter
			Critter bride = (Critter)this.map.get(frontPosi);
			// they have to be face to face 
			boolean faceToFace = Math.abs(cri.Direction-bride.Direction) == 3;
			// the last action of bride must also be mate
			boolean wantMate = bride.wantMate();
			// the position to put child
			HexCoord childPosi = getMatePosi(cri,bride);
			// both of them have enough energy
			int brideCost = World.MATE_COST * bride.getComplexity();
			int criCost = World.MATE_COST * cri.getComplexity();
			boolean criEnoughEner = cri.getMem(4) >= criCost;
			boolean briEnoughEner = bride.getMem(4) >= brideCost;
			boolean enoughEnergy = briEnoughEner && criEnoughEner;
			
			if(wantMate && faceToFace && enoughEnergy
					&& childPosi!=null){
				// finally, they can mate
				succ = true;
				Critter child = Critter.mate(cri, bride, childPosi, this);
				child.setPosition(childPosi);
				this.map.put(childPosi, child);
				this.critters.add(child);
				changedHex.add(childPosi);
				changedHex.add(cri.position);
				changedHex.add(bride.position);
			}
			
		}
		
		if(succ){
			cost = World.MATE_COST * cri.getComplexity();
		}else{
			cost = cri.getMem(3); 
		}
		
		cri.setMem(4, cri.getMem(4) - cost);
		if(cri.getMem(4) <= 0){
			die(cri);
			return;
		}
		//    w.unlock();
		
	}

	/**
	 * Get and calculate the mate position for a couple of critters
	 * 
	 * @param cri the one critter that want to mate
	 * @param bride the other critter that want to mate
	 * @return the position where the child locate
	 */
	private HexCoord getMatePosi(Critter cri, Critter bride) {
		// TODO Auto-generated method stub
		r.lock();
		try{
			HexCoord criBack = getBackPosi(cri);
			HexCoord brideBack = getBackPosi(cri);
			boolean criValid = validPosi(criBack);
			boolean briValid = validPosi(brideBack);
			if(!criValid && !briValid) return null; // no space for child
			//valid position and there is nothing on this position
			criValid = criValid && this.map.get(criBack) == null;
			briValid = briValid && this.map.get(briValid) == null;
			// if both valid return random one
			if(criValid && briValid) return World.RAND.nextDouble() > 0.5?
														criBack:brideBack;
			// return the valid one
			if(criValid) return criBack;
			if(briValid) return brideBack;
			return null;
		}finally{
			r.unlock();
		}
		
	}
	
	/**
	 * Print the world info
	 */
	public void printInfo(){
    	System.out.println("The current step is NO: " + this.getSteps());
    	System.out.println("The number of critters is: " + this.getCritterNumber());
		int row = this.getRow() / 2;
		int col = this.getCol() - row;
		System.out.println("row: " + this.Row);
		System.out.println("col: " + this.Col);
		int cnt1 = this.getCol() / 2 + this.getCol() % 2;
		int cnt2 = this.getCol() / 2;
		
		while(row>=0){
			if(!(row==this.getRow()/2 && this.getCol() % 2 ==1)){
				System.out.print("  ");	
				for(int j = row + 1, k = 0; k < cnt2; k++){
					int c = 2 * k + 1;
					int r = j + k;
					HexCoord posi = new HexCoord(c,r);
//					System.out.println(posi);
					Placeable pla = this.getObj(posi);
//					System.out.print(posi+"  ");
					if(pla == null) System.out.print("-   ");
					else if(pla instanceof Rock)
						System.out.print("#   ");
					else if(pla instanceof Food){
						System.out.print("f   ");
					}
					else if(pla instanceof Critter){
						System.out.print(((Critter)pla).getDirection() + "   ");
					}
				}
			}

			System.out.println();
			for(int j = row, k = 0; k < cnt1; k++){
				int c = 2 * k;
				int r = j + k;
				HexCoord posi = new HexCoord(c,r);
//				System.out.println(posi);
				Placeable pla = this.getObj(posi);
//				System.out.print(posi+"  ");
				if(pla == null) System.out.print("-   ");
				else if(pla instanceof Rock)
					System.out.print("#   ");
				else if(pla instanceof Food){
					System.out.print("f   ");
				}
				else if(pla instanceof Critter){
					System.out.print(((Critter)pla).getDirection() + "   ");
				}
			}
			System.out.println();
			row--;
		}
	}
	
	public Hashtable<HexCoord, Placeable> getMap(){
		return this.map;
	}
	
	public HashSet<HexCoord> getChange(){
		r.lock();
		try{
			return this.changedHex;
		}finally{
			r.unlock();
		}

	}
	
	public void clearChange(){
		//    w.lock();
		this.changedHex.clear();
		//    w.unlock();
	}
	
	public HashSet<Critter> getDieCritter(){
		r.lock();
		try{
			return this.dieCritters;
		}finally{
			r.unlock();
		}
		
	}
	
	/**
	 * Print the hex info
	 * 
	 * @param c the column of the hex
	 * @param r the row of the hex
	 */
	public StringBuilder printHex(HexCoord hex){
		int c = hex.col;
		int r = hex.row;
    	Placeable pla = this.getObj(new HexCoord(c,r));
    	if(pla == null){
//    		System.out.println("Nothing is here~~");
    		return null;
    	}
    	
    	if(pla instanceof Rock){
    		System.out.println("Here is a rock");
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	if(pla instanceof Food){
    		System.out.println("Here is some food: " + ((Food)pla).getFoodValue());
    		return sb.append("food: " + ((Food)pla).getFoodValue());
    	}
    	
    	if(pla instanceof Critter){
//    		System.out.println("Here is a critter");
    		System.out.println("The species is: " + ((Critter)pla).getName());
    		((Critter)pla).printMem();
    		ProgramImpl rules = (ProgramImpl)((Critter)pla).getRules();
    		sb = new StringBuilder();
    		rules.prettyPrint(sb);
//    		System.out.println("The rule set is: ");
//    		System.out.println(sb.toString());
    		int lastIndex = ((Critter)pla).getLastRuleIndex();
    		Rule lastRule = rules.getRule(lastIndex);
    		sb.append("*#*");
    		lastRule.prettyPrint(sb);
//    		System.out.println("The last excuted rule is: ");
//    		System.out.println(sb.toString());
    		return sb;	
    	}
    	return null;
	}
	
	/**
	 * Load critter given the filename
	 * 
	 * @param filename the name of file that contains the critter info
	 * @param n the number of critters need to be loaded
	 */
	public HashSet loadCritter(String filename, int n){
		//    w.lock();
		try{
			HashSet<HexCoord> newCritterPosi = new HashSet<HexCoord>();
	    	int i = n * 15;
	    	while(n > 0 && i > 0) {
	    		try {
					Critter cri = new Critter(filename);
					int r = this.RAND.nextInt(this.getRow());
					int c = this.RAND.nextInt(this.getCol());
					cri.setPosition(new HexCoord(c,r));
					boolean succ = this.addObj(cri, cri.getPosition());
					if(succ){
						newCritterPosi.add(cri.position);
						n--;
					} 
					i--;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}
	    	return newCritterPosi;
		}finally{
			//    w.unlock();
		}
//    	System.out.println("load over~: ");
	}

	
	
	
}
