package AFan_compiler;

public class Item {
	//����
		public final static int constant = 1;
		//����
		public final static int variable = 2;
		//����
		public final static int procedure = 3;
			
		public String name;
		public int type;
		//��С
		public int value;
		//���
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
		 * �����޸�ֵ�������͹��̾��ᱨ��
		 * @param num
		 * @return �޸ĳɹ�or��
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
