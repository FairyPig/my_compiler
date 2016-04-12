package AFan_compiler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.*;


public class compile{
	public static Vector pcodes = new Vector();
	
	public static void write(String s){
		pcodes.addElement(s);
	}
	
	public static void main(String[] arg){
		MyFrame mf = new MyFrame();

	}
}


class MyFrame{
	
	JFrame mainFrame;
	Container thisContainer; 
	private static final long serialVersionUID = 1L;
	private String filename = "";
	MenuBar menuBar; // 菜单栏
	Menu sMenu, hMenu; // 选项菜单,和帮助菜单
	MenuItem itemRestart, itemExit;
	Font ft1, ft2; // 字体
	JPanel centerPanel, northPanel,sorthPanel,eastPanel,p2,p3;
	JLabel welcome,hint_1,hint_2; // 欢迎界面
	JButton start, exit,submit; // 开始按钮，结束按钮
	String[] A, B, C, D, timu; // 文字常量
	JButton re; // 选择按钮和返回按钮
	JTextField tf4,result;
	
	// 初始化所有变量
	public MyFrame(){
			init();
	}
	
	public void init()
	{
		ft1 = new Font("宋体", Font.BOLD, 30);
		ft2 = new Font("宋体", Font.PLAIN, 20);
		mainFrame=new JFrame("13211031 张凡"); 

		
		thisContainer = mainFrame.getContentPane();
		
		thisContainer.setLayout(new BorderLayout());
		((JComponent) thisContainer).setBorder(BorderFactory.createEmptyBorder(100,20,20,20));
		centerPanel = new JPanel();
		thisContainer.add(centerPanel,BorderLayout.CENTER);
		
		northPanel = new JPanel();
		thisContainer.add(northPanel,BorderLayout.NORTH);
		
		sorthPanel = new JPanel();
		thisContainer.add(sorthPanel,BorderLayout.SOUTH);
		
		eastPanel = new JPanel();
		thisContainer.add(eastPanel, BorderLayout.EAST);
		p3 = new JPanel();
		//text1 = new JTextField();
		welcome = new JLabel("Welcome to my compiler");
		welcome.setFont(ft1);
		northPanel.add(welcome);
		hint_1 = new JLabel("请输入文件名(文件在当前目录下): ");
		hint_1.setFont(ft2);
		tf4 = new JTextField();
		tf4.setFont(ft2);
		centerPanel.setLayout(new GridLayout(3,1));
		centerPanel.add(hint_1);
		centerPanel.add(tf4);
		
		submit = new JButton("提交");
		exit = new JButton("退出");
		submit.setFont(ft2);
		exit.setFont(ft2);
		sorthPanel.add(submit);
		sorthPanel.add(exit);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				filename = tf4.getText();
				if(filename != null){
					String name = filename+".txt";
					File file = new File(name);
					if(!file.exists()){
						JOptionPane.showMessageDialog(thisContainer, "提示", "该文件不存在", 0);  
					}else{
						Tex s = new Tex(name);
						Error er = new Error();
						SymbolTable table = new SymbolTable();
						syntaxAnalysis sy = new syntaxAnalysis(table,er,s);
						sy.mainProgram();
						if(er.errs.isEmpty()){
							sy.cout(filename+"_rst.txt");
							JOptionPane.showMessageDialog(thisContainer, "编译成功", "已生成中间代码，请查看新生成文件！", 0);
						}else{
							er.cout(filename+"_rst.txt");
							JOptionPane.showMessageDialog(thisContainer, "编译成功", "源代码有错，请查看新生成文件！", 0);
						}
					}
				}
			}
		});
		
		mainFrame.setBounds(280,100,600,500); 
		mainFrame.setResizable(false);
		mainFrame.setVisible(true); 
	}
}
