import symtab.*;
import java.util.*;

public class DefSymbols extends DecafBaseListener {
	protected BasicScope globals;
	protected Scope currentScope = null;
	protected Dictionary errors = new Hashtable();

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
		for (Enumeration k = errors.keys(); k.hasMoreElements();) {
			System.out.println(k.nextElement());
		}
	}

	@Override
	public void enterMethod_decl(DecafParser.Method_declContext ctx) {
		// Define a function symbol in the current scope
		// make that function symbol the current scope
		String funcName = ctx.ID(0).getText();
//		System.out.println(funcName);
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
			System.out.println(varName);
			VariableSymbol sym = new VariableSymbol(varName);
			if (currentScope.resolve(varName) == null) {
				currentScope.define(sym);
			} else {
				String key = String.format("VarError: %s is already declared", varName);
				errors.put(key, "1");
			}
		}
	}
	@Override
	public void enterField_decl(DecafParser.Field_declContext ctx) {
		int numVars = ctx.line().size();
		for (int i = 0; i < numVars; i++){
			String varName = ctx.line(i).getText();
			System.out.println(varName);
			VariableSymbol sym = new VariableSymbol(varName);
			if (currentScope.resolve(varName) == null) {
				currentScope.define(sym);
			} else {
				String key = String.format("VarError: %s is already declared", varName);
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
		String varName = ctx.location().getText();
		Symbol sym = currentScope.resolve(varName);
		if (sym == null) {
			String key = String.format("Error: Identifier '" + varName + "' used without being declared.");
			errors.put(key, "1");
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
