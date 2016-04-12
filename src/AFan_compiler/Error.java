package AFan_compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class Error {
	public Vector errs = new Vector();
	public static String[] errors = new String[]{
		 "",
	     "1.应是=而不是:=",
	     "2.=后应为数",
	     "3.标识符后应为=",
	     "4.const,var,procedure 后应为标识符",
	     "5.漏掉逗号或分号",
	     "6.过程说明后的符号不正确",
	     "7.应为语句",
	     "8.程序体内语句后的符号不正确",
	     "9.应为句号",
	     "10.语句之间漏分号",
	     "11.标识符未说明",
	     "12.不可向常量或过程名赋值",
	     "13.应为赋值运算符:=",
	     "14.call后应为标识符",
	     "15.不可调用常量或变量",
	     "16.应为then",
	     "17.应为分号或end",
	     "18.应为do",
	     "19.语句后的符号不正确",
	     "20.应为关系运算符",
	     "21.表达式内不可有过程标识符",
	     "22.漏右括号",
	     "23.因子后不可为此符号",
	     "24.表达式不能以此符号开始",
	     "25.这个数太大",
	     "26.应为因子",
	     "27.标识符已存在",
	     "28.缺少until",
	     "29.缺少赋值:=符号",
	     "30.不是有效的标识符",    
	     "31.",
	     "32.",
	     "33.",
	     "34.缺少左括号",
	};

	public void rep_errors(int linenum,int errid){
		String errinfo = "第" + linenum + "行：    " + errors[errid];
		errs.addElement(errinfo);
	}
	
	public void print_err(){
		for(int i=0;i<errs.size();i++){
			System.out.println(errs.elementAt(i));
		}
	}
	
	public void cout(String name){
		try{
			String fileadd = name;
			File file = new File(fileadd);
			if(!file.exists()){
				file.createNewFile();
				System.out.println("文件不存在");
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String msg = null;
			for(int i=0;i<errs.size();i++){
				msg = (String) errs.elementAt(i) + "\r\n";
				bw.write(msg);
				
			}
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
