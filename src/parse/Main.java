package parse;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import ast.Mutation;
import ast.MutationFactory;
import ast.Node;
import ast.Program;

public class Main {
	public static void main(String[] args) throws FileNotFoundException{
		Parser parser = new ParserImpl();
		Program prog = parser.parse(new FileReader("examples/test1.txt"));
		StringBuilder sb = new StringBuilder();
		prog.prettyPrint(sb);
//		ArrayList<Program> pros = new ArrayList<Program>();
//		for(int i = 0; i < 100; i++){
//			pros.add(prog.copy());
//		}
//		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));
//		int count = 0;
//		for(int i = 0; i < 100; i++){
//			Mutation muta = MutationFactory.getRemove();
//			Program pro = pros.get(i);
//			muta.setProgram(pro);
//			boolean succ = muta.getMutated();
//			System.out.println("mutated");
//			System.out.println((pro.prettyPrint(new StringBuilder()).toString()));	
//			if(succ) count++;
//		}
//		System.out.println(count);
		prog = prog.mutate();
		System.out.println((prog.prettyPrint(new StringBuilder()).toString()));

	}
}
