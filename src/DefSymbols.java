import symtab.*;
import java.util.*;

public class DefSymbols extends DecafBaseListener {
	protected BasicScope globals;
	protected Scope currentScope = null;
	protected Dictionary errors = new Hashtable();
	protected List<String> methods = new ArrayList<String>();

	// Handle Scopes

	@Override
	public void enterProgram(DecafParser.ProgramContext ctx) {
		// create a global scope
		globals = new BasicScope(null);
		// set currentScope to globals;
		currentScope = globals;
		ctx.scope = currentScope;
	}

	@Override
	public void exitProgram(DecafParser.ProgramContext ctx) {
		// pop the scope
		currentScope = currentScope.getEnclosingScope();
		boolean flag = false;
		for (String method : methods)
		{
			if (Objects.equals(method, "main"))
			{
				flag = true;
				break;
			}
		}
		if (!flag) {
			String key = "Error: No main method.";
			errors.put(key, "1");
		}
		for (Enumeration k = errors.keys(); k.hasMoreElements();) {
			System.out.println(k.nextElement());
		}
	}

	@Override
	public void enterMethod_decl(DecafParser.Method_declContext ctx) {
		// Define a function symbol in the current scope
		// make that function symbol the current scope
		if (ctx.getChild(0).getText().equalsIgnoreCase("void") ){
//			System.out.println(ctx.block().statement(0).getText());
			for (var el:ctx.block().statement()) {

//				System.out.println(el.getRuleIndex());
//				System.out.println(el.getChild(0).getText());
				if (el.getChild(0).getText().equalsIgnoreCase("return")){
					String key = "Can't have return in void type.";
					errors.put(key, "1");
				}
			}
		}
		else {
			int temp =0;
			if (ctx.block().statement().size() ==0){
				String key = String.format("must have return in "+ctx.getChild(0).getText() +" type.");
				errors.put(key, "1");
			}
			for (var el:ctx.block().statement()) {

//				System.out.println(el.getRuleIndex());
//				System.out.println(el.getChild(0).getText());
				if (!el.getChild(0).getText().equalsIgnoreCase("return")){
					temp++;

				}
			}
			if (temp ==ctx.block().statement().size()){
				String key = String.format("must have return in "+ctx.getChild(0).getText() +" type");
				errors.put(key, "1");
			}
		}
		String funcName = ctx.ID(0).getText();
//		System.out.println(funcName);
		methods.add(funcName);
		FunctionSymbol funcsymbol = new FunctionSymbol(funcName, currentScope);
		currentScope.define(funcsymbol);
		currentScope = funcsymbol;
		ctx.scope = currentScope;

	}

	@Override
	public void exitMethod_decl(DecafParser.Method_declContext ctx) {
		// pop the scope
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterVar_decl(DecafParser.Var_declContext ctx) {
		int numVars = ctx.ID().size();
		for (int i = 0; i < numVars; i++){
			String varName = ctx.ID(i).getText();
			VariableSymbol sym = new VariableSymbol(varName);
			if (currentScope.resolve(varName) == null) {
				currentScope.define(sym);
			} else if (currentScope.getEnclosingScope().resolve(varName) == null) {
					String key = String.format("VarError: %s is already declared", varName);
					errors.put(key, "1");
					}
			}
		}

	@Override
	public void enterField_decl(DecafParser.Field_declContext ctx) {
		int numVars = ctx.line().size();
		for (int i = 0; i < numVars; i++){
			String varName = ctx.line(i).ID().getText();
			VariableSymbol sym = new VariableSymbol(varName);
			if (currentScope.resolve(varName) == null) {
				currentScope.define(sym);
			} else if (currentScope.getEnclosingScope().resolve(varName) == null) {
				String key = String.format("VarError: %s is already declared", varName);
				errors.put(key, "1");
			}
			if (ctx.line(i).INT_LITERAL() != null && Integer.parseInt(ctx.line(i).INT_LITERAL().getText()) <= 0)
			{
				String key = "Error: The'int literal' in an array declaration must be greater than 0.";
				errors.put(key, "1");
			}
		}
	}

	@Override
	public void enterBlock(DecafParser.BlockContext ctx) {
		// push a new BasicScope
		currentScope = new BasicScope(currentScope);
		ctx.scope = currentScope;
	}

	@Override
	public void exitBlock(DecafParser.BlockContext ctx) {
		// pop the scope
		currentScope = currentScope.getEnclosingScope();
	}

	// Handle refs
	@Override
	public void enterStatement(DecafParser.StatementContext ctx) {
		if (ctx.location() != null) {
			String varName = ctx.location().ID().getText();
			Symbol sym = currentScope.resolve(varName);
			if (sym == null) {
				String key = String.format("Error: Identifier '" + varName + "' used without being declared.");
				errors.put(key, "1");
			}
		}
		if (ctx.getText().substring(0, 2).equals("if")) {
			System.out.println(ctx.expr(0));
		}

	}

//	@Override
//	public void enterExpr(DecafParser.ExprContext ctx) {
//		if (ctx.ID() != null) {
//			String varName = ctx.ID().getText();
//			Symbol sym = currentScope.resolve(varName);
//			if (sym == null) {
//				System.err.println("No such var: " + varName);
//			}
//		}
//	}
}
