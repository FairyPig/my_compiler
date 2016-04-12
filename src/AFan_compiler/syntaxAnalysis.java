package AFan_compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;


public class syntaxAnalysis {
	
	SymbolTable table;
	Error er;
	Tex sc;
	Symbol sym;

	final int sysnum = 36;
	//传递集合
	BitSet fsys = new BitSet(sysnum);
	//分程序
	BitSet partFirsys = new BitSet(sysnum);
	//语句
	BitSet blocFirsys = new BitSet(sysnum);
	//因子
	BitSet facFirsys = new BitSet(sysnum);
	//条件
	BitSet ifFirsys = new BitSet(sysnum);
	//项
	BitSet itemFirsys = new BitSet(sysnum);
	//表达式
	BitSet expFirsys = new BitSet(sysnum);
	int dx = 0;
	//运行栈上限
	private static final int stackSize = 1000;
	private static final int arraySize = 500;
	//虚拟机代码指针，[0,arraySize-1]
	public int arrayPtr = 0;
	public Pcode[] pcodeArray;
	
	public syntaxAnalysis(SymbolTable t,Error e,Tex s){
		table = t;
		er = e;
		sc = s;
		pcodeArray = new Pcode[arraySize];
		sym = sc.getsym();
		startFir();
	}
	
