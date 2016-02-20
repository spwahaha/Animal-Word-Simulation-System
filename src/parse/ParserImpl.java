package parse;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ast.Action;
import ast.BinaryCondition;
import ast.BinaryCondition.Operator;
import ast.BinaryExpr;
import ast.BinaryRel;
import ast.Command;
import ast.Condition;
import ast.Expr;
import ast.NamedExpr;
import ast.NamedExpr.Kind;
import ast.NamedUnaryExpr;
import ast.Number;
import ast.Program;
import ast.ProgramImpl;
import ast.Rule;
import ast.UnaryExpr;
import ast.UniaryAction;
import ast.Update;
import exceptions.SyntaxError;

public class ParserImpl implements Parser {
    protected static final Map<TokenType, Action.Kind> actionMap =
            createTokenLookupMap(Action.Kind.values());
    protected static final Map<TokenType, BinaryRel.Operator> relOpMap =
            createTokenLookupMap(BinaryRel.Operator.values());
    protected static final Map<TokenType, BinaryCondition.Operator> conditionMap =
            createTokenLookupMap(BinaryCondition.Operator.values());
    protected static final Map<TokenType, BinaryExpr.Operator> ExprMap =
            createTokenLookupMap(BinaryExpr.Operator.values());
    protected static final Map<TokenType, NamedExpr.Kind> SensorMap = 
    		createTokenLookupMap(NamedExpr.Kind.values());
    protected static final Map<TokenType, UnaryExpr.Operator> unExprOpMap =
    		createTokenLookupMap(UnaryExpr.Operator.values());

    
	
	
	protected static <E> Map<TokenType, E> createTokenLookupMap(E[] values){
		Map<TokenType, E> map = new HashMap<TokenType,E>();
		for(E e:values){
			map.put(TokenType.getTypeFromString(e.toString()), e);
		}
		return map;
	}
	
	
	
