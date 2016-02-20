package interpret;

import java.util.ArrayList;

import ast.Action;
import ast.BinaryCondition;
import ast.BinaryExpr;
import ast.BinaryRel;
import ast.Command;
import ast.Condition;
import ast.Expr;
import ast.NamedExpr;
import ast.NamedUnaryExpr;
import ast.Program;
import ast.ProgramImpl;
import ast.Rule;
import ast.UnaryExpr;
import ast.UniaryAction;
import ast.Update;
import simulation.Critter;
import simulation.World;
import ast.Number;

public class InterpreterImpl implements Interpreter {
	protected Critter critter;
	protected Program program;
	
	public InterpreterImpl(Critter critter, Program program){
		this.critter = critter;
		this.program = program;
	}
	
	public Outcome interpret(){
		Program program = this.program;
		return interpret(program);

	}
	
	
	public Outcome interpret(Program p, boolean doUpdate) {
		// TODO Auto-generated method stub
		Outcome outcome = null;
		ArrayList<Rule> rules = ((ProgramImpl)program).getRules();
		critter.setMem(5, 0);
		for(int i = 0; i < World.MAX_RULES_PER_TURN; i++){
			Rule rule = rules.get(i % rules.size());
			outcome = InterpretRule(rule,doUpdate);
			if(outcome==null){
				critter.setMem(5, critter.getMem(5) + 1);
			}else{
				if(doUpdate)
					critter.setLastRule(i);
				return outcome;
			}

		}	
		System.out.println("critter pass:" + critter.getMem(5));
		return new OutcomeImpl("wait", -1);
	}
	

	@Override
	public Outcome interpret(Program p) {
		// TODO Auto-generated method stub
		return interpret(p,true);
	}
	
	
	private Outcome InterpretRule(Rule rule, boolean doUpdate) {
		// TODO Auto-generated method stub
		boolean condition = interpretCondition(rule.getCondition());
		Outcome outcome = null;
		if(condition){
			Command command = rule.getCommand();
			ArrayList<Update> updates = command.getUpdates();
			for(Update update:updates){
				int index=  interpretExpr(update.getIndex());
				int expr = interpretExpr(update.getExpr());
				if(doUpdate)
					critter.setMem(index, expr);
			}
			Action action = command.getAction();
			if(action!=null){
				switch(action.getKind()){
				case SERVE:{
					int expr = interpretExpr(((UniaryAction)action).getExpr());
					outcome = new OutcomeImpl(action.getKind().toString(), expr);
					break;
				}
				case TAG:
					{
						int expr = interpretExpr(((UniaryAction)action).getExpr());
						if(expr<0 || expr > Critter.MAX_TAG_VALUE) return null;
						outcome = new OutcomeImpl(action.getKind().toString(), expr);
						break;
					}
				default:
					outcome = new OutcomeImpl(action.getKind().toString(),-1);
				}
			}
			
		}
		
		return outcome;
	}

	private boolean interpretCondition(Condition condition) {
		// TODO Auto-generated method stub
		if(condition instanceof BinaryCondition){
			boolean left = interpretCondition(((BinaryCondition) condition).getLeft());
			boolean right = interpretCondition(((BinaryCondition) condition).getRight());
			switch(((BinaryCondition) condition).getOp()){
			case OR:
				return left || right;
			case AND:
				return left && right;
			}
		}

		if(condition instanceof BinaryRel){
			int left = interpretExpr(((BinaryRel)condition).getLeft());
			int right = interpretExpr(((BinaryRel)condition).getRight());
			switch(((BinaryRel)condition).getOp()){
			case LT: return left < right;
			case LE: return left <= right;
			case EQ: return left == right;
			case GE: return left >= right;
			case GT: return left > right;
			case NE: return left != right;
			}
		}
		return false;
	}
	

	private int interpretExpr(Expr expression) {
		// TODO Auto-generated method stub
		if(expression instanceof BinaryExpr){
			int left = interpretExpr(((BinaryExpr) expression).getLeft());
			int right = interpretExpr(((BinaryExpr)expression).getRight());
			switch(((BinaryExpr) expression).getOp()){
			case PLUS: return left + right;
			case MINUS: return left - right;
			case MUL: return left * right;
			case DIV: {
				if(right==0) return 0;
				return left / right;
			}
			case MOD: return left % right;
			}
		}
		
		if(expression instanceof Number){
			return ((Number)expression).getValue();
		}
		
		if(expression instanceof UnaryExpr){
			return -(interpretExpr(((UnaryExpr) expression).getExpr())); 
		}
		
		if(expression instanceof NamedExpr){
			return interpretNamedExpr((NamedExpr)expression);
		}
		
		return 0;
	}
	private int interpretNamedExpr(NamedExpr expression) {
		// TODO Auto-generated method stub
		if(expression instanceof NamedUnaryExpr){
			int expr = interpretExpr(((NamedUnaryExpr)expression).getExpr());
			switch(expression.getKind()){
			case NEARBY: return critter.nearby(expr % 6);
			case RANDOM: return critter.random(expr);
			case MEM:{
				if(expr > Critter.MAX_MEM_INEDX || expr < 0) return 0;
				return critter.getMem(expr);	
			}
			case AHEAD: return critter.ahead(expr);
			}
		}else return critter.smell(); // SMELL

		return 0;
	}

	@Override
	public boolean eval(Condition c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int eval(Expr e) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
}
