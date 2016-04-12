package AFan_compiler;

public class Symbol {
		public static String[] word= new String[]{
			"begin",
			"call",
			"const",
			"do",
			"end",
			"if",
			"odd",
			"procedure",
			"read",
			"then",
			"var",
			"while",
			"write",
			"repeat",
			"until",
			"else"
		};
		public static String[] wsym = new String[]{
			"beginsym",
			"callsym",
			"constsym",
			"dosym",
			"endsym",
			"ifsym",
			"oddsym",
			"procsym",
			"readsym",
			"thensym",
			"varsym",
			"whilesym",
			"writesym",
			"repeatsym",
			"untilsym",
			"elsesym"
		};
		
		public static int ident = 0;
		public static int plus = 1;//+
		public static int minus = 2;//-
		public static int times = 3;//*
		public static int slash = 4;// /
		public static int lparen = 5;//(
		public static int rparen = 6;//)
		public static int eql = 7;//=
		public static int uneql = 8;//<>
		public static int comma = 9;//,
		public static int period = 10;//.
		public static int lss = 11;//<
		public static int gtr = 12;//>
		public static int semicolon = 13;//;
		public static int lessequal = 14;//<=
		public static int greaterequal = 15;//>=
		public static int beginsym = 18;
		public static int callsym = 19;
		public static int constsym = 20;
		public static int dosym = 21;
		public static int endsym = 22;
		public static int ifsym = 23;
		public static int oddsym = 24;
		public static int procsym = 25;
		public static int readsym = 26;
		public static int thensym = 27;
		public static int varsym = 28;
		public static int whilesym = 29;
		public static int writesym = 30;
		public static int repeatsym = 31;
		public static int untilsym = 32;
		public static int elsesym = 33;
		public static int number = 34;
		public static int becomes = 35;//:=
		public static int nul = 36;
		/*
		 * 返回-1 为未查到
		 * 否则返回ID值
		 */
		public static int getWordId(String my_name){
			int num = -1;
			for(int i = 0; i < word.length;i ++){
				if(word[i].equals(my_name)){
					num = i + 18;
					break;
				}
			}
			return num;
		}
		
		//类型
		int symID;
		int num = 0;
		String name;
		public Symbol(int id){
			symID = id;
			name = "";
			num = 0;
		}
}
