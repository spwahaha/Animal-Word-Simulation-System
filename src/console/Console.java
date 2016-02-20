package console;

import java.io.IOException;
import java.util.Scanner;

import ast.ProgramImpl;
import ast.Rule;
import simulation.Critter;
import simulation.Food;
import simulation.HexCoord;
import simulation.Placeable;
import simulation.Rock;
import simulation.World;

/** The console user interface for Assignment 5. */
public class Console {
    private Scanner scan;
    public boolean done;
	World world = null;

    //TODO world representation...

    public static void main(String[] args) {
        Console console = new Console();

        while (!console.done) {
            System.out.print("Enter a command or \"help\" for a list of commands.\n> ");
            console.handleCommand();
        }
    }

    /**
     * Processes a single console command provided by the user.
     */
    void handleCommand() {
        String command = scan.next().toLowerCase();
        switch (command) {
        case "new": {
            newWorld();
            break;
        }
        case "load": {
            String filename = scan.next();
            try {
				loadWorld(filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            break;
        }
        case "critters": {
            String filename = scan.next();
            int n = scan.nextInt();
            loadCritters(filename, n);
            break;
        }
        case "step": {
            int n = scan.nextInt();
            advanceTime(n);
            break;
        }
        case "info": {
            worldInfo();
            break;
        }
        case "hex": {
            int c = scan.nextInt();
            int r = scan.nextInt();
            hexInfo(c, r);
            break;
        }
        case "help": {
            printHelp();
            break;
        }
        case "exit": {
            done = true;
            break;
        }
        default:
            System.out.println(command + " is not a valid command.");
        }
    }

    /**
     * Constructs a new Console capable of reading the standard input.
     */
    public Console() {
        scan = new Scanner(System.in);
        done = false;
    }

    /**
     * Starts new random world simulation.
     */
    private void newWorld() {
        //TODO implement
    	try {
			world = new World();
	    	System.out.println("new world");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    /**
     * Starts new simulation with world specified in filename.
     * @param filename
     * @throws IOException 
     */
    private void loadWorld(String filename) throws IOException {
        //TODO implement
    	world = new World(filename);
    }

    /**
     * Loads critter definition from filename and randomly places 
     * n critters with that definition into the world.
     * @param filename
     * @param n
     */
    private void loadCritters(String filename, int n) {
        //TODO implement
    	int number = n;
    	if(this.world==null){
    		System.out.println("NO WORLD NOW!!!");
    		return;
    	}
    	this.world.loadCritter(filename, n);
    }

    /**
     * Advances the world by n time steps.
     * @param n
     */
    private void advanceTime(int n) {
        //TODO implement
    	if(this.world==null){
    		System.out.println("NO WORLD NOW!!!");
    		return;
    	}
    	this.world.execute(n);
    }

    /**
     * Prints current time step, number of critters, and world
     * map of the simulation.
     */
    private void worldInfo() {
        //TODO implement
    	if(this.world==null){
    		System.out.println("NO WORLD NOW!!!");
    		return;
    	}
    	// start to draw the world map
    	this.world.printInfo();
    }


	/**
     * Prints description of the contents of hex (c,r).
     * @param c column of hex
     * @param r row of hex
     */
    private void hexInfo(int c, int r) {
        //TODO implement
    	if(this.world==null){
    		System.out.println("NO WORLD NOW!!!");
    		return;
    	}
    	this.world.printHex(new HexCoord(c,r));
    }

    /**
     * Prints a list of possible commands to the standard output.
     */
    private void printHelp() {
        System.out.println("new: start a new simulation with a random world");
        System.out.println("load <world_file>: start a new simulation with "
                + "the world loaded from world_file");
        System.out.println("critters <critter_file> <n>: add n critters "
                + "defined by critter_file randomly into the world");
        System.out.println("step <n>: advance the world by n timesteps");
        System.out.println("info: print current timestep, number of critters "
                + "living, and map of world");
        System.out.println("hex <c> <r>: print contents of hex "
                + "at column c, row r");
        System.out.println("exit: exit the program");
    }
}