	public void gen(int f,int l,int a){
		if(arrayPtr>=arraySize){
			System.out.println("代码过长");
		}
		pcodeArray[arrayPtr++] = new Pcode(f,l,a);
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
			for(int i = 0; pcodeArray[i] != null; i++){
				msg = "" + i + "  " + Pcode.pcode[pcodeArray[i].f] + " " + pcodeArray[i].l + " " + pcodeArray[i].a + "\r\n";
				bw.write(msg);
			}
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void startFir(){
		ifFirsys.set(Symbol.oddsym);
		ifFirsys.set(Symbol.plus);
		ifFirsys.set(Symbol.minus);
		ifFirsys.set(Symbol.lparen);
		ifFirsys.set(Symbol.ident);
		ifFirsys.set(Symbol.number);
		
		itemFirsys.set(Symbol.ident);
		itemFirsys.set(Symbol.number);
		itemFirsys.set(Symbol.lparen);
		
		expFirsys.set(Symbol.plus);
		expFirsys.set(Symbol.minus);
		expFirsys.set(Symbol.lparen);
		expFirsys.set(Symbol.ident);
		expFirsys.set(Symbol.number);
		
		partFirsys.set(Symbol.constsym);
		partFirsys.set(Symbol.varsym);
		partFirsys.set(Symbol.procsym);
		partFirsys.set(Symbol.ident);
		partFirsys.set(Symbol.ifsym);
		partFirsys.set(Symbol.callsym);
		partFirsys.set(Symbol.beginsym);
		partFirsys.set(Symbol.whilesym);
		partFirsys.set(Symbol.readsym);
		partFirsys.set(Symbol.writesym);
		
		blocFirsys.set(Symbol.ident);
		blocFirsys.set(Symbol.callsym);
		blocFirsys.set(Symbol.beginsym);
		blocFirsys.set(Symbol.ifsym);
		blocFirsys.set(Symbol.whilesym);
		blocFirsys.set(Symbol.readsym);
		blocFirsys.set(Symbol.writesym);
		
		facFirsys.set(Symbol.ident);
		facFirsys.set(Symbol.number);
		facFirsys.set(Symbol.lparen);
	}
	public boolean getsym(){
		if(sym.symID != Symbol.period){
			sym = sc.getsym();
			return true;
		}
		return false;
	}
	//s1----应该是这个
	public boolean test(BitSet s1,BitSet s2,int n){
		if(!s1.get(sym.symID)){
			er.rep_errors(sc.lineCnt, n);
			BitSet s = (BitSet) s1.clone();
			s.or(s2);
			while(!s.get(sym.symID)){
				getsym();
			}
			return false;
		}else{
			return true;
		}
	}
	public void jump(BitSet s){
		while(!s.get(sym.symID)){
			getsym();
		}
	}
	public boolean searchItem(Symbol sym1){
		if(table.serachTable(sym1.name) == -1)
			return true;
		return false;
	}
	
	void mainProgram(){
		
		int lev = 0;
		
		fsys.set(Symbol.period);

		test(partFirsys,fsys,8);
		partProgram(lev,fsys);
		
		if(sym.symID != Symbol.period){
			er.rep_errors(sc.lineCnt,9);
		}else{
			System.out.println("编译成功！");
		}
	}
	
	void partProgram(int lev,BitSet fsys){
		System.out.println("******分程序lev:"+lev+"*********");
		int cx0,dx0,tx0;
		tx0 = table.itemPtr;
		dx0 = dx;
		dx = 3;
		table.getItem(table.itemPtr).addr = this.arrayPtr;         //在符号表的当前位置记录下这个jmp指令在代码段中的位置 
        this.gen(Pcode.JMP, 0, 0);
        
		if(sym.symID == Symbol.constsym){
			fsys.or(blocFirsys);
			fsys.set(Symbol.semicolon);
			constSym(lev,fsys);
		}
		if(sym.symID == Symbol.varsym){
			fsys.or(blocFirsys);
			fsys.set(Symbol.semicolon);
			varSym(lev,fsys);
		}
		if(sym.symID == Symbol.procsym){
			fsys.or(blocFirsys);
			fsys.set(Symbol.semicolon);
			proSym(lev,fsys);
		}
		
		Item item = table.getItem(tx0);
        this.pcodeArray[item.addr].a = this.arrayPtr;
        item.addr = this.arrayPtr;
        item.size = dx;        
        cx0 = this.arrayPtr;
        
        //分配内存代码，
        this.gen(Pcode.INT, 0, dx);
        
        fsys.set(Symbol.semicolon);
        fsys.set(Symbol.endsym);
        //test(blocFirsys,fsys,8);
		blockSym(lev,fsys);
		
		this.gen(Pcode.OPR, 0, 0);
		
		System.out.println("******分程序结束:"+lev+"*********");
		table.printTable();
		dx = dx0;
	    table.itemPtr = tx0;
	}
	
	void constSym(int lev,BitSet fsys){
		getsym();
		defineConst(lev,fsys);
		while(sym.symID == Symbol.comma){
			getsym();
			defineConst(lev,fsys);
		}
		if(sym.symID == sym.semicolon){
			getsym();
		}else{
			//少分号
			er.rep_errors(sc.lineCnt, 5);
		}
	}
	
	void defineConst(int lev,BitSet fsys){
		if(sym.symID == Symbol.ident){
			Symbol s = sym;
			getsym();
			if(sym.symID == Symbol.eql){
				getsym();
				if(sym.symID == Symbol.number){
					s.num = sym.num;
					s.symID = sym.symID;
					if(searchItem(s)){
						table.insertTable(s, Item.constant, 0, dx);
					}else{
						//已存在标识符
						er.rep_errors(sc.lineCnt, 27);
						BitSet s1 = new BitSet(sysnum);
						s1.set(Symbol.comma);
						s1.set(Symbol.semicolon);
						jump(s1);
					}
					getsym();
				}else{
					//后面不是数字
					er.rep_errors(sc.lineCnt, 2);
					BitSet s2 = new BitSet(sysnum);
					s2.set(Symbol.comma);
					s2.set(Symbol.semicolon);
					jump(s2);
				}
			}else{
				//不是等号
				er.rep_errors(sc.lineCnt, 3);
				BitSet s3 = new BitSet(sysnum);
				s3.set(Symbol.comma);
				s3.set(Symbol.semicolon);
				jump(s3);
			}
		}else{
			//不是标识符
			er.rep_errors(sc.lineCnt, 4);
			BitSet s4 = new BitSet(sysnum);
			s4.set(Symbol.comma);
			s4.set(Symbol.semicolon);
			jump(s4);
		}
	}
	
	void varSym(int lev,BitSet fsys){
		getsym();
		defineVar(lev,fsys);
		while(sym.symID == Symbol.comma){
			getsym();
			defineVar(lev,fsys);
		}
		if(sym.symID == sym.semicolon){
			getsym();
		}else{
			//少分号
			er.rep_errors(sc.lineCnt, 5);
		}
	}
	
	void defineVar(int lev,BitSet fsys){
		if(sym.symID == Symbol.ident){
			if(searchItem(sym)){
				table.insertTable(sym, Item.variable, lev, dx);
				dx++;
			}else{
				//标识符已存在
				er.rep_errors(sc.lineCnt, 27);
				BitSet s1 = new BitSet(sysnum);
				s1.set(Symbol.comma);
				s1.set(Symbol.semicolon);
				jump(s1);
			}
			getsym();
		}else{
			//不是标识符
			er.rep_errors(sc.lineCnt, 4);
			BitSet s1 = new BitSet(sysnum);
			s1.set(Symbol.comma);
			s1.set(Symbol.semicolon);
			jump(s1);
		}
	}
	//过程说明部分
	void proSym(int lev,BitSet fsys){
		while (sym.symID == Symbol.procsym) {                 //如果是procedure
            getsym();
            if (sym.symID == Symbol.ident) {                      //填写符号表
                table.insertTable(sym, Item.procedure, lev, dx);        //当前作用域的大小 
                getsym();
            } else {
                //procedure后应为标识符
    			er.rep_errors(sc.lineCnt, 4);
    			BitSet s1 = new BitSet(sysnum);
    			s1.set(Symbol.semicolon);
    			jump(s1);
            }
            if (sym.symID == Symbol.semicolon)               //分号，表示<过程首部>结束
            {
                getsym();
            } else {
                //漏了逗号或者分号
            	er.rep_errors(sc.lineCnt, 5);
            }
            
            fsys.or(partFirsys);
            //test(partFirsys,fsys,8);
            partProgram(lev + 1,fsys);                                  //嵌套层次+1，分析分程序

            if (sym.symID == Symbol.semicolon) {                          //<过程说明部分> 识别成功
            	getsym();    
            } else {
                //漏了逗号或者分号
            	er.rep_errors(sc.lineCnt, 5);
            }
        }
	}
	//语句
	void blockSym(int lev,BitSet fsys){
		System.out.println("--------语句开始-------"+sym.symID);
		System.out.println(sym.symID);
		//赋值语句
		if(sym.symID == Symbol.ident){
			assignin(lev,fsys);
		}
		//条件语句
		else if(sym.symID == Symbol.ifsym){
			ifSym(lev,fsys);
		}
		//当型循环语句
		else if(sym.symID == Symbol.whilesym){
			whileSym(lev,fsys);
		}
		//过程调用语句
		else if(sym.symID == Symbol.callsym){
			callSym(lev,fsys);
		}
		//读语句
		else if(sym.symID == Symbol.readsym){
			readSym(lev,fsys);
		}
		//写语句
		else if(sym.symID == Symbol.writesym){
			writeSym(lev,fsys);
		}
		//复合语句
		else if(sym.symID == Symbol.beginsym){
			beginSym(lev,fsys);
		}
		//重复语句
		else if(sym.symID == Symbol.repeatsym){
			repeatSym(lev,fsys);
		}
		else{
			//空语句
		}
		System.out.println("--------语句结束-------"+sym.symID);
	}
	/**
	 * 复合语句
	 * @param lev
	 */
	void beginSym(int lev,BitSet fsys){
		getsym();
		fsys.set(Symbol.endsym);
        //test(blocFirsys,fsys,8);
		blockSym(lev,fsys);
		while(sym.symID == Symbol.semicolon){
			getsym();
			
	        //test(blocFirsys,fsys,8);
			blockSym(lev,fsys);
		}
		if(sym.symID == Symbol.endsym){
			getsym();
		}else{
			//缺少end
			er.rep_errors(sc.lineCnt, 17);
		}
	}
	/**
	 * 重复语句
	 * @param lev
	 */
	void repeatSym(int lev,BitSet fsys){
		getsym();
		int cx = this.arrayPtr;
		
		fsys.set(Symbol.untilsym);
        //test(blocFirsys,fsys,8);
		blockSym(lev,fsys);
		while(sym.symID == Symbol.semicolon){
			getsym();

	        //test(blocFirsys,fsys,8);
			blockSym(lev,fsys);
		}
		if(sym.symID == Symbol.untilsym){
			getsym();
			condition(lev,fsys);
			this.gen(Pcode.JPC, 0, cx);
		}else{
			//缺少until
			er.rep_errors(sc.lineCnt, 28);
		}
	}
	/**
	 * 写语句
	 * @param lev
	 */
	void writeSym(int lev,BitSet fsys){
		getsym();
		if(sym.symID == Symbol.lparen){
			int position = 0;
			do{
				getsym();
				//fsys.set(Symbol.rparen);
				//test(expFirsys,fsys,24);
				expresion(lev,fsys);
				this.gen(Pcode.WRT, 0, 0);
			}while(sym.symID == Symbol.comma);
			if(sym.symID == Symbol.rparen){
				getsym();
			}else{
				//缺少右括弧
				er.rep_errors(sc.lineCnt, 22);
			}
		}else{
			//缺少左括号
			er.rep_errors(sc.lineCnt, 34);
		}
		//this.gen(Pcode.OPR, 0, 15);   //输出换行
	}
	/**
	 * 读语句
	 * @param lev
	 */
	void readSym(int lev,BitSet fsys){
		getsym();
		if(sym.symID == Symbol.lparen){
			int position = 0;
			do{
				getsym();
				if(sym.symID == Symbol.ident){
					position = table.serachTable(sym.name);
					if(position > 0){
						Item item = table.getItem(position);
						 if (item.type != Item.variable) {                      //判断符号表中的该符号类型是否为变量
		                        //标识符不为变量
							 	er.rep_errors(sc.lineCnt, 15);
							 	BitSet s = new BitSet(sysnum);
							 	s.set(Symbol.rparen);
							 	s.set(Symbol.semicolon);
							 	s.set(Symbol.period);
							 	jump(s);
							 	break;
							 	
		                    } else {
		                        //this.gen(Pcode.OPR, 0, 16);                            //OPR 0 16:读入一个数据
		                        this.gen(Pcode.STO, lev - item.level, item.addr);   //STO L A;存储变量
		                   }
					}else{
						//未找到该标识符
						er.rep_errors(sc.lineCnt, 11);
						BitSet s = new BitSet(sysnum);
					 	s.set(Symbol.rparen);
					 	s.set(Symbol.semicolon);
					 	s.set(Symbol.period);
					 	jump(s);
					 	break;
					}
				}else{
					//不是标识符
					er.rep_errors(sc.lineCnt, 30);
					BitSet s = new BitSet(sysnum);
				 	s.set(Symbol.rparen);
				 	s.set(Symbol.semicolon);
				 	s.set(Symbol.period);
				 	jump(s);
				 	break;
				}
				getsym();
			}while(sym.symID == Symbol.comma);
		}else{
			//缺少左括号
			er.rep_errors(sc.lineCnt, 34);
		}
		
		if(sym.symID == Symbol.rparen){
			getsym();
		}else{
			//缺少右括弧
			er.rep_errors(sc.lineCnt, 22);
		}
	}
	/**
	 * 过程调用语句
	 * @param lev
	 */
	void callSym(int lev,BitSet fsys){
		getsym();
		if(sym.symID == Symbol.ident){
			int position = table.serachTable(sym.name);
			if(position > 0){
				Item item = table.getItem(position);
				if(item.type == Item.procedure){
					this.gen(Pcode.CAL, lev - item.level, item.addr);
					getsym();
				}else{
					//标识符不是过程
					er.rep_errors(sc.lineCnt, 15);
					getsym();
				}
			}else{
				//找不到标识符
				er.rep_errors(sc.lineCnt, 11);
				getsym();
			}
		}else{
			//不是标识符
			er.rep_errors(sc.lineCnt, 4);
			getsym();
		}
	}
	/**
	 * 当型循环语句
	 * @param lev
	 */
	void whileSym(int lev,BitSet fsys){
		getsym();
		System.out.println("hi-while循环");
		int ax = this.arrayPtr;
		
		fsys.set(Symbol.ident);
		fsys.set(Symbol.number);
		//test(ifFirsys,fsys,24);
		condition(lev,fsys);
		if(sym.symID == Symbol.dosym){
			getsym();
		}else{
			//缺少do语句
			er.rep_errors(sc.lineCnt, 18);
		}
		int cx = this.arrayPtr;
		this.gen(Pcode.JPC, 0 ,0);
		
        //test(blocFirsys,fsys,8);
		blockSym(lev,fsys);
		
		this.gen(Pcode.JMP, 0, ax);
		this.pcodeArray[cx].a = this.arrayPtr;
	}
	/**
	 * 条件语句
	 * @param lev
	 */
	void ifSym(int lev,BitSet fsys){
		System.out.println("~~~~~~条件语句开始~~~~~~");
		fsys.set(Symbol.ident);
		fsys.set(Symbol.number);
		//test(ifFirsys,fsys,24);
		getsym();
		condition(lev,fsys);
		if(sym.symID == Symbol.thensym){
			getsym();
		}else{
			//缺少then语句
			er.rep_errors(sc.lineCnt, 16);
		}
		int cx = this.arrayPtr;                                          //保存当前指令地址
        this.gen(Pcode.JPC, 0, 0);                                      //生成条件跳转指令，跳转地址位置，暂时写0
        
        //test(blocFirsys,fsys,8);
        blockSym(lev,fsys);                                                     //处理then后的statement
        this.pcodeArray[cx].a = this.arrayPtr;
        
        if(sym.symID == Symbol.elsesym){
        	getsym();
        	
            //test(blocFirsys,fsys,8);
        	blockSym(lev,fsys);
        	System.out.println("~~~~~~条件语句结束~~~~~~");
        }
	}
	/**
	 * 条件
	 * @param lev
	 */
	void condition(int lev,BitSet fsys){
		System.out.println("~~~~~~条件开始~~~~~~");
		if(sym.symID == Symbol.oddsym){
			getsym();
			//test(expFirsys,fsys,24);
			expresion(lev,fsys);
			this.gen(Pcode.OPR, 0, 6);    //OPR 0 6:判断栈顶元素是否为奇数
		}else{
			expresion(lev,fsys);
			int tempsym = sym.symID;
			if(sym.symID == Symbol.eql || sym.symID == Symbol.uneql || sym.symID == Symbol.gtr || sym.symID == Symbol.greaterequal || sym.symID == Symbol.lss || sym.symID == Symbol.lessequal){
				getsym();
				expresion(lev,fsys);
				if(tempsym == Symbol.eql){
					this.gen(Pcode.OPR, 0, 8);
				}else if(tempsym == Symbol.uneql){
					this.gen(Pcode.OPR, 0, 9);
				}else if(tempsym == Symbol.gtr){
					this.gen(Pcode.OPR, 0, 12);
				}else if(tempsym == Symbol.greaterequal){
					this.gen(Pcode.OPR, 0, 11);
				}else if(tempsym == Symbol.lss){
					this.gen(Pcode.OPR, 0, 10);
				}else if(tempsym == Symbol.lessequal){
					this.gen(Pcode.OPR, 0, 13);
				}
				System.out.println("~~~~~~条件结束~~~~~~");
			}else{
				//符号不识别
				er.rep_errors(sc.lineCnt, 11);
			}
		}
	}
	/**
	 * 赋值语句
	 * @param lev
	 */
	void assignin(int lev,BitSet fsys){
		System.out.println("******赋值语句name:" + sym.name +"*********");
		table.printTable();
		int position = table.serachTable(sym.name);
		if(position > 0){
			Item item = table.getItem(position);
			if(item.type == Item.variable){
				getsym();
				if(sym.symID == Symbol.becomes){
					getsym();
					expresion(lev,fsys);
					this.gen(Pcode.STO, lev - item.level, item.addr);
					System.out.println("******赋值语句成功*********"+sym.symID);
				}else{
					//不是赋值符号
					if(sym.symID == Symbol.eql){
						er.rep_errors(sc.lineCnt, 13);
						getsym();
						expresion(lev,fsys);
						this.gen(Pcode.STO, lev - item.level, item.addr);
					}else{
						er.rep_errors(sc.lineCnt, 29);
					}
				}
			}else{
				//不是variable类型
				er.rep_errors(sc.lineCnt, 12);
			}
		}else{
			//未找到标识符
			er.rep_errors(sc.lineCnt, 11);
			BitSet b = new BitSet(sysnum);
			b.set(Symbol.ident);
			jump(b);
			getsym();
		}
	}
	/**
	 * 表达式语句
	 * @param lev
	 */
	void expresion(int lev,BitSet fsys){
		if(sym.symID == Symbol.plus || sym.symID == Symbol.minus){
			int tempType = sym.symID;
			getsym();
			//test(itemFirsys,fsys,26);
			term(lev,fsys);
			if (tempType == Symbol.minus) //OPR 0 1:：NEG取反
            {
                this.gen(Pcode.OPR, 0, 1);
            }
		}else{
			term(lev,fsys);
		}
		while(sym.symID == Symbol.plus || sym.symID == Symbol.minus){
			int tempType = sym.symID;
			getsym();
			//test(itemFirsys,fsys,26);
			term(lev,fsys);
			if (tempType == Symbol.minus)
            {
                this.gen(Pcode.OPR, 0, 3);
            }else if(tempType == Symbol.plus){
            	this.gen(Pcode.OPR, 0, 2);
            }
		}
	}
	/**
	 * 项语句
	 * @param lev
	 */
	void term(int lev,BitSet fsys){
		//test(facFirsys,fsys,26);
		factor(lev,fsys);
		while(sym.symID == Symbol.times || sym.symID == Symbol.slash){
			int tempType = sym.symID;
			getsym();
			//test(facFirsys,fsys,26);
			factor(lev,fsys);
			//乘法:OPR 0 4 ,除法:OPR 0 5
			if (tempType == Symbol.times)
            {
                this.gen(Pcode.OPR, 0, 4);
            }else if(tempType == Symbol.slash){
            	this.gen(Pcode.OPR, 0, 5);
            }
		}
	}
	/**
	 * 因子语句
	 * @param lev
	 */
	void factor(int lev,BitSet fsys){
		System.out.println("因子语句！！！！"+sym.symID);
		if(sym.symID == Symbol.ident){
			int position = table.serachTable(sym.name);
			if(position > 0){
				Item item = table.getItem(position);
				if(item.type == Item.constant){ //常量
					this.gen(Pcode.LIT, 0, item.value);
				}else if(item.type == Item.variable){ //变量
					
					this.gen(Pcode.LOD, lev - item.level, item.addr);
				}else{
					//不是常量or变量
					er.rep_errors(sc.lineCnt, 15);
				}
			}else{
				//未找到标识符
				er.rep_errors(sc.lineCnt, 11);
			}
			getsym();
		}else if(sym.symID == Symbol.number){
			System.out.println("因子语句开始！！！"+sym.symID);
			int num = sym.num;
			 this.gen(Pcode.LIT, 0, num);                     //生成lit指令，把这个数值字面常量放到栈顶
             getsym();
             System.out.println("因子语句结束！！！"+sym.symID);
		}else if(sym.symID == Symbol.lparen){
			getsym();
			expresion(lev,fsys);
			if(sym.symID == Symbol.rparen){
				getsym();
			}else{
				//少右括号
				er.rep_errors(sc.lineCnt, 33);
			}
		}else{
			//不是因子
			er.rep_errors(sc.lineCnt, 26);
		}
	}
	
	public void printArray(){
		String msg = null;
		for(int i = 0; pcodeArray[i] != null; i++){
			msg = "" + i + "  " + Pcode.pcode[pcodeArray[i].f] + " " + pcodeArray[i].l + " " + pcodeArray[i].a;
			System.out.println(msg);
			compile.write(msg + '\n');
		}
	}
	public static void main(String[] args){
		String name = "test4.txt";
		String path = name;
		Tex s = new Tex(path);
		Error e = new Error();
		SymbolTable table = new SymbolTable();
		syntaxAnalysis p = new syntaxAnalysis(table,e,s);
		p.mainProgram();
		p.printArray();
		if(e.errs.isEmpty()){
			p.cout("test4_re.txt");
		}else{
			e.cout("test4_re.txt");
		}
	}
}
