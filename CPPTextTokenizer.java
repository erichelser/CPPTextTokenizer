import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.lang.*;

public class CPPTextTokenizer //extends JApplet implements MouseListener, ActionListener
{
	private static String[] args;
	public CPPTextTokenizer(){}
	public static void main(String[] a) { args=a; (new CPPTextTokenizer()).init(); }

	public void init()
	{
		CIFileReader f=new CIFileReader();
		f.setTargetFile(args[0]);
		f.readFile();

		Tokenizer t=new Tokenizer();
		t.setText(f.getFileData());
		t.tokenize();
		ArrayList<Token> a=t.getTokenList();
		for(int i=0; i<a.size(); i++)
			System.out.println(a.get(i).getTokenType()+"\t"+a.get(i).getData());
	}
}

class CIFileReader
{
	protected String targetFile;
	protected String fileData;
	protected int status;
	public CIFileReader()
	{
		targetFile="";
		fileData="";
		status=0;
	}
	public void setTargetFile(String f)
	{
		targetFile=f;
	}
	public void readFile()
	{
		System.out.println("READING: "+targetFile);
		try
		{
			BufferedReader reader=java.security.AccessController.doPrivileged(
				new java.security.PrivilegedAction<BufferedReader>() {
					public BufferedReader run() {
						try{
						return new BufferedReader(new FileReader(targetFile));
						}
						catch(FileNotFoundException e){
						System.out.println("FNFE");
						}
						return null;
					}
				}
				);
			fileData="";
			String line="";
			StringBuilder sb=new StringBuilder("");
			while((line=reader.readLine())!=null)
			{
				int comment=line.indexOf("//");
				if(comment!=-1)
					line=line.substring(0,comment);
				sb.append(line);
				sb.append(" ");
			}
			fileData=sb.toString();
			status=1;
			reader.close();
		}
		catch(IOException e)
		{
			status=2;
		}
		System.out.println("READ("+status+"): "+fileData.length()+" BYTES");
	}
	public String getFileData()
	{
		return fileData;
	}
	public int getStatus()
	{
		return status;
	}
}

