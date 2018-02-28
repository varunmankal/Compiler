package cop5556fa17;

import java.util.HashMap;
import java.lang.Object;
import java.net.MalformedURLException;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.SymbolTable;
import cop5556fa17.Scanner.Kind;
public class TypeCheckVisitor implements ASTVisitor {
	
	HashMap<String,ASTNode> hmap=SymbolTable.getInstance().htable;
	
	
		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		
		
	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		if(!hmap.containsKey(declaration_Variable.name))
		{
			if(declaration_Variable.e!=null)
			{
				declaration_Variable.e.visit(this, arg);
			}
			declaration_Variable.ntype=declaration_Variable.getType(declaration_Variable.type.kind);
			if(declaration_Variable.e!=null)
			{	
				if(declaration_Variable.ntype==declaration_Variable.e.ntype)
				{
					;
				}
				else
				{
					throw new SemanticException(declaration_Variable.firstToken,"invalid expression type");
				}
			}
			hmap.put(declaration_Variable.name, declaration_Variable);
					
		}
		else
		{
			throw new SemanticException(declaration_Variable.firstToken,"invalid type");
		}

		return declaration_Variable.name;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);
		if(expression_Binary.op==Kind.OP_EQ || expression_Binary.op==Kind.OP_NEQ)
		{
			expression_Binary.ntype=Type.BOOLEAN;
		}
		else if((expression_Binary.op==Kind.OP_GE || expression_Binary.op==Kind.OP_GT
				|| expression_Binary.op==Kind.OP_LE || expression_Binary.op==Kind.OP_LT)
			&& expression_Binary.e0.ntype==Type.INTEGER)
		{
			expression_Binary.ntype=Type.BOOLEAN;
		}
		else if((expression_Binary.op==Kind.OP_AND || expression_Binary.op==Kind.OP_OR)
			&& (expression_Binary.e0.ntype==Type.INTEGER || expression_Binary.e0.ntype==Type.BOOLEAN))
		{
			expression_Binary.ntype=expression_Binary.e0.ntype;
		}
		else if((expression_Binary.op==Kind.OP_DIV || expression_Binary.op==Kind.OP_MINUS ||
				expression_Binary.op==Kind.OP_MOD || expression_Binary.op==Kind.OP_POWER ||
				expression_Binary.op==Kind.OP_PLUS || expression_Binary.op==Kind.OP_TIMES) &&
				(expression_Binary.e0.ntype==Type.INTEGER))
		{
			expression_Binary.ntype=Type.INTEGER;
		}
		if(expression_Binary.e0.ntype==expression_Binary.e1.ntype && expression_Binary.ntype!=Type.NONE)
		{
			;
		}
		else
		{
			throw new SemanticException(expression_Binary.firstToken,"invalid type");
		}
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		expression_Unary.e.visit(this, arg);
		Type temp=expression_Unary.e.ntype;
		if((expression_Unary.op==Kind.OP_EXCL)&&(temp==Type.BOOLEAN || temp==Type.INTEGER))
		{
			expression_Unary.ntype=temp;
		}
		else if((expression_Unary.op==Kind.OP_PLUS || expression_Unary.op==Kind.OP_MINUS)
				&& temp==Type.INTEGER)
		{
			expression_Unary.ntype=Type.INTEGER;
		}
		if(expression_Unary.ntype!=Type.NONE)
		{
			;
			
		}
		else
			throw new SemanticException(expression_Unary.firstToken,"invalid type");
		
		return null;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if(index.e0.ntype==Type.INTEGER && index.e1.ntype==Type.INTEGER)
		{
			index.setCartesian(!(index.e0.firstToken.kind==Kind.KW_r && index.e0.getClass()==Expression_PredefinedName.class && index.e1.getClass()==Expression_PredefinedName.class &&  index.e1.firstToken.kind==Kind.KW_a));
		}
		else
		{
			throw new SemanticException(index.firstToken,"invalid indices");
		}
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expression_PixelSelector.index.visit(this, arg);
		if(hmap.containsKey(expression_PixelSelector.name))
		{
		Type temp=hmap.get(expression_PixelSelector.name).ntype;
		if(temp==Type.IMAGE)
			expression_PixelSelector.ntype=Type.INTEGER;
		else if(expression_PixelSelector.index==null)
		{
			expression_PixelSelector.ntype=temp;
		}
		
		if(expression_PixelSelector.ntype!=Type.NONE)
		{
			;
		}
		else
			throw new SemanticException(expression_PixelSelector.firstToken,"invalid type");
		}
		else
			throw new SemanticException(expression_PixelSelector.firstToken,"variable not declared");
		return null;
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		expression_Conditional.condition.visit(this, arg);
		if(expression_Conditional.falseExpression!=null)
		{
			expression_Conditional.falseExpression.visit(this, arg);
		}
		if(expression_Conditional.trueExpression!=null)
		{
			expression_Conditional.trueExpression.visit(this, arg);
		}
		expression_Conditional.ntype=expression_Conditional.trueExpression.ntype;
		if(expression_Conditional.condition.ntype==Type.BOOLEAN && 
				expression_Conditional.trueExpression.ntype==expression_Conditional.falseExpression.ntype)
		{
			;
		}
		else
		{
			throw new SemanticException(expression_Conditional.firstToken,"invalid expression");
		}
		
		return null;
	
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(!hmap.containsKey(declaration_Image.name))
		{
			
			if(declaration_Image.xSize!=null)
			{
				declaration_Image.xSize.visit(this, arg);
			}
			if(declaration_Image.ySize!=null)
			{
				declaration_Image.ySize.visit(this, arg);
			}
			if(declaration_Image.source!=null)
			{
				declaration_Image.source.visit(this, arg);
			}
			declaration_Image.ntype=Type.IMAGE;
			hmap.put(declaration_Image.name, declaration_Image);
			if(declaration_Image.xSize!=null)
			{
				if(declaration_Image.ySize!=null && 
						declaration_Image.xSize.ntype==Type.INTEGER && 
						declaration_Image.ySize.ntype==Type.INTEGER)
				{	
					;
				}
				else
				{
					throw new SemanticException(declaration_Image.firstToken, "invalid type");
				}
			}
		}
		else
		{
			throw new SemanticException(declaration_Image.firstToken, "invalid type");
		}
		
		return declaration_Image.name;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		if(isValidUrl(source_StringLiteral.fileOrUrl))
		{
			source_StringLiteral.ntype=Type.URL;
		}
		else
		{
			source_StringLiteral.ntype=Type.FILE;
		}
		return null;
		
	}

	public boolean isValidUrl(String url)
	{
		try
		{
			new java.net.URL(url);
		}
		catch(MalformedURLException e)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		source_CommandLineParam.paramNum.visit(this, arg);
		//source_CommandLineParam.ntype=source_CommandLineParam.paramNum.ntype;
		source_CommandLineParam.ntype=null;
		if(source_CommandLineParam.paramNum.ntype==Type.INTEGER)
		{
			;
		}
		else
			throw new SemanticException(source_CommandLineParam.firstToken,"invalid source");
		return null;
		
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		if(hmap.containsKey(source_Ident.name))
		{
		
		source_Ident.ntype=hmap.get(source_Ident.name).ntype;
		if(source_Ident.ntype==Type.FILE || source_Ident.ntype==Type.URL)
		{
			;
		}
		else
			throw new SemanticException(source_Ident.firstToken,"invalid source identifier");
		}
		else
			throw new SemanticException(source_Ident.firstToken,"invalid source identifier");
		return null;
		
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		if(!hmap.containsKey(declaration_SourceSink.name))
		{
			declaration_SourceSink.source.visit(this, arg);
			declaration_SourceSink.ntype=declaration_SourceSink.getType(declaration_SourceSink.type);
			hmap.put(declaration_SourceSink.name, declaration_SourceSink);
			if(declaration_SourceSink.source.ntype==declaration_SourceSink.ntype || declaration_SourceSink.source.ntype==null)
			{
				;
			
			}
			else
			{
				throw new SemanticException(declaration_SourceSink.firstToken, "invalid type");
			}
		}
		else
		{
			throw new SemanticException(declaration_SourceSink.firstToken, "invalid type");
		}
		return declaration_SourceSink.name;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		expression_IntLit.ntype=Type.INTEGER;
		return null;
		
		
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(expression_FunctionAppWithExprArg.arg!=null)
		{
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		}
		if(expression_FunctionAppWithExprArg.arg.ntype==Type.INTEGER)
		{
			expression_FunctionAppWithExprArg.ntype=Type.INTEGER;
		}
		else
		{
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken,"invalid expression");
			
		}
		return null;
		
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(expression_FunctionAppWithIndexArg.arg!=null)
		{
		expression_FunctionAppWithIndexArg.arg.visit(this, arg);
		}
		expression_FunctionAppWithIndexArg.ntype=Type.INTEGER;
		
	    return null;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		expression_PredefinedName.ntype=Type.INTEGER;
		
		return null;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		statement_Out.sink.visit(this, arg);
		if(hmap.containsKey(statement_Out.name))
		{
		statement_Out.setDec((Declaration)hmap.get(statement_Out.name));
		if(hmap.get(statement_Out.name)!=null && 
				((hmap.get(statement_Out.name).ntype==Type.INTEGER || hmap.get(statement_Out.name).ntype==Type.BOOLEAN)
				&& statement_Out.sink.ntype==Type.SCREEN) ||
				(hmap.get(statement_Out.name).ntype==Type.IMAGE && 
				(statement_Out.sink.ntype==Type.FILE || statement_Out.sink.ntype==Type.SCREEN)))
		{
			;
		}
		else
		{
			throw new SemanticException(statement_Out.firstToken,"invalid type");
		}
		}
		else
		{
			throw new SemanticException(statement_Out.firstToken,"invalid type");
		}
		return statement_Out.name;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		statement_In.source.visit(this, arg);
		if(hmap.containsKey(statement_In.name))
		{
		statement_In.setDec((Declaration)hmap.get(statement_In.name));
		
		}
		else
		{
			throw new SemanticException(statement_In.firstToken,"invalid type");
		}
		return statement_In.name;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(statement_Assign.lhs!=null)
		{
		statement_Assign.lhs.visit(this, arg);
		}
		if(statement_Assign.e!=null)
		{
		statement_Assign.e.visit(this, arg);
		}
		if(statement_Assign.lhs.ntype==statement_Assign.e.ntype)
		{
			statement_Assign.setCartesian(statement_Assign.lhs.isCartesian());
		}
		
		else
		{
			if(statement_Assign.lhs.ntype==Type.IMAGE && statement_Assign.e.ntype==Type.INTEGER)
			{
				;
			}
			else
			{
			throw new SemanticException(statement_Assign.firstToken,"invalid type");
			}
		}
		return null;
		
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		if(hmap.containsKey(lhs.name))
		{
		lhs.ntype=hmap.get(lhs.name).ntype;
		if(lhs.index!=null)
		{
			lhs.index.visit(this, arg);
			lhs.setCartesian(lhs.index.isCartesian());
		}
		}
		else
			throw new SemanticException(lhs.firstToken,"invalid lhs");
		
		return null;
		
	}

	
	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		sink_SCREEN.ntype=Type.SCREEN;
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		if(hmap.containsKey(sink_Ident.name))
		{
			sink_Ident.ntype=hmap.get(sink_Ident.name).ntype;
			if(sink_Ident.ntype==Type.FILE)
			{
				;
			}
		}
		else
			throw new SemanticException(sink_Ident.firstToken,"invalid type");
		return null;
		
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		expression_BooleanLit.ntype=Type.BOOLEAN;
		
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(hmap.containsKey(expression_Ident.name))
		{
		expression_Ident.ntype=hmap.get(expression_Ident.name).ntype;
		}
		else
		{
			throw new SemanticException(expression_Ident.firstToken,"invalid type");
		}
		return null;
		
	}

}
