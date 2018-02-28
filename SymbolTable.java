package cop5556fa17;

import java.util.HashMap;
import java.util.Hashtable;

import cop5556fa17.AST.*;

public class SymbolTable {

	HashMap<String,ASTNode> htable;
	public static SymbolTable symTable;
	
	private SymbolTable() {
		// TODO Auto-generated constructor stub
		htable = new HashMap<>();
		
	}
	
	
	
	
	public static SymbolTable getInstance()
	{
		if(symTable == null) symTable=new SymbolTable();
		
		return symTable;
	}

}
