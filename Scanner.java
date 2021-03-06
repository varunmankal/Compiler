/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
  */

package cop5556fa17;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanner {
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		
		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
		
		public int getPos() { return pos; }

	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, 
		KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, 
		KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, 
		KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, 
		KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, 
		KW_image/* image */,  KW_int/* int */, 
		KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, 
		OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, 
		OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, 
		OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */, RPAREN/* ) */, 
		LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
	}
	List<String> list;
	
	//list.add("x");
	//list.add("X");

	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); //for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); //for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/** 
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.  
	 */
	static final char EOFchar = 0;
	
	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;
	
	/**
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  



	
	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		SymbolTable.symTable=null;
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
		list=new ArrayList<>();
		list.add("x");
		list.add("X");
		list.add("y");
		list.add("Y");
		list.add("r");
		list.add("R");
		list.add("a");
		list.add("A");
		list.add("Z");
		list.add("DEF_X");
		list.add("DEF_Y");
		list.add("SCREEN");
		list.add("cart_x");
		list.add("cart_y");
		list.add("polar_a");
		list.add("polar_r");
		list.add("abs");
		list.add("sin");
		list.add("cos");
		list.add("atan");
		list.add("log");
		list.add("image");
		list.add("int");
		list.add("boolean");
		list.add("url");
		list.add("file");
		list.add("true");
		list.add("false");
		
	}


	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	
//////////////////////////////////////////////////////	
	public Scanner scan() throws LexicalException {
		/* TODO  Replace this with a correct and complete implementation!!! */
		char ch;
		int line_num=1,pos=0,pos_line=1;
		//System.out.println("ans is"+Character.isLetterOrDigit('@'));
		/*for(int i=0;i<chars.length;i++)
		{
			System.out.println("char at "+i+" is "+chars[i]);
		}*/
		//System.out.println("length is "+ chars.length);
	
		for(int i=0;i<chars.length;i++)
		{
			ch=chars[i];
			//System.out.println("hello");
			if(Character.isLetter(ch)||ch=='$'||ch=='_')
			{
				StringBuilder sb=new StringBuilder();
				//int index=pos;
				while(chars[i]!=EOFchar)
				{
					if(Character.isLetterOrDigit(chars[i])||chars[i]=='$'||chars[i]=='_')
					{
						sb.append(chars[i]);i++;//pos++;
					//	pos++;
						//System.out.println("world");
					}
					else
					{
						break;
					}
				}	
					
					
						String keyword=sb.toString();
						if(list.contains(keyword))
						{
							if(keyword.equals("x"))
								tokens.add(new Token(Kind.KW_x,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("X"))
								tokens.add(new Token(Kind.KW_X,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("y"))
								tokens.add(new Token(Kind.KW_y,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("Y"))
								tokens.add(new Token(Kind.KW_Y,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("R"))
								tokens.add(new Token(Kind.KW_R,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("r"))
								tokens.add(new Token(Kind.KW_r,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("a"))
								tokens.add(new Token(Kind.KW_a,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("A"))
								tokens.add(new Token(Kind.KW_A,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("Z"))
								tokens.add(new Token(Kind.KW_Z,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("DEF_X"))
							{
								tokens.add(new Token(Kind.KW_DEF_X,pos,keyword.length(),line_num,pos_line));
							}
							else if(keyword.equals("DEF_Y"))
								tokens.add(new Token(Kind.KW_DEF_Y,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("SCREEN"))
								tokens.add(new Token(Kind.KW_SCREEN,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("cart_x"))
								tokens.add(new Token(Kind.KW_cart_x,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("cart_y"))
								tokens.add(new Token(Kind.KW_cart_y,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("polar_a"))
								tokens.add(new Token(Kind.KW_polar_a,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("polar_r"))
								tokens.add(new Token(Kind.KW_polar_r,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("abs"))
								tokens.add(new Token(Kind.KW_abs,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("sin"))
								tokens.add(new Token(Kind.KW_sin,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("cos"))
								tokens.add(new Token(Kind.KW_cos,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("atan"))
								tokens.add(new Token(Kind.KW_atan,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("log"))
								tokens.add(new Token(Kind.KW_log,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("image"))
								tokens.add(new Token(Kind.KW_image,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("int"))
								tokens.add(new Token(Kind.KW_int,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("boolean"))
								tokens.add(new Token(Kind.KW_boolean,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("url"))
								tokens.add(new Token(Kind.KW_url,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("file"))
								tokens.add(new Token(Kind.KW_file,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("true"))
								tokens.add(new Token(Kind.BOOLEAN_LITERAL,pos,keyword.length(),line_num,pos_line));
							else if(keyword.equals("false"))
								tokens.add(new Token(Kind.BOOLEAN_LITERAL,pos,keyword.length(),line_num,pos_line));
							pos_line+=keyword.length();
							pos+=keyword.length();
						}
						else
						{
							// identifier 
							tokens.add(new Token(Kind.IDENTIFIER,pos,keyword.length(),line_num,pos_line));
							pos_line+=keyword.length();
							
							pos+=keyword.length();
						}
						i--;
						
					
					
				}
							
			
			else if(chars[i]==' '||chars[i]=='\t'||chars[i]=='\f')
			{
				pos++;
				pos_line++;
			}
			else if(chars[i]=='\n')
			{
				line_num++;
				pos_line=1;
				pos++;
			}
			else if(chars[i]=='\r')
			{
				if(i+1<chars.length&&chars[i+1]=='\n')
				{
					pos++;
					i++;
					
				}
				
					line_num++;
					pos_line=1;
					pos++;
				
				
			}
			
			
			else if(i+1<chars.length&&chars[i]=='/' && chars[i+1]=='/')
			{	
				i+=2;
				pos+=2;
				pos_line+=2;
				//System.out.println("comment");
				while(true)
				{
					if(chars[i]=='\n'||chars[i]=='\r'|| chars[i]==EOFchar)
					{
						//line_num++;
						//pos_line=1;
						//pos+=1;
						//i+=1;
						
						i--;
						break;
					}
					else
					{
						pos++;
						pos_line++;
						i++;
					}
				}
			}
			else if(Character.isDigit(chars[i]))
			{
				
				if(chars[i]=='0')
				{
					
					tokens.add(new Token(Kind.INTEGER_LITERAL,pos,1,line_num,pos_line++));
					pos++;
					if(Character.isDigit(chars[i+1]))
					{
						//throw exception
					}
					else if(chars[i+1]=='0')
					{
					//	tokens.add();
					}
					
				}
				else
				{
				StringBuilder sb=new StringBuilder();
								//System.out.println("digit");
				while(i<chars.length)
				{
					if(Character.isDigit(chars[i]))
					{
						sb.append(chars[i]);
						i++;
					}
					else
					{
						try
						{
							int number=Integer.parseInt(sb.toString());
							
						}
						catch(NumberFormatException e)
						
						{
							throw new LexicalException("Integer value out of bound", pos);
						}
						tokens.add(new Token(Kind.INTEGER_LITERAL,pos,sb.length(),line_num,pos_line));
						pos_line+=sb.length();
						pos+=sb.length();
						i--;
						break;
					}
				}
				
				}
			}
			
			else if(chars[i]=='>'||chars[i]=='<'||chars[i]=='!'||chars[i]=='?'||chars[i]==':'||chars[i]=='='||chars[i]=='!'||
					chars[i]=='&'||chars[i]=='|'||chars[i]=='+'||chars[i]=='-'||chars[i]=='*'||chars[i]=='/'||
					chars[i]=='%'||chars[i]=='@')
			{
				boolean flag=true;
				
				if((i+1)<chars.length)
				{	
						if((chars[i]=='='&&chars[i+1]=='='))
							{
								tokens.add(new Token(Kind.OP_EQ,pos,2,line_num,pos_line));flag=false;i++;pos+=2;pos_line+=2;
							}
						
						else if((chars[i]=='!'&&chars[i+1]=='='))
						{
							tokens.add(new Token(Kind.OP_NEQ,pos,2,line_num,pos_line));flag=false;i++;pos_line+=2;pos+=2;
							}
						else if((chars[i]=='<'&&chars[i+1]=='='))
							{
							tokens.add(new Token(Kind.OP_LE,pos,2,line_num,pos_line));flag=false;i++;pos_line+=2;pos+=2;
							}
						else if(chars[i]=='>'&&chars[i+1]=='=')
						{
							tokens.add(new Token(Kind.OP_GE,pos,2,line_num,pos_line));flag=false;i++;pos_line+=2;pos+=2;
							}
				
						else if(chars[i]=='-'&&chars[i+1]=='>')
							{
							tokens.add(new Token(Kind.OP_RARROW,pos,2,line_num,pos_line));flag=false;i++;pos_line+=2;pos+=2;
							}
				
						else if(chars[i]=='<'&&chars[i+1]=='-')
						{
							tokens.add(new Token(Kind.OP_LARROW,pos,2,line_num,pos_line));flag=false;i++;pos_line+=2;pos+=2;
							}
						else if(chars[i]=='*'&&chars[i+1]=='*')
						{
							tokens.add(new Token(Kind.OP_POWER,pos,2,line_num,pos_line));flag=false;i++;pos_line+=2;pos+=2;
							}
						
				}
				if(flag)
				{
					if(chars[i]=='>')
						tokens.add(new Token(Kind.OP_GT,pos++,1,line_num,pos_line++));
					else if(chars[i]=='<')
						tokens.add(new Token(Kind.OP_LT,pos++,1,line_num,pos_line++));
					else if(chars[i]=='!')
						tokens.add(new Token(Kind.OP_EXCL,pos++,1,line_num,pos_line++));
					else if(chars[i]=='?')
						tokens.add(new Token(Kind.OP_Q,pos++,1,line_num,pos_line++));
					else if(chars[i]==':')
						tokens.add(new Token(Kind.OP_COLON,pos++,1,line_num,pos_line++));
					else if(chars[i]=='&')
						tokens.add(new Token(Kind.OP_AND,pos++,1,line_num,pos_line++));
					else if(chars[i]=='|')
						tokens.add(new Token(Kind.OP_OR,pos++,1,line_num,pos_line++));
					else if(chars[i]=='+')
						tokens.add(new Token(Kind.OP_PLUS,pos++,1,line_num,pos_line++));
					else if(chars[i]=='-')
						tokens.add(new Token(Kind.OP_MINUS,pos++,1,line_num,pos_line++));
					else if(chars[i]=='*')
						tokens.add(new Token(Kind.OP_TIMES,pos++,1,line_num,pos_line++));
					else if(chars[i]=='/')
						tokens.add(new Token(Kind.OP_DIV,pos++,1,line_num,pos_line++));
					else if(chars[i]=='%')
						tokens.add(new Token(Kind.OP_MOD,pos++,1,line_num,pos_line++));
					else if(chars[i]=='@')
						tokens.add(new Token(Kind.OP_AT,pos++,1,line_num,pos_line++));
					else if(chars[i]=='=')
						tokens.add(new Token(Kind.OP_ASSIGN,pos++,1,line_num,pos_line++));
				}
				
			}
			else if(chars[i]=='('||chars[i]==')'||chars[i]=='['||chars[i]==']'||chars[i]==','||chars[i]==';')
			{
				
				if(chars[i]=='(')
					tokens.add(new Token(Kind.LPAREN,pos++,1,line_num,pos_line++));
				else if(chars[i]==')')
					tokens.add(new Token(Kind.RPAREN,pos++,1,line_num,pos_line++));
				else if(chars[i]=='[')
					tokens.add(new Token(Kind.LSQUARE,pos++,1,line_num,pos_line++));
				else if(chars[i]==']')
					tokens.add(new Token(Kind.RSQUARE,pos++,1,line_num,pos_line++));
				else if(chars[i]==',')
					tokens.add(new Token(Kind.COMMA,pos++,1,line_num,pos_line++));
				else if(chars[i]==';')
					tokens.add(new Token(Kind.SEMI,pos++,1,line_num,pos_line++));
				
			}
			
			/*else if(chars[i]=='\\')
				{
						if(chars[i]=='b'||chars[i]=='t'||chars[i]=='n'||chars[i]=='f'||chars[i]=='r'||chars[i]=='"'||
								(chars[i]=='\'')||chars[i]=='\\')
								{
						//	tokens.add(new token());
								}
				}*/
			
			// StringLiteral
			else if(chars[i]=='"')
			{
				//System.out.println("string");
				//int index=pos;
				int temp_line_num=line_num;
				i++;//pos++;
				StringBuilder sb=new StringBuilder();
				sb.append('"');
				while(i<chars.length)
				{
					if(chars[i]=='\n'||chars[i]=='\r')
					{
						throw new LexicalException("new line found in string literal", i);
					}
					
					if(chars[i]=='\\')
					{
						if((i+1<chars.length))
							
						{
							
							/*if(chars[i+1]=='n')
							{	
								line_num++;
								//sb.append("\\n");
							}*/
							//pos++;
							i++;
							switch(chars[i])
							{
							case 'n':
								sb.append("\\n");break;
							case 'r':
								sb.append("\\r");break;
							case 't':
								sb.append("\\t");break;
							case 'f':
								sb.append("\\f");break;
							case 'b':
								sb.append("\\b");break;
							case '"':
								sb.append("\\\"");break;
							case '\'':
								sb.append("\\'");break;
							case '\\':
								sb.append("\\\\");break;
							default :
								throw new LexicalException("unknown character", i-1);
							}
							
						}
					}
					else if(chars[i]=='"')
					{
						sb.append('"');
						//System.out.println("else if");
						//System.out.println("string is"+sb.toString());
						tokens.add(new Token(Kind.STRING_LITERAL,pos,sb.length(),Math.min(line_num, temp_line_num),pos_line));
						pos_line+=sb.length();
						pos+=sb.length();
						break;
					}
					else
					{
						//System.out.println("string else");
						sb.append(chars[i]);
					}
					i++;
				}
				if(i==chars.length)
				{
					//System.out.println("STring is "+sb.toString());
					throw new LexicalException("unclosed string literal", chars.length-1);
				}
			}
			else if(chars[i]=='\0'&&i<(chars.length-1))
			{
				throw new LexicalException("end",i);
			}
			else if(chars[i]==EOFchar)
			{
				tokens.add(new Token(Kind.EOF, pos, 0, line_num, pos_line));
			}
			
			else
			{
				//System.out.println("unknown character at "+i);
				throw new LexicalException("unknown character",i);
			}
		
		
		}
		
	/*	int pos = 0;
		int line = 1;
		int posInLine = 1;*/
		//tokens.add(new Token(Kind.EOF, pos, 0, line_num, pos_line));
		return this;

	}
	

//////////////////////////////////////////////////////
	/**
	 * Returns true if the internal interator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}
	
	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}
	
	
	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}
