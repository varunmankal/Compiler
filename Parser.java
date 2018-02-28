package cop5556fa17;



import java.util.ArrayList;
import java.util.Arrays;


import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.AST.*;

import static cop5556fa17.Scanner.Kind.*;



public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
		SymbolTable.getInstance();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program e=program();
		matchEOF();
		return e;
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	public Program program() throws SyntaxException {
		//TODO  implement this
		
		Token literal=t;
		ArrayList<ASTNode> decs=new ArrayList<>();
		match(IDENTIFIER);
		while(t.kind==KW_int || t.kind==KW_boolean || t.kind==KW_image || t.kind==KW_url || t.kind==Kind.KW_file
			||	t.kind==IDENTIFIER)
		{
			if(t.kind==KW_int || t.kind==KW_boolean || t.kind==KW_image || t.kind==KW_url || t.kind==Kind.KW_file)
			{
				decs.add(declaration());
				match(SEMI);
			}
			else if(t.kind==IDENTIFIER)
			{
				decs.add(statement());
				match(SEMI);
			}
		}
		Program p=new Program(literal, literal, decs);
			return p;
		//throw new UnsupportedOperationException();
	}
	
	Declaration declaration() throws SyntaxException
	{
		if(t.kind==KW_int || t.kind==KW_boolean)
			return variabledeclaration();
		else if(t.kind==KW_image)
			return imageDeclaration();
		else if(t.kind==Kind.KW_url || t.kind==Kind.KW_file)
		{
			return sourceSinkDeclaration();
		}
		else
		{
			String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		
	}
	
	

	Declaration_Variable variabledeclaration() throws SyntaxException
	{
		Token literal_1=t;
		Expression e=null;
		varType();
		Token literal_2=t;
		match(IDENTIFIER);
		if(t.kind==OP_ASSIGN)
		{
			match(OP_ASSIGN);
			e=expression();
		}
		else 
			;
		Declaration_Variable dv=new Declaration_Variable(literal_1,literal_1,literal_2,e);
		return dv;
	}
	
	
	
	void varType() throws SyntaxException
	{
		if(t.kind==KW_int)
		{
			match(KW_int);
		}
		else if(t.kind==KW_boolean)
		{
			match(KW_boolean);
		}
		else
			{String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);}
	}
	
	Declaration_Image imageDeclaration() throws SyntaxException
	{
		
		Expression e1=null,e2=null;
		Source s=null;
		Token literal_1=t;
		match(KW_image);
		if(t.kind==Kind.LSQUARE)
		{
		match(LSQUARE);
		e1=expression();
		match(COMMA);
		e2=expression();
		match(RSQUARE);
		}
		else
			;
		//imageDeclarationPrime(sam);
		Token literal_2=t;
		match(IDENTIFIER);
		if(t.kind==OP_LARROW)
		{
			match(OP_LARROW);
			s=source();
		}
		else
			;
		Declaration_Image di=new Declaration_Image(literal_1,e1,e2,literal_2,s);
		return di;
	}
	
	

