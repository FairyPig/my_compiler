package AFan_compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Pcode {
	public static String[] pcode = new String[]{
		"LIT",   //取常量a放到数据栈栈顶
		"OPR",   //执行运算,a表示执行何种运算
		"LOD",   //取变量(相对地址为a,层次差为1)放到数据栈栈顶
		"STO",   //将数据栈栈顶内容存入变量(相对地址为a,层次差为1)
		"CAL",   //调用过程(入口指令地址为a，层次差为1)
		"INT",   //数据栈栈指针增加a
		"JMP",   //无条件转移到a
		"JPC",   //条件转移到指令地址a
		"RED",   //读数据并存入变量(相对地址a，层次差为1)
		"WRT"    //将栈顶内容输出
	};
	public static int LIT = 0;
	public static int OPR = 1;
	public static int LOD = 2;
	public static int STO = 3;
	public static int CAL = 4;
	public static int INT = 5;
	public static int JMP = 6;
	public static int JPC = 7;
	public static int RED = 8;
	public static int WRT = 9;
	
	//操作码，如上
	public int f;
	//变量或过程被引用的分程序与说明该变量或分程序之间的层次差
	public int l;
	//如上注释所示，不同操作码a含义不同
	public int a;
	//初始化P_code
	public Pcode(int f,int l,int a){
		this.f = f;
		this.l = l;
		this.a = a;
	}
}
