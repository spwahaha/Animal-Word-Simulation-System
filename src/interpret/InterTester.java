package interpret;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;

import ast.Program;
import parse.Parser;
import parse.ParserImpl;
import simulation.Critter;

public class InterTester {

	@Test
	public void test() throws FileNotFoundException {
		Parser parser = new ParserImpl();
		Program prog = parser.parse(new FileReader("examples/test1.txt"));
		StringBuilder sb = new StringBuilder();
		prog.prettyPrint(sb);
		Critter cri1 = new Critter();
		InterpreterImpl inter = new InterpreterImpl(cri1, prog);
		inter.interpret();
		System.out.println("prog");
		
	}

}
