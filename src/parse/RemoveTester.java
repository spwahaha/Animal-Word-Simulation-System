package parse;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.junit.Test;

import ast.Mutation;
import ast.MutationFactory;
import ast.Node;
import ast.Program;

public class RemoveTester {

	@Test
	public void Removetest() throws FileNotFoundException {
		Parser parser = new ParserImpl();
		Program prog = parser.parse(new FileReader("examples/test1.txt"));
		Program prog2 = prog.copy();
		assert prog!=prog2;
		StringBuilder sb = new StringBuilder();
		prog.prettyPrint(sb);
		Node node = prog.nodeAt(1);
		Node node1 = node.copy();
		assert node!=node1;
		ArrayList<Program> pros = new ArrayList<Program>();
		for(int i = 0; i < 100; i++){
			pros.add(prog.copy());
		}
//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
		int count = 0;

		for(int i = 0; i < 100; i++){
//			Mutation muta = MutationFactory.getRemove();
			Program pro = pros.get(i);
			StringBuilder sb1 = new StringBuilder();
			pro.prettyPrint(sb1);
//s			System.out.println((pro.prettyPrint(sb1)));	
			pro.mutate();
			StringBuilder sb2 = new StringBuilder();
			pro.prettyPrint(sb2);
//			System.out.println(());	
			if(!sb1.toString().equals(sb2.toString()))
				count++;
//			if(succ) count++;
		}
		System.out.println("removed");
		System.out.println(count);

//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
	}
	
	@Test
	public void swapTest() throws FileNotFoundException{
		Parser parser = new ParserImpl();
		Program prog = parser.parse(new FileReader("examples/test1.txt"));
		Program prog2 = prog.copy();
		assert prog!=prog2;
//		System.out.println((prog2.prettyPrint(new StringBuilder()).toString()));

//		System.out.println("success");
		StringBuilder sb = new StringBuilder();
		prog.prettyPrint(sb);
//		System.out.println(sb.toString());
//		System.out.println(prog.size());
		Node node = prog.nodeAt(1);
//		System.out.println("asd");
		Node node1 = node.copy();
		assert node!=node1;
		ArrayList<Program> pros = new ArrayList<Program>();
		for(int i = 0; i < 100; i++){
			pros.add(prog.copy());
		}
//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
		int count = 0;
		for(int i = 0; i < 100; i++){
//			Mutation muta = MutationFactory.getRemove();
			Program pro = pros.get(i);
			StringBuilder sb1 = new StringBuilder();
			pro.prettyPrint(sb1);
//s			System.out.println((pro.prettyPrint(sb1)));	
			pro.mutate();
			StringBuilder sb2 = new StringBuilder();
			pro.prettyPrint(sb2);
//			System.out.println(());	
			if(!sb1.toString().equals(sb2.toString()))
				count++;
//			if(succ) count++;
		}
		System.out.println("swapped");
		System.out.println(count);

//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
	}
	
	
	@Test
	public void replaceTest() throws FileNotFoundException{
		Parser parser = new ParserImpl();
		Program prog = parser.parse(new FileReader("examples/test1.txt"));
		Program prog2 = prog.copy();
		assert prog!=prog2;
//		System.out.println((prog2.prettyPrint(new StringBuilder()).toString()));

//		System.out.println("success");
		StringBuilder sb = new StringBuilder();
		prog.prettyPrint(sb);
//		System.out.println(sb.toString());
//		System.out.println(prog.size());
		Node node = prog.nodeAt(1);
//		System.out.println("asd");
		Node node1 = node.copy();
		assert node!=node1;
		ArrayList<Program> pros = new ArrayList<Program>();
		for(int i = 0; i < 100; i++){
			pros.add(prog.copy());
		}
//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
		int count = 0;
		for(int i = 0; i < 100; i++){
//			Mutation muta = MutationFactory.getRemove();
			Program pro = pros.get(i);
			StringBuilder sb1 = new StringBuilder();
			pro.prettyPrint(sb1);
//s			System.out.println((pro.prettyPrint(sb1)));	
			pro.mutate();
			StringBuilder sb2 = new StringBuilder();
			pro.prettyPrint(sb2);
//			System.out.println(());	
			if(!sb1.toString().equals(sb2.toString()))
				count++;
//			if(succ) count++;
		}
		System.out.println("replaced");
		System.out.println(count);

//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
	}

