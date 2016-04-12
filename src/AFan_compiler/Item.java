package AFan_compiler;

public class Item {
	//常量
		public final static int constant = 1;
		//变量
		public final static int variable = 2;
		//过程
		public final static int procedure = 3;
			
		public String name;
		public int type;
		//大小
		public int value;
		//层次
		public int level;
		public int addr;
		public int size;
		
		public Item(){
			name = "";
			type = 0;
		}
		
		public Item(String name,int lev){
			this.name = name;
			level = lev;
		}
		
		/**
		 * 变量修改值，常量和过程均会报错
		 * @param num
		 * @return 修改成功or错
		 */
		public boolean setValue(int num){
			if(this.type != 2){
				return false;
			}else{
				this.value = num;
				return true;
			}
		}
}
