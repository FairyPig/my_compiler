package AFan_compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Pcode {
	public static String[] pcode = new String[]{
		"LIT",   //ȡ����a�ŵ�����ջջ��
		"OPR",   //ִ������,a��ʾִ�к�������
		"LOD",   //ȡ����(��Ե�ַΪa,��β�Ϊ1)�ŵ�����ջջ��
		"STO",   //������ջջ�����ݴ������(��Ե�ַΪa,��β�Ϊ1)
		"CAL",   //���ù���(���ָ���ַΪa����β�Ϊ1)
		"INT",   //����ջջָ������a
		"JMP",   //������ת�Ƶ�a
		"JPC",   //����ת�Ƶ�ָ���ַa
		"RED",   //�����ݲ��������(��Ե�ַa����β�Ϊ1)
		"WRT"    //��ջ���������
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
	
	//�����룬����
	public int f;
	//��������̱����õķֳ�����˵���ñ�����ֳ���֮��Ĳ�β�
	public int l;
	//����ע����ʾ����ͬ������a���岻ͬ
	public int a;
	//��ʼ��P_code
	public Pcode(int f,int l,int a){
		this.f = f;
		this.l = l;
		this.a = a;
	}
}