    @Override
    public Program parse(Reader r) {
        // TODO
	    	Tokenizer tn = new Tokenizer(r);
	    	try {
				return parseProgram(tn);
			} catch (SyntaxError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	 //       throw new UnsupportedOperationException();

    }
    
    public Program parse(Tokenizer t){
    	try {
			return parseProgram(t);
		} catch (SyntaxError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    /** Parses a program from the stream of tokens provided by the Tokenizer,
     *  consuming tokens representing the program. All following methods with
     *  a name "parseX" have the same spec except that they parse syntactic form
     *  X.
     *  @return the created AST
     *  @throws SyntaxError if there the input tokens have invalid syntax
     */
    public static ProgramImpl parseProgram(Tokenizer t) throws SyntaxError {
        // TODO
    	ArrayList<Rule> rules = new ArrayList<Rule>();
        while(t.hasNext()){
        	rules.add(parseRule(t));
        }
        return new ProgramImpl(rules);
    }

    public static Rule parseRule(Tokenizer t) throws SyntaxError {
        // TODO
        Condition cond = parseCondition(t);
        consume(t,TokenType.ARR);
//        System.out.println(t.peek());
        Command com = parseCommand(t);
        consume(t,TokenType.SEMICOLON);
        return new Rule(cond,com);
    }

	public static Condition parseCondition(Tokenizer t) throws SyntaxError {
        // TODO
		Condition con1 = parseConjunction(t);
		
		while(t.hasNext()&&t.peek().getType() == TokenType.OR){
			Token token = t.next();
			BinaryCondition.Operator op = conditionMap.get(token.getType());
			Condition con2 = parseConjunction(t);
			con1 = new BinaryCondition(con1,op,con2);
		}
		
		return con1;
		
    }

    private static Condition parseConjunction(Tokenizer t) throws SyntaxError  {
		// TODO Auto-generated method stub
		Condition rel1 = parseRelation(t);
		while(t.hasNext()&&t.peek().getType()==TokenType.AND){
			Operator op =  conditionMap.get(t.next().getType());
			Condition rel2 = parseRelation(t);
			rel1 = new BinaryCondition(rel1, op, rel2);
		}
		return rel1;
		
	}



	private static Condition parseRelation(Tokenizer t) throws SyntaxError {
		// TODO Auto-generated method stub
		Token token = t.peek();
		if(token.getType()==TokenType.LBRACE){
			//parseCondition
			consume(t,TokenType.LBRACE);
			Condition con = parseCondition(t);
			consume(t,TokenType.RBRACE);
			return con;
		}else{
			Expr ex1 = parseExpression(t);
			ast.BinaryRel.Operator op =relOpMap.get(t.next().getType());
			Expr ex2 = parseExpression(t);
			return new BinaryRel(ex1, op, ex2);
		}		
	}



	public static Expr parseExpression(Tokenizer t) throws SyntaxError {
        // TODO
		Expr term1 = parseTerm(t);
		while(t.hasNext()&&t.peek().isAddOp()){
			ast.BinaryExpr.Operator op = ExprMap.get(t.next().getType());
			Expr term2 = parseTerm(t);
			term1 = new BinaryExpr(term1, op, term2);
		}
		return term1;
    }

    public static Expr parseTerm(Tokenizer t) throws SyntaxError {
        // TODO
    	Expr factor1 = parseFactor(t);
    	while(t.hasNext()&&t.peek().isMulOp()){
    		ast.BinaryExpr.Operator op = ExprMap.get(t.next().getType());
    		Expr factor2 = parseFactor(t);
    		factor1 = new BinaryExpr(factor1, op, factor2);
    	}
    	
    	return factor1;
    }

    public static Expr parseFactor(Tokenizer t) throws SyntaxError {
        // TODO
    	Token token = t.peek();
    	Expr expr = null;
    	if(token.isSensor()||token.getType() == TokenType.MEM || token.isMemSugar()){
    		//sensor
    		expr = parseSensor(t);
    	}else if(token.isNum()){
    		// is number
    		expr = parseNumber(t);
    	}else if(token.getType() == TokenType.LPAREN){
    		// (expr)
    		consume(t,TokenType.LPAREN);
    		expr = parseExpression(t);
    		consume(t,TokenType.RPAREN);
    	}
    	else if(token.getType() == TokenType.MINUS){
    		expr = parseUnaryExpr(t);
    		
    	}
    	
    	return expr;
    }
    
    
    




	private static Expr parseSensor(Tokenizer t) throws SyntaxError {
		// TODO Auto-generated method stub
		checkHasNext(t);
		Token token = t.peek();

		if(token.isMemSugar()||token.getType()==TokenType.MEM){
			Expr index = parseMem(t);
			Kind kind = NamedExpr.Kind.MEM;
			return new NamedUnaryExpr(kind, index);
		}else if(token.getType()==TokenType.SMELL){
			NamedExpr.Kind kind = SensorMap.get(t.next().getType());
			return new NamedExpr(kind);
		}else{
			NamedExpr.Kind kind = SensorMap.get(t.next().getType());
			consume(t,TokenType.LBRACKET);
			Expr expr = parseExpression(t);
			consume(t,TokenType.RBRACKET);
			return new NamedUnaryExpr(kind,expr);
		}
	}



	private static Expr parseNumber(Tokenizer t) throws SyntaxError {
		// TODO Auto-generated method stub
    	Token token = t.peek();
    	if(!token.isNum()){
//    		error(token + "is not a number");
    	}
    	int num = Integer.parseInt(t.next().toString());
    	return new Number(num);
	}

    private static Expr parseUnaryExpr(Tokenizer t) throws SyntaxError {
		// TODO Auto-generated method stub
		ast.UnaryExpr.Operator op = unExprOpMap.get(t.next().getType());
		Expr expr = parseFactor(t);
		return new UnaryExpr(op, expr);
	}

	private static Command parseCommand(Tokenizer t) throws SyntaxError {
		// TODO Auto-generated method stub
    	ArrayList<Update> updates = new ArrayList<Update>();
    	Action action = null;
    	// change here, handle one action instead of more times
    	while(t.hasNext()&&t.peek().getType()!=TokenType.SEMICOLON&&t.peek().getType()!=TokenType.EOF){
    		Token token = t.peek();
    		if(token.isAction()){
    			t.next();
    			Action.Kind kind = actionMap.get(token.getType());
    			switch(kind){
    			case TAG:
    			case SERVE:
    				consume(t,TokenType.LBRACKET);
//    				System.out.println(t.peek());
    				Expr index = parseExpression(t);
    				consume(t,TokenType.RBRACKET);
//    				System.out.println(t.peek());
    				action = new UniaryAction(kind, index);
    				break;
   				default:
   					action = new Action(kind);
   					break;
    			}
    		}
    		if(t.peek().isMemSugar()||t.peek().getType()==TokenType.MEM){
    	   		updates.add(parseUpdate(t));
    		}
//    		System.out.println(t.peek());
    	}
		return new Command(updates,action);
	}

	private static Update parseUpdate(Tokenizer t) throws SyntaxError {
		// TODO Auto-generated method stub
	
		checkHasNext(t);
		Token token = t.peek();
		Expr index = parseMem(t);
		consume(t,TokenType.ASSIGN);
		Expr expr = parseExpression(t);
		
		return new Update(index, expr);

	}

	private static Expr parseMem(Tokenizer t) throws SyntaxError {
		// TODO Auto-generated method stub
		checkHasNext(t);
		Token token = t.peek();
		if(token.isMemSugar()){
			token = t.next();
			switch(token.getType()){
			case ABV_MEMSIZE:
				return new Number(0);
			case ABV_DEFENSE:
				return new Number(1);
			case ABV_OFFENSE:
				return new Number(2);
			case ABV_SIZE:
				return new Number(3);
			case ABV_ENERGY:
				return new Number(4);
			case ABV_PASS:
				return new Number(5);
			case ABV_TAG:
				return new Number(6);
			case ABV_POSTURE:
				return new Number(7);
			default:
				error("wrong sugar");
				return null;
			}
		}
		
		consume(t, TokenType.MEM);
		consume(t,TokenType.LBRACKET);
		Expr index = parseExpression(t);
		consume(t,TokenType.RBRACKET);
		return index;
	}




    // TODO
    // add more as necessary...

    /**
     * Consumes a token of the expected type.
     * @throws SyntaxError if the wrong kind of token is encountered.
     */
    public static void consume(Tokenizer t, TokenType tt) throws SyntaxError {
        // TODO
    	checkHasNext(t);
        Token token = t.next();
        if(token.getType()!=(tt)){
        	error("expected" + tt);
        }
    }
    
    public static void checkHasNext(Tokenizer t) throws SyntaxError{
    	if(!t.hasNext())
    		error("Unexpected end of line");
    }
    
	protected static SyntaxError se = new SyntaxError();
	private static void error(String string) throws SyntaxError{
		// TODO Auto-generated method stub
		se.setMessage(string);
		throw se;
	}
    
    
}
