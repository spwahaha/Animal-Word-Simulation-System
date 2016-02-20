package simulation;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import ast.Program;
import parse.Parser;
import parse.ParserImpl;
import parse.Token;
import parse.Tokenizer;

public class SimulationTester {

	
	
	
	public void Worldtest() throws IOException {
		String fileName = "examples/world.txt";
		World world = new World(fileName);
		world.execute(2);
		System.out.println("finish excute");
	}
	
	
	public void Waittest() throws IOException {
		String fileName = "examples/waitworld.txt";
		World world = new World(fileName);
		int energy1 = world.critters.get(0).getMem(4);
		world.execute(2);
		int energy2 = world.critters.get(0).getMem(4);
		assertEquals(energy1 + 2,energy2);
		System.out.println("finish excute");
	}	
	
	
	
	public void turntest() throws IOException {
		String fileName = "examples/turnworld.txt";
		System.out.println(-1 % 6);
		World world = new World(fileName);
		int dir11 = world.critters.get(0).getDirection(); //3  turn left
		int dir21 = world.critters.get(1).getDirection(); //3  turn right
		world.execute(4);
		int energy2 = world.critters.get(0).getMem(4);
		assertEquals(5,world.critters.get(0).getDirection());
		assertEquals(1,world.critters.get(1).getDirection());		
		System.out.println("finish excute");
	}		
	
	
	public void eattest() throws IOException {
		String fileName = "examples/eatworld.txt";
		World world = new World(fileName);
		int dir11 = world.critters.get(0).getDirection(); //3  turn left
		int dir21 = world.critters.get(1).getDirection(); //3  turn right
		int energy11 = world.critters.get(0).getMem(4);
		int energy21 = world.critters.get(1).getMem(4); 
		world.execute(4);
		int energy12 = world.critters.get(0).getMem(4);
		int energy22 = world.critters.get(1).getMem(4); 

		System.out.println("finish excute");
	}	
	
	
	public void movetest() throws IOException {
		String fileName = "examples/moveworld.txt";
		World world = new World(fileName);
		int dir11 = world.critters.get(0).getDirection(); //3  turn left
		int dir21 = world.critters.get(1).getDirection(); //3  turn right
		int energy11 = world.critters.get(0).getMem(4);
		int energy21 = world.critters.get(1).getMem(4); 
		world.execute(4);
		int energy12 = world.critters.get(0).getMem(4);
		int energy22 = world.critters.get(1).getMem(4); 
		assertEquals(new HexCoord(4,3), world.critters.get(0).position);
		assertEquals(new HexCoord(4,9), world.critters.get(1).position);
	}		
	
	

	
	
	public void servetest() throws IOException {
		String fileName = "examples/serveworld.txt";
		World world = new World(fileName);
		int dir11 = world.critters.get(0).getDirection(); //3  turn left
		int dir21 = world.critters.get(1).getDirection(); //3  turn right
		int energy11 = world.critters.get(0).getMem(4);
		int energy21 = world.critters.get(1).getMem(4); 
		world.execute(4);
		int energy12 = world.critters.get(0).getMem(4);
		int energy22 = world.critters.get(1).getMem(4); 
	}		
	
	
	
	
	
	
	public void attacktest() throws IOException {
		String fileName = "examples/attackworld.txt";
		World world = new World(fileName);
//		int dir11 = world.critters.get(0).getDirection(); //3  turn left
//		int dir21 = world.critters.get(1).getDirection(); //3  turn right
//		int energy11 = world.critters.get(0).getMem(4);
//		int energy21 = world.critters.get(1).getMem(4); 
		world.execute(4);
//		int energy12 = world.critters.get(0).getMem(4);
		int energy22 = world.critters.get(1).getMem(4); 
	}		
	
	
	public void tagtest() throws IOException {
		String fileName = "examples/tagworld.txt";
		World world = new World(fileName);
//		int dir11 = world.critters.get(0).getDirection(); //3  turn left
//		int dir21 = world.critters.get(1).getDirection(); //3  turn right
//		int energy11 = world.critters.get(0).getMem(4);
//		int energy21 = world.critters.get(1).getMem(4); 
		world.execute(4);
//		int energy12 = world.critters.get(0).getMem(4);
		int energy22 = world.critters.get(1).getMem(4); 
	}	
	
	@Test
	public void growtest() throws IOException {
		String fileName = "examples/tagworld.txt";
		World world = new World(fileName);
//		int dir11 = world.critters.get(0).getDirection(); //3  turn left
//		int dir21 = world.critters.get(1).getDirection(); //3  turn right
//		int energy11 = world.critters.get(0).getMem(4);
//		int energy21 = world.critters.get(1).getMem(4); 
		world.execute(4);
//		int energy12 = world.critters.get(0).getMem(4);
		int energy22 = world.critters.get(1).getMem(4); 
	}	

	
	public void budtest() throws IOException {
		String fileName = "examples/tagworld.txt";
		World world = new World(fileName);
//		int dir11 = world.critters.get(0).getDirection(); //3  turn left
//		int dir21 = world.critters.get(1).getDirection(); //3  turn right
//		int energy11 = world.critters.get(0).getMem(4);
//		int energy21 = world.critters.get(1).getMem(4); 
		world.execute(4);
//		int energy12 = world.critters.get(0).getMem(4);
		int energy22 = world.critters.get(1).getMem(4); 
	}	
	
	@Test
	public void matetest() throws IOException {
		String fileName = "examples/tagworld.txt";
		World world = new World(fileName);
//		int dir11 = world.critters.get(0).getDirection(); //3  turn left
//		int dir21 = world.critters.get(1).getDirection(); //3  turn right
//		int energy11 = world.critters.get(0).getMem(4);
//		int energy21 = world.critters.get(1).getMem(4); 
		world.execute(4);
//		int energy12 = world.critters.get(0).getMem(4);
		int energy22 = world.critters.get(1).getMem(4); 
	}	
		
	
	public void Crittertest() throws IOException {
		String fileName = "examples/example-critter.txt";
		Critter cri = new Critter(fileName);
		ArrayList<Critter> cris = new ArrayList<Critter>();
		cris.add(cri);
		System.out.println(cris.get(0).getMem(3));
		Critter cri2 = cris.get(0);
		cri2.setMem(3, cri2.getMem(3) + 1);
		System.out.println(cris.get(0).getMem(3));		
		HashMap<Integer, Food> hm = new HashMap<Integer, Food>();
		hm.put(1, new Food(300));
		Food fod1 = hm.get(1);
		fod1.setFoodValue(100);
		System.out.println(hm.get(1).value);
	}

}
