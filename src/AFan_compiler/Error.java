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
	     "1.Ӧ��=������:=",
	     "2.=��ӦΪ��",
	     "3.��ʶ����ӦΪ=",
	     "4.const,var,procedure ��ӦΪ��ʶ��",
	     "5.©�����Ż�ֺ�",
	     "6.����˵����ķ��Ų���ȷ",
	     "7.ӦΪ���",
	     "8.������������ķ��Ų���ȷ",
	     "9.ӦΪ���",
	     "10.���֮��©�ֺ�",
	     "11.��ʶ��δ˵��",
	     "12.�����������������ֵ",
	     "13.ӦΪ��ֵ�����:=",
	     "14.call��ӦΪ��ʶ��",
	     "15.���ɵ��ó��������",
	     "16.ӦΪthen",
	     "17.ӦΪ�ֺŻ�end",
	     "18.ӦΪdo",
	     "19.����ķ��Ų���ȷ",
	     "20.ӦΪ��ϵ�����",
	     "21.���ʽ�ڲ����й��̱�ʶ��",
	     "22.©������",
	     "23.���Ӻ󲻿�Ϊ�˷���",
	     "24.���ʽ�����Դ˷��ſ�ʼ",
	     "25.�����̫��",
	     "26.ӦΪ����",
	     "27.��ʶ���Ѵ���",
	     "28.ȱ��until",
	     "29.ȱ�ٸ�ֵ:=����",
	     "30.������Ч�ı�ʶ��",    
	     "31.",
	     "32.",
	     "33.",
	     "34.ȱ��������",
	};

	public void rep_errors(int linenum,int errid){
		String errinfo = "��" + linenum + "�У�    " + errors[errid];
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
				System.out.println("�ļ�������");
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
