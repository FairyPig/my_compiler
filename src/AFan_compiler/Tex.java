package AFan_compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


public class Tex {
	private BufferedReader in;
	public int[] ssym;
	//目前读到第几行
	public int lineCnt=0;
	//目前读到的字符为..
	private char curCh = ' ';
	//这一行的所有字符
	private String line;
	//这一行的字符长度
	public int lineLength = 0;
	//已经读到该行的第..个
	public int chCount = 0;
	
	public Tex(String filepath){
		try{
			in = new BufferedReader(new FileReader(filepath));
		}catch(Exception e){
			System.out.println("----文件未找到----");
		}
		//设置单字符
		ssym = new int[256];
		Arrays.fill(ssym, Symbol.nul);
		ssym['+'] = Symbol.plus;
		ssym['-'] = Symbol.minus;
		ssym['*'] = Symbol.times;
		ssym['/'] = Symbol.slash;
		ssym['('] = Symbol.lparen;
		ssym[')'] = Symbol.rparen;
		ssym['='] = Symbol.eql;
		ssym[','] = Symbol.comma;
		ssym['.'] = Symbol.period;
		ssym[';'] = Symbol.semicolon;
	}
	
	public boolean getch(){
		if(chCount == lineLength){
			try{
				line = in.readLine();
				lineCnt++;
			}catch(IOException e){
				e.printStackTrace();
			}
			if(line != null){
				lineLength = line.length();
				chCount = 0;
				curCh = line.charAt(chCount);
				chCount ++;
				return true;
			}else{
				return false;
			}
		}else{
			curCh = line.charAt(chCount++);
			return true;
		}
	}
	
	public Symbol getsym(){
		Symbol sym;
		while(curCh == ' '){
			if(getch());
		}
		if((curCh >= 'a' && curCh <= 'z')||(curCh >= 'A' && curCh <= 'Z')){
			sym = matchKeyword();
		}else if(curCh >= '0' && curCh <= '9'){
			sym = matchNumber();
		}else{
			sym = matchOperator();
		}
		return sym;
	}

	private Symbol matchKeyword(){
		StringBuffer sb = new StringBuffer();
		do{
			sb.append(curCh);
			getch();
		}while((curCh >= 'a' && curCh <= 'z')||(curCh >= 'A' && curCh <= 'Z')||(curCh >='0' && curCh <= '9'));
		
		String token = sb.toString();
		int index = Symbol.getWordId(token);
		Symbol sym = null;
		if(index < 0){
			//为标识符
			sym = new Symbol(0);
			sym.name = token;
		}else{
			sym = new Symbol(index);
		}
		return sym;
	}
	
	private Symbol matchNumber(){
		Symbol sym = new Symbol(Symbol.number);
		do{
			sym.num = 10 * sym.num + curCh - '0';
			getch();
		}while(curCh >= '0' && curCh <= '9');
		
		return sym;
	}
	
	private Symbol matchOperator(){
		Symbol sym = null;
		switch(curCh){
			case ':':
				getch();
				if(curCh == '='){
					sym = new Symbol(Symbol.becomes);
					getch();
				}else{
					sym = new Symbol(Symbol.nul);
				}
				break;
			case '<':
				getch();
				if(curCh == '='){
					sym = new Symbol(Symbol.lessequal);
					getch();
				}else if(curCh == '>'){
					sym = new Symbol(Symbol.uneql);
					getch();
				}else{
					sym = new Symbol(Symbol.lss);
				}
				break;
			case '>':
				getch();
				if(curCh == '='){
					sym = new Symbol(Symbol.greaterequal);
					getch();
				}else{
					sym = new Symbol(Symbol.gtr);
				}
				break;
			default:
				sym = new Symbol(ssym[curCh]);
				if(sym.symID != Symbol.period){
					getch();
				}
		}
		return sym;
	}
}
