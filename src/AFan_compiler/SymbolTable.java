package AFan_compiler;

public class SymbolTable {
	//当前符号表指针
		public int itemPtr = 0;
		//符号表大小
		public static final int tableMax = 100;
		public static final int symMax = 10;
		public static final int addrMax = 1000000;
		public static final int levMax = 3;
		public static final int numMax = 14;
		//名字表
		public Item[] table = new Item[tableMax];
		
		public Item getItem(int i){
			if(table[i] == null){
				table[i] = new Item();
			}
			return table[i];
		}
		/**
		 * 插入符号表
		 * @param s
		 * @param type
		 * @param level
		 * @param add
		 */
		public void insertTable(Symbol s,int type,int level,int add){
			itemPtr++;
			Item item = getItem(itemPtr);
			switch(type){
			case Item.constant:
				item.level = level; 
				item.value = s.num;
				break;
			case Item.variable:    
				item.level = level;    
				item.addr = add;    
				break;
			case Item.procedure:    
				item.level = level; 
			}
			item.name = s.name;
			item.type = type;
		}
		/**
		 * 在某一层查找变量or常量
		 * @param name
		 * @param level
		 * @return
		 */
		public int serachTable(String name){
			for(int i = 1;i <= itemPtr; i++){
				Item item = getItem(i);
				if(item.name.equals(name)){
					return i;
				}
			}
			return -1;
		}
		/**
		 * debug专用
		 */
		public void printTable(){
			System.out.println("------begin to print SymbolTable------");
			for(int i = 1; i <= itemPtr; i++){
				Item item = getItem(i);
				System.out.println("   "+"no." + i + ":  name:"+item.name + "   type:" + item.type + "  lev:" + item.level + "   value:" + item.value + "    addr:" + item.addr);
			}
			System.out.println("------end to print SymbolTable------");
		}
}
