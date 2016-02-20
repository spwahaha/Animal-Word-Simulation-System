package parse;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class TokenTest {
	public static void main(String[] args){
		try {
			Tokenizer tn = new Tokenizer(new FileReader("examples/unmutated_critter.txt"));
			while(tn.hasNext()){
				System.out.println(tn.next());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
