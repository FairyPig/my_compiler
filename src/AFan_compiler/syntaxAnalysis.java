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
	//���ݼ���
	BitSet fsys = new BitSet(sysnum);
	//�ֳ���
	BitSet partFirsys = new BitSet(sysnum);
	//���
	BitSet blocFirsys = new BitSet(sysnum);
	//����
	BitSet facFirsys = new BitSet(sysnum);
	//����
	BitSet ifFirsys = new BitSet(sysnum);
	//��
	BitSet itemFirsys = new BitSet(sysnum);
	//���ʽ
	BitSet expFirsys = new BitSet(sysnum);
	int dx = 0;
	//����ջ����
	private static final int stackSize = 1000;
	private static final int arraySize = 500;
	//���������ָ�룬[0,arraySize-1]
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
			System.out.println("�������");
		}
		pcodeArray[arrayPtr++] = new Pcode(f,l,a);
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
	//s1----Ӧ�������
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
			System.out.println("����ɹ���");
		}
	}
	
	void partProgram(int lev,BitSet fsys){
		System.out.println("******�ֳ���lev:"+lev+"*********");
		int cx0,dx0,tx0;
		tx0 = table.itemPtr;
		dx0 = dx;
		dx = 3;
		table.getItem(table.itemPtr).addr = this.arrayPtr;         //�ڷ��ű�ĵ�ǰλ�ü�¼�����jmpָ���ڴ�����е�λ�� 
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
        
        //�����ڴ���룬
        this.gen(Pcode.INT, 0, dx);
        
        fsys.set(Symbol.semicolon);
        fsys.set(Symbol.endsym);
        //test(blocFirsys,fsys,8);
		blockSym(lev,fsys);
		
		this.gen(Pcode.OPR, 0, 0);
		
		System.out.println("******�ֳ������:"+lev+"*********");
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
			//�ٷֺ�
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
						//�Ѵ��ڱ�ʶ��
						er.rep_errors(sc.lineCnt, 27);
						BitSet s1 = new BitSet(sysnum);
						s1.set(Symbol.comma);
						s1.set(Symbol.semicolon);
						jump(s1);
					}
					getsym();
				}else{
					//���治������
					er.rep_errors(sc.lineCnt, 2);
					BitSet s2 = new BitSet(sysnum);
					s2.set(Symbol.comma);
					s2.set(Symbol.semicolon);
					jump(s2);
				}
			}else{
				//���ǵȺ�
				er.rep_errors(sc.lineCnt, 3);
				BitSet s3 = new BitSet(sysnum);
				s3.set(Symbol.comma);
				s3.set(Symbol.semicolon);
				jump(s3);
			}
		}else{
			//���Ǳ�ʶ��
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
			//�ٷֺ�
			er.rep_errors(sc.lineCnt, 5);
		}
	}
	
	void defineVar(int lev,BitSet fsys){
		if(sym.symID == Symbol.ident){
			if(searchItem(sym)){
				table.insertTable(sym, Item.variable, lev, dx);
				dx++;
			}else{
				//��ʶ���Ѵ���
				er.rep_errors(sc.lineCnt, 27);
				BitSet s1 = new BitSet(sysnum);
				s1.set(Symbol.comma);
				s1.set(Symbol.semicolon);
				jump(s1);
			}
			getsym();
		}else{
			//���Ǳ�ʶ��
			er.rep_errors(sc.lineCnt, 4);
			BitSet s1 = new BitSet(sysnum);
			s1.set(Symbol.comma);
			s1.set(Symbol.semicolon);
			jump(s1);
		}
	}
	//����˵������
	void proSym(int lev,BitSet fsys){
		while (sym.symID == Symbol.procsym) {                 //�����procedure
            getsym();
            if (sym.symID == Symbol.ident) {                      //��д���ű�
                table.insertTable(sym, Item.procedure, lev, dx);        //��ǰ������Ĵ�С 
                getsym();
            } else {
                //procedure��ӦΪ��ʶ��
    			er.rep_errors(sc.lineCnt, 4);
    			BitSet s1 = new BitSet(sysnum);
    			s1.set(Symbol.semicolon);
    			jump(s1);
            }
            if (sym.symID == Symbol.semicolon)               //�ֺţ���ʾ<�����ײ�>����
            {
                getsym();
            } else {
                //©�˶��Ż��߷ֺ�
            	er.rep_errors(sc.lineCnt, 5);
            }
            
            fsys.or(partFirsys);
            //test(partFirsys,fsys,8);
            partProgram(lev + 1,fsys);                                  //Ƕ�ײ��+1�������ֳ���

            if (sym.symID == Symbol.semicolon) {                          //<����˵������> ʶ��ɹ�
            	getsym();    
            } else {
                //©�˶��Ż��߷ֺ�
            	er.rep_errors(sc.lineCnt, 5);
            }
        }
	}
	//���
	void blockSym(int lev,BitSet fsys){
		System.out.println("--------��俪ʼ-------"+sym.symID);
		System.out.println(sym.symID);
		//��ֵ���
		if(sym.symID == Symbol.ident){
			assignin(lev,fsys);
		}
		//�������
		else if(sym.symID == Symbol.ifsym){
			ifSym(lev,fsys);
		}
		//����ѭ�����
		else if(sym.symID == Symbol.whilesym){
			whileSym(lev,fsys);
		}
		//���̵������
		else if(sym.symID == Symbol.callsym){
			callSym(lev,fsys);
		}
		//�����
		else if(sym.symID == Symbol.readsym){
			readSym(lev,fsys);
		}
		//д���
		else if(sym.symID == Symbol.writesym){
			writeSym(lev,fsys);
		}
		//�������
		else if(sym.symID == Symbol.beginsym){
			beginSym(lev,fsys);
		}
		//�ظ����
		else if(sym.symID == Symbol.repeatsym){
			repeatSym(lev,fsys);
		}
		else{
			//�����
		}
		System.out.println("--------������-------"+sym.symID);
	}
	/**
	 * �������
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
			//ȱ��end
			er.rep_errors(sc.lineCnt, 17);
		}
	}
	/**
	 * �ظ����
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
			//ȱ��until
			er.rep_errors(sc.lineCnt, 28);
		}
	}
	/**
	 * д���
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
				//ȱ��������
				er.rep_errors(sc.lineCnt, 22);
			}
		}else{
			//ȱ��������
			er.rep_errors(sc.lineCnt, 34);
		}
		//this.gen(Pcode.OPR, 0, 15);   //�������
	}
	/**
	 * �����
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
						 if (item.type != Item.variable) {                      //�жϷ��ű��еĸ÷��������Ƿ�Ϊ����
		                        //��ʶ����Ϊ����
							 	er.rep_errors(sc.lineCnt, 15);
							 	BitSet s = new BitSet(sysnum);
							 	s.set(Symbol.rparen);
							 	s.set(Symbol.semicolon);
							 	s.set(Symbol.period);
							 	jump(s);
							 	break;
							 	
		                    } else {
		                        //this.gen(Pcode.OPR, 0, 16);                            //OPR 0 16:����һ������
		                        this.gen(Pcode.STO, lev - item.level, item.addr);   //STO L A;�洢����
		                   }
					}else{
						//δ�ҵ��ñ�ʶ��
						er.rep_errors(sc.lineCnt, 11);
						BitSet s = new BitSet(sysnum);
					 	s.set(Symbol.rparen);
					 	s.set(Symbol.semicolon);
					 	s.set(Symbol.period);
					 	jump(s);
					 	break;
					}
				}else{
					//���Ǳ�ʶ��
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
			//ȱ��������
			er.rep_errors(sc.lineCnt, 34);
		}
		
		if(sym.symID == Symbol.rparen){
			getsym();
		}else{
			//ȱ��������
			er.rep_errors(sc.lineCnt, 22);
		}
	}
	/**
	 * ���̵������
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
					//��ʶ�����ǹ���
					er.rep_errors(sc.lineCnt, 15);
					getsym();
				}
			}else{
				//�Ҳ�����ʶ��
				er.rep_errors(sc.lineCnt, 11);
				getsym();
			}
		}else{
			//���Ǳ�ʶ��
			er.rep_errors(sc.lineCnt, 4);
			getsym();
		}
	}
	/**
	 * ����ѭ�����
	 * @param lev
	 */
	void whileSym(int lev,BitSet fsys){
		getsym();
		System.out.println("hi-whileѭ��");
		int ax = this.arrayPtr;
		
		fsys.set(Symbol.ident);
		fsys.set(Symbol.number);
		//test(ifFirsys,fsys,24);
		condition(lev,fsys);
		if(sym.symID == Symbol.dosym){
			getsym();
		}else{
			//ȱ��do���
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
	 * �������
	 * @param lev
	 */
	void ifSym(int lev,BitSet fsys){
		System.out.println("~~~~~~������俪ʼ~~~~~~");
		fsys.set(Symbol.ident);
		fsys.set(Symbol.number);
		//test(ifFirsys,fsys,24);
		getsym();
		condition(lev,fsys);
		if(sym.symID == Symbol.thensym){
			getsym();
		}else{
			//ȱ��then���
			er.rep_errors(sc.lineCnt, 16);
		}
		int cx = this.arrayPtr;                                          //���浱ǰָ���ַ
        this.gen(Pcode.JPC, 0, 0);                                      //����������תָ���ת��ַλ�ã���ʱд0
        
        //test(blocFirsys,fsys,8);
        blockSym(lev,fsys);                                                     //����then���statement
        this.pcodeArray[cx].a = this.arrayPtr;
        
        if(sym.symID == Symbol.elsesym){
        	getsym();
        	
            //test(blocFirsys,fsys,8);
        	blockSym(lev,fsys);
        	System.out.println("~~~~~~����������~~~~~~");
        }
	}
	/**
	 * ����
	 * @param lev
	 */
	void condition(int lev,BitSet fsys){
		System.out.println("~~~~~~������ʼ~~~~~~");
		if(sym.symID == Symbol.oddsym){
			getsym();
			//test(expFirsys,fsys,24);
			expresion(lev,fsys);
			this.gen(Pcode.OPR, 0, 6);    //OPR 0 6:�ж�ջ��Ԫ���Ƿ�Ϊ����
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
				System.out.println("~~~~~~��������~~~~~~");
			}else{
				//���Ų�ʶ��
				er.rep_errors(sc.lineCnt, 11);
			}
		}
	}
	/**
	 * ��ֵ���
	 * @param lev
	 */
	void assignin(int lev,BitSet fsys){
		System.out.println("******��ֵ���name:" + sym.name +"*********");
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
					System.out.println("******��ֵ���ɹ�*********"+sym.symID);
				}else{
					//���Ǹ�ֵ����
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
				//����variable����
				er.rep_errors(sc.lineCnt, 12);
			}
		}else{
			//δ�ҵ���ʶ��
			er.rep_errors(sc.lineCnt, 11);
			BitSet b = new BitSet(sysnum);
			b.set(Symbol.ident);
			jump(b);
			getsym();
		}
	}
	/**
	 * ���ʽ���
	 * @param lev
	 */
	void expresion(int lev,BitSet fsys){
		if(sym.symID == Symbol.plus || sym.symID == Symbol.minus){
			int tempType = sym.symID;
			getsym();
			//test(itemFirsys,fsys,26);
			term(lev,fsys);
			if (tempType == Symbol.minus) //OPR 0 1:��NEGȡ��
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
	 * �����
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
			//�˷�:OPR 0 4 ,����:OPR 0 5
			if (tempType == Symbol.times)
            {
                this.gen(Pcode.OPR, 0, 4);
            }else if(tempType == Symbol.slash){
            	this.gen(Pcode.OPR, 0, 5);
            }
		}
	}
	/**
	 * �������
	 * @param lev
	 */
	void factor(int lev,BitSet fsys){
		System.out.println("������䣡������"+sym.symID);
		if(sym.symID == Symbol.ident){
			int position = table.serachTable(sym.name);
			if(position > 0){
				Item item = table.getItem(position);
				if(item.type == Item.constant){ //����
					this.gen(Pcode.LIT, 0, item.value);
				}else if(item.type == Item.variable){ //����
					
					this.gen(Pcode.LOD, lev - item.level, item.addr);
				}else{
					//���ǳ���or����
					er.rep_errors(sc.lineCnt, 15);
				}
			}else{
				//δ�ҵ���ʶ��
				er.rep_errors(sc.lineCnt, 11);
			}
			getsym();
		}else if(sym.symID == Symbol.number){
			System.out.println("������俪ʼ������"+sym.symID);
			int num = sym.num;
			 this.gen(Pcode.LIT, 0, num);                     //����litָ��������ֵ���泣���ŵ�ջ��
             getsym();
             System.out.println("����������������"+sym.symID);
		}else if(sym.symID == Symbol.lparen){
			getsym();
			expresion(lev,fsys);
			if(sym.symID == Symbol.rparen){
				getsym();
			}else{
				//��������
				er.rep_errors(sc.lineCnt, 33);
			}
		}else{
			//��������
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