class Tokenizer
{
	private String incomingText;
	private ArrayList <Token> tokenList;
	public Tokenizer()
	{
		incomingText="";
	}
	public void setText(String x)
	{
		incomingText=x;
	}
	public void tokenize()
	{
		tokenList=new ArrayList<Token>(0);

		String[] a=incomingText.split("\\s");
		for(String x:a)
			if(x.length()>0)
				tokenList.add(new Token(x,"N/A"));

		//Next, identify special characters and operators.

		breakUpTokens("\"","QUOTE");
		breakUpTokens("\'","TICK");
		breakUpTokens("&","AND");
		breakUpTokens("|","OR");
		breakUpTokens("*","STAR");
		breakUpTokens("/","SLASH");
		breakUpTokens("-","DASH");
		breakUpTokens("+","PLUS");
		breakUpTokens("(","OPEN PREN");
		breakUpTokens(")","CLOSE PREN");
		breakUpTokens("[","OPEN BRACKET");
		breakUpTokens("]","CLOSE BRACKET");
		breakUpTokens("{","OPEN BRACE");
		breakUpTokens("}","CLOSE BRACE");
		breakUpTokens(";","SEMICOLON");
		breakUpTokens(":","COLON");
		breakUpTokens(",","COMMA");
		breakUpTokens(".","DOT");
		breakUpTokens("~","TILDE");
		breakUpTokens("^","CARET");
		breakUpTokens("<","LEFT");
		breakUpTokens(">","RIGHT");
		breakUpTokens("!","BANG");
		breakUpTokens("=","EQUALS");
		breakUpTokens("%","PERCENT");
		breakUpTokens("\\","BACKSLASH");

		joinTokens("COLON","COLON","DOUBLE COLON");

		joinTokens("DASH","DASH","DECREMENT");
		joinTokens("PLUS","PLUS","INCREMENT");

		joinTokens("SLASH","STAR","BEGIN BLOCK COMMENT");
		joinTokens("STAR","SLASH","END BLOCK COMMENT");

		joinTokens("BACKSLASH","BACKSLASH","LITERAL BACKSLASH");
		joinTokens("BACKSLASH","TICK","LITERAL TICK");
		joinTokens("BACKSLASH","QUOTE","LITERAL QUOTE");

		joinTokens("LEFT","LEFT","LEFT LEFT");
		joinTokens("RIGHT","RIGHT","RIGHT RIGHT");
		joinTokens("DASH","RIGHT","ARROW");

		joinTokens("BANG","EQUALS","LOGICAL NE");
		joinTokens("EQUALS","EQUALS","LOGICAL EQ");
		joinTokens("LEFT","EQUALS","LOGICAL LE");
		joinTokens("RIGHT","EQUALS","LOGICAL GE");

		joinTokens("AND","AND","LOGICAL AND");
		joinTokens("OR","OR","LOGICAL OR");
		joinTokens("BANG","EQUALS","BANG EQUALS");

		joinTokens("PLUS","EQUALS","PLUS EQUALS");
		joinTokens("DASH","EQUALS","DASH EQUALS");
		joinTokens("STAR","EQUALS","STAR EQUALS");
		joinTokens("SLASH","EQUALS","SLASH EQUALS");
		joinTokens("PERCENT","EQUALS","PERCENT EQUALS");
		joinTokens("AND","EQUALS","AND EQUALS");
		joinTokens("OR","EQUALS","OR EQUALS");
		joinTokens("CARET","EQUALS","CARET EQUALS");

		joinTokens("LEFT LEFT","EQUALS","LEFT LEFT EQUALS");
		joinTokens("RIGHT RIGHT","EQUALS","RIGHT RIGHT EQUALS");



		identifyToken("if","KEYWORD");
		identifyToken("for","KEYWORD");
		identifyToken("while","KEYWORD");
		identifyToken("do","KEYWORD");
		identifyToken("continue","KEYWORD");
		identifyToken("break","KEYWORD");
		identifyToken("template","KEYWORD");
		identifyToken("const","KEYWORD");
		identifyToken("return","KEYWORD");
		identifyToken("this","KEYWORD");
		identifyToken("operator","KEYWORD");

		identifyToken("struct","KEYWORD");
		identifyToken("class","KEYWORD");
		identifyToken("namespace","KEYWORD");
		identifyToken("new","KEYWORD");
		identifyToken("delete","KEYWORD");
		identifyToken("using","KEYWORD");
		identifyToken("sizeof","KEYWORD");
		identifyToken("reinterpret_cast","KEYWORD");
		identifyToken("static_cast","KEYWORD");
		identifyToken("dynamic_cast","KEYWORD");
		identifyToken("const_cast","KEYWORD");

		identifyToken("typedef","KEYWORD");
		identifyToken("friend","KEYWORD");
		identifyToken("extern","KEYWORD");
		identifyToken("enum","KEYWORD");

		identifyToken("#define","KEYWORD");
		identifyToken("#endif","KEYWORD");
		identifyToken("#ifdef","KEYWORD");
		identifyToken("#ifndef","KEYWORD");
		identifyToken("#elif","KEYWORD");
		identifyToken("#else","KEYWORD");
		identifyToken("#error","KEYWORD");
		identifyToken("#pragma","KEYWORD");
		identifyToken("#undef","KEYWORD");
		identifyToken("#include","KEYWORD");

		identifyToken("bool","PRIM DATA TYPE");
		identifyToken("void","PRIM DATA TYPE");
		identifyToken("double","PRIM DATA TYPE");
		identifyToken("float","PRIM DATA TYPE");
		identifyToken("char","PRIM DATA TYPE");
		identifyToken("int","PRIM DATA TYPE");

		identifyToken("signed","PRIM DATA TYPE");
		identifyToken("unsigned","PRIM DATA TYPE");
		identifyToken("short","PRIM DATA TYPE");
		identifyToken("long","PRIM DATA TYPE");


		identifyToken("true","VALUE LITERAL BOOLEAN");
		identifyToken("false","VALUE LITERAL BOOLEAN");

		joinLiteralStrings();
		joinBlockComments();

		//for(int i=0; i<tokenList.size(); i++)
		//	System.out.println(tokenList.get(i).getTokenType()+" ["+tokenList.get(i).getData()+"]");
	}
	private void breakUpTokens(String target, String targetType)
	{
		for(int i=0; i<tokenList.size(); i++)
		{
			if(!tokenList.get(i).getTokenType().equals("N/A")) continue;

			String item=tokenList.get(i).getData();
			int targetIndex=item.indexOf(target);
			if(targetIndex!=-1)
			{
				if(targetIndex>0)
				{
					tokenList.get(i).setData(item.substring(0,targetIndex));
					tokenList.add(i+1,new Token(item.substring(targetIndex,targetIndex+target.length()),targetType));

					if(item.substring(targetIndex+target.length()).length()>0)
						tokenList.add(i+2,new Token(item.substring(targetIndex+target.length()),"N/A"));
					i++;
				}
				else
				{
					tokenList.get(i).setData(item.substring(0,target.length()));
					tokenList.get(i).setTokenType(targetType);
					if(item.substring(target.length()).length()>0)
						tokenList.add(i+1,new Token(item.substring(target.length()),"N/A"));
				}
			}
		}
	}
	private void joinTokens(String targetType1, String targetType2, String resultTargetType)
	{
		for(int i=0; i<tokenList.size()-1; i++)
		{
			if(   tokenList.get(i  ).getTokenType().equals(targetType1)
			   && tokenList.get(i+1).getTokenType().equals(targetType2))
			{
				tokenList.get(i).setData( tokenList.get(i).getData()
				                          + tokenList.get(i+1).getData());
				tokenList.get(i).setTokenType(resultTargetType);
				tokenList.remove(i+1);
			}
		}
	}
	private void identifyToken(String target, String type)
	{
		for(int i=0; i<tokenList.size(); i++)
		{
			if(tokenList.get(i).getData().equals(target))
			{
				tokenList.get(i).setTokenType(type);
			}
		}
	}
	private void joinBlockComments()
	{
		boolean repeatLoop;
		do
		{
			repeatLoop=false;
			for(int i=0; i<tokenList.size()-1; i++)
			{
				if(   tokenList.get(i).getTokenType().equals("BEGIN BLOCK COMMENT"))
				{
					tokenList.get(i).setData(  tokenList.get(i  ).getData()
					                         + tokenList.get(i+1).getData());

					if(tokenList.get(i+1).getTokenType().equals("END BLOCK COMMENT"))
						tokenList.get(i).setTokenType("BLOCK COMMENT");

					tokenList.remove(i+1);

					repeatLoop=true;
					i=tokenList.size();
				}
			}
		}while(repeatLoop);
	}
	private void joinLiteralStrings()
	{
		boolean repeatLoop;
		do
		{
			repeatLoop=false;
			for(int i=0; i<tokenList.size()-1; i++)
			{
				if(   tokenList.get(i).getTokenType().equals("QUOTE")
				   || tokenList.get(i).getTokenType().equals("PARTIAL STRING"))
				{
					tokenList.get(i).setData(  tokenList.get(i  ).getData()
					                         + tokenList.get(i+1).getData());

					if(tokenList.get(i+1).getTokenType().equals("QUOTE"))
						tokenList.get(i).setTokenType("VALUE LITERAL STRING");
					else
						tokenList.get(i).setTokenType("PARTIAL STRING");

					tokenList.remove(i+1);

					repeatLoop=true;
					i=tokenList.size();
				}
			}
		}while(repeatLoop);
	}
	public ArrayList<Token> getTokenList()
	{
		return tokenList;
	}
}

class Token
{
	private String data;
	private String tokenType;
	public Token(String _data, String _tokenType)
	{
		setData(_data);
		setTokenType(_tokenType);
	}
	public void setData(String _data) { data=_data; }
	public void setTokenType(String _tokenType) { tokenType=_tokenType; }
	public String getData() { return data; }
	public String getTokenType() { return tokenType; }
}