//	private void imageDeclarationPrime() throws SyntaxException {
//		// TODO Auto-generated method stub
//		if(t.kind==Kind.LSQUARE)
//		{
//		match(LSQUARE);
//		sample.exp1=expression();
//		match(COMMA);
//		sample.exp2=expression();
//		match(RSQUARE);
//		}
//		else
//			;
//	}
	
	Source source() throws SyntaxException
	{
		Token literal=t;
		Source s;
		if(t.kind==Kind.STRING_LITERAL)
		{
			//match(STRING_LITERAL);
			return source_literal();
		}
		else if(t.kind==Kind.IDENTIFIER)
		{
			//match(IDENTIFIER);
			return source_ident();
		}
		else if(t.kind==OP_AT)
		{
			//match(OP_AT);
			//expression();
			return source_command();
		}
		else
		{
			String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);
		}
		
		//return s;
	}
	
	Source_StringLiteral source_literal() throws SyntaxException 
	{
		Token literal=t;
		match(Kind.STRING_LITERAL);
		Source_StringLiteral st=new Source_StringLiteral(literal, literal.getText());
		return st;
	}
	
	Source_Ident source_ident() throws SyntaxException
	{
		Token literal=t;
		match(IDENTIFIER);
		Source_Ident si =new Source_Ident(literal,literal);
		return si;
	}
	
	Source_CommandLineParam source_command() throws SyntaxException
	{
		Token literal=t;
		match(OP_AT);
		Expression e=expression();
		Source_CommandLineParam sc=new Source_CommandLineParam(literal,e);
		return sc;
	}
	Statement statement() throws SyntaxException
	{
		Token literal=t;
		match(IDENTIFIER);
		return statementPrime(literal);
	}
	
	Statement statementPrime(Token lit) throws SyntaxException
	{
		
		if(t.kind==Kind.LSQUARE)
		{
			//match(LSQUARE);
			//lhsSelector();
			//match(RSQUARE);
			return assignStmt(lit);
//			lhs();
//			match(OP_ASSIGN);
//			expression();
			
		}
//		else if(t.kind==OP_ASSIGN)
//		{
//			match(OP_ASSIGN);
//			expression();
//		}
		else if(t.kind==Kind.OP_RARROW)
		{
			return imageOut(lit);
//			match(OP_RARROW);
//			sink();
		}
		else if(t.kind==Kind.OP_LARROW)
		{
			return imageIn(lit);
//			match(OP_LARROW);
//			source();
		}
		else
		{
			//String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;
			//throw new SyntaxException(t, message);
			return assignStmt(lit);
		}
		
     
	}
	Statement_Assign assignStmt(Token token) throws SyntaxException
	{
		LHS l;
		Expression e;
		l=lhs(token);
		match(OP_ASSIGN);
		e=expression();
		Statement_Assign sa=new Statement_Assign(token,l,e);
		return sa;
	}
	
	Statement_In imageIn(Token token) throws SyntaxException
	{
		
		match(OP_LARROW);
		Source s=source();
		Statement_In si=new Statement_In(token,token,s);
		return si;
	}
	Statement_Out imageOut(Token token) throws SyntaxException
	{
		match(OP_RARROW);
		Sink s=sink();
		Statement_Out so=new Statement_Out(token,token,s);
		return so;
		
	}
	
	LHS lhs(Token token) throws SyntaxException
	{
		Index in=null;
		if(t.kind==Kind.LSQUARE)
		{
			match(LSQUARE);
			in=lhsSelector();
			match(RSQUARE);
		}
		else
			;
		LHS l=new LHS(token,token,in);
		return l;
	}
	
	
	Index lhsSelector() throws SyntaxException
	{
			Index in;
			match(LSQUARE);
			if(t.kind==KW_x)
			{
				in=xySelector();
			}
			else if(t.kind==KW_r)
			{
				in=raSelector();
			}
			else
				{String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);}
			match(RSQUARE);
		return in;
	}
	
	Index xySelector() throws SyntaxException
	{
		Token literal=t;
		Expression e0=null,e1=null;
		e0=new Expression_PredefinedName(literal, t.kind);
		match(KW_x);
		match(COMMA);
		e1=new Expression_PredefinedName(literal, t.kind);
		match(KW_y);
		Index in=new Index(literal,e0,e1);
		return in;
	}
	
	Index raSelector() throws SyntaxException
	{
		Token literal=t;
		Expression e0=null,e1=null;
		e0=new Expression_PredefinedName(literal, t.kind);
		match(KW_r);
		match(COMMA);
		e1=new Expression_PredefinedName(literal, t.kind);
		match(KW_a);
		Index in=new Index(literal,e0,e1);
		return in;
	}
	
	Sink sink() throws SyntaxException
	{
		if(t.kind==Kind.IDENTIFIER)
		{
			//match(IDENTIFIER);
			return sink_indent();
		}
		else if(t.kind==Kind.KW_SCREEN)
		{
			//match(KW_SCREEN);
			return sink_screen();
		}
		else
		{
			String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);
		}
		
	}
	
	
	 private Sink_SCREEN sink_screen() throws SyntaxException {
		// TODO Auto-generated method stub
		 Token literal=t;
		 match(KW_SCREEN);
		 Sink_SCREEN si=new Sink_SCREEN(literal);
		 return si;
	}

	private Sink_Ident sink_indent() throws SyntaxException {
		// TODO Auto-generated method stub
		 Token literal=t;
		 match(IDENTIFIER);
		 Sink_Ident si=new Sink_Ident(literal, literal);
		 return si;
	}

	Declaration_SourceSink sourceSinkDeclaration() throws SyntaxException {
		// TODO Auto-generated method stub
		Token literal_1=t;
		sourceSinkType();
		Token literal_2=t;
		match(IDENTIFIER);
		match(OP_ASSIGN);
		Source s=source();
		Declaration_SourceSink ds=new Declaration_SourceSink(literal_1,literal_1,literal_2,s);
		return ds;
	}
	
	

	 void sourceSinkType() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.kind==KW_url)
		{
			match(KW_url);
		}
		else if(t.kind==KW_file)
		{
			match(KW_file);
		}
		else
			{String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);}
	}

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	 public Expression expression() throws SyntaxException {
		//TODO implement this.
		Token literal=t;
		Expression e;
		e=orExpression();
		return expressionPrime(e,literal);	
		
		//throw new UnsupportedOperationException();
	}

	private Expression expressionPrime(Expression e,Token literal) throws SyntaxException {
		// TODO Auto-generated method stub
		Expression e1=null;
		Expression e2=null;
		Expression e3=null;
		if(t.kind==OP_Q)
		{
			match(OP_Q);
			e1=expression();
			match(OP_COLON);
			e2=expression();
			e3=new Expression_Conditional(literal,e,e1,e2);
		}
		else
			e3=e;
		
		return e3;
	}

	Expression orExpression() throws SyntaxException{
		Token literal1=t;
		Expression e1=null;
		Expression e2=null;
		Token literal2=null;
		e1=andExpression();
		while(t.kind==OP_OR)
		{
			literal2=t;
			match(OP_OR);
			e2=andExpression();
			e1=new Expression_Binary(literal1,e1,literal2,e2);
		}
		//Expression_Binary eb=new Expression_Binary(literal1,e1,literal2,e2);
		return e1;
	}
	
	Expression andExpression() throws SyntaxException{
		Token literal1=t;
		Expression e1=null;
		e1=eqExpression();
		Expression e2=null;
		Token literal2=null;
		while(t.kind==OP_AND)
		{
			literal2=t;
			match(OP_AND);
			e2=eqExpression();
			e1=new Expression_Binary(literal1,e1,literal2,e2);
		}
		//Expression_Binary eb=new Expression_Binary(literal1,e1,literal2,e2);
		return e1;
		
	}
	
	Expression eqExpression() throws SyntaxException{
		Token literal1=t;
		Expression e1=null;
		e1=relExpression();
		Expression e2=null;
		Token literal2=null;
		while(t.kind==OP_EQ || t.kind==OP_NEQ)
		{
			literal2=t;
			if(t.kind==OP_EQ)
				match(OP_EQ);
			else if(t.kind==OP_NEQ)
				match(OP_NEQ);
			else
				{String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);}
			e2=relExpression();
			e1=new Expression_Binary(literal1,e1,literal2,e2);
		}
		//Expression_Binary eb=new Expression_Binary(literal1,e1,literal2,e2);
		return e1;
		
	}
	
	Expression relExpression() throws SyntaxException{
		Token literal1=t;
		Expression e1=null;
		e1=addExpression();
		Expression e2=null;
		Token literal2=null;
		while(t.kind==OP_LT || t.kind==OP_GT || t.kind==OP_LE || t.kind==OP_GE)
		{
			literal2=t;
			if(t.kind==OP_LT)
				match(OP_LT);
			else if(t.kind==OP_GT)
				match(OP_GT);
			else if(t.kind==OP_GE)
				match(OP_GE);
			else if(t.kind==OP_LE)
				match(OP_LE);
			else
				{String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);}
			e2=addExpression();
			e1=new Expression_Binary(literal1,e1,literal2,e2);
		}
		//Expression_Binary eb=new Expression_Binary(literal1,e1,literal2,e2);
		return e1;
		
	}
	
	Expression addExpression() throws SyntaxException{
		Token literal1=t;
		Expression e1=null;
		e1=mulExpression();
		Expression e2=null;
		Token literal2=null;
		while(t.kind==Kind.OP_MINUS || t.kind==OP_PLUS)
		{
			literal2=t;
			if(t.kind==OP_PLUS)
				match(OP_PLUS);
			else if(t.kind==OP_MINUS)
				match(OP_MINUS);
			else
				{String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);}
			e2=mulExpression();
			e1=new Expression_Binary(literal1,e1,literal2,e2);
		}
		//Expression_Binary eb=new Expression_Binary(literal1,e1,literal2,e2);
		return e1;
		
	}
	Expression mulExpression() throws SyntaxException{
		Token literal1=t;
		Expression e1=null;
		e1=unaryExpression();
		Expression e2=null;
		Token literal2=null;
		while(t.kind==OP_TIMES || t.kind==Kind.OP_DIV || t.kind==OP_MOD)
		{
			literal2=t;
			if(t.kind==Kind.OP_TIMES)
				match(OP_TIMES);
			else if(t.kind==OP_DIV)
				match(OP_DIV);
			else if(t.kind==OP_MOD)
				match(OP_MOD);
			else
				{String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);}
			e2=unaryExpression();
			e1=new Expression_Binary(literal1,e1,literal2,e2);
		}
		//Expression_Binary eb=new Expression_Binary(literal1,e1,literal2,e2);
		return e1;
		
	}
	
	Expression unaryExpression() throws SyntaxException
	{
		Token literal=t;
		Expression e;
		if(t.kind==OP_PLUS)
		{
			match(OP_PLUS);
			e=unaryExpression();	
		}
		else if(t.kind==Kind.OP_MINUS)
		{
			match(OP_MINUS);
			e=unaryExpression();	
		}
		else if(t.kind==INTEGER_LITERAL || t.kind==LPAREN || t.kind==Kind.KW_sin || t.kind==KW_cos || t.kind==KW_atan
				|| t.kind==KW_abs || t.kind==KW_cart_y || t.kind==Kind.KW_cart_x || t.kind==KW_polar_a ||
				t.kind==KW_polar_r || t.kind==Kind.IDENTIFIER || t.kind==KW_x || t.kind==KW_y ||
				t.kind==KW_r || t.kind==KW_a || t.kind==Kind.OP_EXCL || t.kind==KW_X || t.kind==KW_Y ||
				t.kind==KW_Z || t.kind==KW_A || t.kind==KW_R || t.kind==Kind.KW_DEF_X || t.kind==Kind.KW_DEF_Y 
				|| t.kind==BOOLEAN_LITERAL)
		{
			return unaryExpressionNotPlusMinus();
		}
		else
		{
			String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		Expression_Unary eu=new Expression_Unary(literal,literal,e);
		return eu;
		
	}
	

	Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		// TODO Auto-generated method stub
		 Token literal=t;
		 Expression e;
		if(t.kind==Kind.OP_EXCL)
		{
			match(OP_EXCL);
			e=unaryExpression();
		}
		else if(t.kind==INTEGER_LITERAL || t.kind==LPAREN || t.kind==Kind.KW_sin || t.kind==KW_cos || t.kind==KW_atan
				|| t.kind==KW_abs || t.kind==KW_cart_y || t.kind==Kind.KW_cart_x || t.kind==KW_polar_a ||
				t.kind==KW_polar_r || t.kind==BOOLEAN_LITERAL)
		{
			e=primary();
			return e;
		}
		else if(t.kind==Kind.IDENTIFIER)
		{
			e=identOrPixel();
			return e;
		}
		else if(t.kind==KW_x || t.kind==KW_y ||
				t.kind==KW_r || t.kind==KW_a || t.kind==KW_X || t.kind==KW_Y ||
				t.kind==KW_Z || t.kind==KW_A || t.kind==KW_R || t.kind==Kind.KW_DEF_X || t.kind==Kind.KW_DEF_Y)
		{
			t=scanner.nextToken();
			e=new Expression_PredefinedName(literal, literal.kind);
			return e;
		}
		else 
		{
			String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		Expression eu=new Expression_Unary(literal,literal,e);
		return eu;
	}

	 Expression identOrPixel() throws SyntaxException {
			// TODO Auto-generated method stub
		 	Token literal=t;
			match(IDENTIFIER);
			return identOrPixelPrime(literal);
		}
		

	Expression identOrPixelPrime(Token token) throws SyntaxException {
			// TODO Auto-generated method stub
			 Index in;
			 Expression e;
			if(t.kind==Kind.LSQUARE)
			{
				match(LSQUARE);
				in=selector();
				match(RSQUARE);
				e=new Expression_PixelSelector(token, token, in);
			}
			else
			{	
				e=new Expression_Ident(token, token);
			}
			return e;
		}

	 Index selector() throws SyntaxException {
		// TODO Auto-generated method stub
		Token literal=t;
		Expression e1=expression();
		match(COMMA);
		Expression e2=expression();
		Index in=new Index(literal,e1,e2);
		return in;
	}

	Expression primary() throws SyntaxException
	{
		Expression e;
		if(t.kind==INTEGER_LITERAL)
		{
			//match(INTEGER_LITERAL);
			e=primary_literal();
		}
		else if(t.kind==LPAREN)
		{
			match(LPAREN);
			e=expression();
			match(RPAREN);
		}
		else if(t.kind==Kind.KW_sin || t.kind==KW_cos || t.kind==KW_atan
				|| t.kind==KW_abs || t.kind==KW_cart_y || t.kind==Kind.KW_cart_x || t.kind==KW_polar_a ||
				t.kind==KW_polar_r)
		{
			e=functionApplication();
		}
		else if(t.kind==Kind.BOOLEAN_LITERAL)
		{
			//match(BOOLEAN_LITERAL);
			e=primary_bool();
		}
		else
		{
			String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return e;
	}
	
	private Expression_BooleanLit primary_bool() throws SyntaxException {
		// TODO Auto-generated method stub
		Token literal=t;
		match(BOOLEAN_LITERAL);
		
		Expression_BooleanLit eb=new Expression_BooleanLit(literal,literal.getText().equals("true"));
		return eb;
	}

	private Expression_IntLit primary_literal() throws SyntaxException {
		// TODO Auto-generated method stub
		Token literal=t;
		match(INTEGER_LITERAL);
		Expression_IntLit ei=new Expression_IntLit(literal,literal.intVal());
		return ei;
		
	}

	Expression functionApplication() throws SyntaxException
	{
		Token literal=t;
		
		functionName();
		return functionApplicationPrime(literal);
	}
	
	private Expression_FunctionApp functionApplicationPrime(Token literal) throws SyntaxException {
		// TODO Auto-generated method stub
		Expression e;
		Index in;
		if(t.kind==LPAREN)
		{
			match(LPAREN);
			e=expression();
			match(RPAREN);
			return new Expression_FunctionAppWithExprArg(literal,literal.kind,e);
			
		}
		else if(t.kind==Kind.LSQUARE)
		{
			match(LSQUARE);
			in=selector();
			match(RSQUARE);
			return new Expression_FunctionAppWithIndexArg(literal, literal.kind, in);
		}
		else
		{
			String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		
	}

	 void functionName() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.kind==Kind.KW_sin || t.kind==KW_cos || t.kind==KW_atan
				|| t.kind==KW_abs || t.kind==KW_cart_y || t.kind==Kind.KW_cart_x || t.kind==KW_polar_a ||
				t.kind==KW_polar_r)
		{
			t=scanner.nextToken();
		}
		else
		{
			String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		//{String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;{String message =  "Incorrect position of "+t+" at " + t.line + ":" + t.pos_in_line;throw new SyntaxException(t, message);}}
	}
	void match(Kind k) throws SyntaxException
	{
		if(t.kind==k)
		{	
			System.out.println("current token is"+t);
			t=scanner.nextToken();
		}
		else
		{
			String message =  "Expected "+k+" at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t,message);
	}
}