	@Test
	public void TransformTest() throws FileNotFoundException{
		Parser parser = new ParserImpl();
		Program prog = parser.parse(new FileReader("examples/test1.txt"));
		Program prog2 = prog.copy();
		assert prog!=prog2;
//		System.out.println((prog2.prettyPrint(new StringBuilder()).toString()));

//		System.out.println("success");
		StringBuilder sb = new StringBuilder();
		prog.prettyPrint(sb);
//		System.out.println(sb.toString());
//		System.out.println(prog.size());
		Node node = prog.nodeAt(1);
//		System.out.println("asd");
		Node node1 = node.copy();
		assert node!=node1;
		ArrayList<Program> pros = new ArrayList<Program>();
		for(int i = 0; i < 100; i++){
			pros.add(prog.copy());
		}
//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
		int count = 0;
		for(int i = 0; i < 100; i++){
//			Mutation muta = MutationFactory.getRemove();
			Program pro = pros.get(i);
			StringBuilder sb1 = new StringBuilder();
			pro.prettyPrint(sb1);
//s			System.out.println((pro.prettyPrint(sb1)));	
			pro.mutate();
			StringBuilder sb2 = new StringBuilder();
			pro.prettyPrint(sb2);
//			System.out.println(());	
			if(!sb1.toString().equals(sb2.toString()))
				count++;
//			if(succ) count++;
		}
		System.out.println("transformed");
		System.out.println(count);

//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
	}
	
	@Test
	public void InsertTest() throws FileNotFoundException{
		Parser parser = new ParserImpl();
		Program prog = parser.parse(new FileReader("examples/test1.txt"));
		Program prog2 = prog.copy();
		assert prog!=prog2;
//		System.out.println((prog2.prettyPrint(new StringBuilder()).toString()));

//		System.out.println("success");
		StringBuilder sb = new StringBuilder();
		prog.prettyPrint(sb);
//		System.out.println(sb.toString());
//		System.out.println(prog.size());
		Node node = prog.nodeAt(1);
//		System.out.println("asd");
		Node node1 = node.copy();
		assert node!=node1;
		ArrayList<Program> pros = new ArrayList<Program>();
		for(int i = 0; i < 100; i++){
			pros.add(prog.copy());
		}
//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
		int count = 0;
		for(int i = 0; i < 100; i++){
//			Mutation muta = MutationFactory.getRemove();
			Program pro = pros.get(i);
			StringBuilder sb1 = new StringBuilder();
			pro.prettyPrint(sb1);
//s			System.out.println((pro.prettyPrint(sb1)));	
			pro.mutate();
			StringBuilder sb2 = new StringBuilder();
			pro.prettyPrint(sb2);
//			System.out.println(());	
			if(!sb1.toString().equals(sb2.toString()))
				count++;
//			if(succ) count++;
		}
		System.out.println("inserted");
		System.out.println(count);

//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
	}
	
	
	@Test
	public void DuplicateTest() throws FileNotFoundException{
		Parser parser = new ParserImpl();
		Program prog = parser.parse(new FileReader("examples/test1.txt"));
		Program prog2 = prog.copy();
		assert prog!=prog2;
//		System.out.println((prog2.prettyPrint(new StringBuilder()).toString()));

//		System.out.println("success");
		StringBuilder sb = new StringBuilder();
//		prog.prettyPrint(sb);
//		System.out.println(sb.toString());
//		System.out.println(prog.size());
		Node node = prog.nodeAt(1);
//		System.out.println("asd");
		Node node1 = node.copy();
		assert node!=node1;
		ArrayList<Program> pros = new ArrayList<Program>();
		for(int i = 0; i < 100; i++){
			pros.add(prog.copy());
		}
//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
		int count = 0;
		for(int i = 0; i < 100; i++){
//			Mutation muta = MutationFactory.getRemove();
			Program pro = pros.get(i);
			StringBuilder sb1 = new StringBuilder();
			pro.prettyPrint(sb1);
//s			System.out.println((pro.prettyPrint(sb1)));	
			pro.mutate();
			StringBuilder sb2 = new StringBuilder();
			pro.prettyPrint(sb2);
//			System.out.println(());	
			if(!sb1.toString().equals(sb2.toString()))
				count++;
//			if(succ) count++;
		}
		System.out.println("duplicated");
		System.out.println(count);

//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
	}
	
}
