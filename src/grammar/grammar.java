package grammar;
import java.util.*;
import java.io.*;

public class grammar {
	public static Vector<terminator> term;
	public static Vector<terminator> non_term;
	public static Vector<expression> expr;
	public static String filename="grammar.txt";
	public static Vector<projlist> projs; 
	public static Vector<project> proj;
	public static expression first_expression;	//在 ReadFromFile() 函数中初始化
	public static int linenum;		//action表和goto表的宽度
	public static sheet sheets;
	
	private int LocateExpr(expression target) {
		boolean find=false;
		int order=0;
		for(expression temp:expr) {
			if(temp.equals(target)) {
				find=true;
				break;
			}
			order++;
		}
		if(find) {
			return order;
		}
		else {
			return -1;
		}
	}
	
	private static terminator isTerm(String val) {
		for(terminator temp:term) {
			if(temp.val.equals(val)) {
				return temp;
			}
		}
		return null;
	}
	
	private static terminator isNonTerm(String val) {
		for(terminator temp:non_term) {
			if(temp.val.equals(val)) {
				return temp;
			}
		}
		return null;
	}
	
	public static void ReadFromFile() throws IOException {
		/*将文本的每一行保存到数据结构（链表）里面*/
		List<String> list = new ArrayList<String>();
		InputStreamReader read = new InputStreamReader(new FileInputStream(filename));// 考虑到编码格式
		BufferedReader bufferedReader = new BufferedReader(read);
		String line="";
		while ((line = bufferedReader.readLine()) != null)
        {
            list.add(line);
        }
		bufferedReader.close();
		/*初始化语法的基本信息*/
		initialize();
		/*处理得到的信息*/
		/*step1 获取所有的非终结符*/
		for(String temp:list) {
			String [] word=temp.split(" ");
			if(isNonTerm(word[0])==null) {
				terminator new_term=new terminator(word[0]);
				new_term.isterm=false;
				non_term.add(new_term);
			}
		}
		/*step2 添加所有的终结符到非终结符的集合里面去*/
		for(String temp:list) {
			String [] word=temp.split(" ");
			for(int i=1;i<word.length;i++) {
				/*只有不是非终结符且之前没有被算进去的才需要添加进去*/
				if(isTerm(word[i])!=null) {
					continue;
				}
				else if(isNonTerm(word[i])!=null) {
					continue;
				}
				else {
					terminator new_term=new terminator(word[i]);
					new_term.isterm=true;
					term.add(new_term);
				}
			}
		}
		/*step3 将所有的作为表达式使用expression类存储起来*/
		int count=0;
		for(String temp:list) {
			String [] word=temp.split(" ");
			terminator left=isNonTerm(word[0]);
			expression new_expr=new expression(left);
			for(int i=1;i<word.length;i++) {
				terminator t;
				if((t=isTerm(word[i]))==null) {
					/*是一个非终结符*/
					t=isNonTerm(word[i]);
				}
				if(t==null) {
					System.out.print(i);
				}
				new_expr.right.add(t);
			}
			new_expr.value=count++;
			expr.add(new_expr);
		}
		first_expression=expr.firstElement();
		/*step4 创建出所有项目集族
		for(expression temp:expr) {
			for(int i=0;i<=temp.right.size();i++) {
				project new_proj=new project(temp,i,false);
				if(i==temp.right.size()) {
					new_proj.reduct=true;
				}
				proj.add(new_proj);
			}
		}
		*/
	}
	
	private static void initialize() {
		term=new Vector<terminator>();
		non_term=new Vector<terminator>();
		expr=new Vector<expression>();
		projs=new Vector<projlist>();
		proj=new Vector<project>();
	}
	
	private static void GetFirst() {
		/*获取所有终结符的first集*/
		/*所有终结符的fist集合就是他自己*/
		for(terminator temp:term) {
			temp.FIRST_SET.add(temp);
		}
		/*非终结符的first集合*/
		boolean flag=true;		//检测记号，如果first集合不再变化则跳出循环
		while(flag) {
			flag=false;
			/*遍历non_term*/
			for(terminator temp_term:non_term) {
				/*遍历所有的推导式*/
				for(expression temp_expr:expr) {
					/*如果左侧第一个是该字符*/
					if(temp_expr.left==temp_term) {
						/*如果推导式的右边的第一个是终结符并且FIRST集合没有被计算过*/
						if(temp_expr.right.firstElement().isterm
							&&temp_term.FIRST_SET.contains(temp_expr.right.elementAt(0))==false) {
							temp_term.FIRST_SET.add(temp_expr.right.elementAt(0));
							flag=true;
						}
						else if(temp_term.isterm==false) {
							/*遍历右边第一个非终结符的first集合*/
							for(terminator temp:temp_expr.right.firstElement().FIRST_SET) {
								if(temp_term.FIRST_SET.contains(temp)==false) {
									temp_term.FIRST_SET.add(temp);
									flag=true;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static projlist createclosure(project p) {
		/*根据一个已有的项目，创建所有的项目*/
		projlist result=new projlist();
		
		/*如果直接是可以规约的状态*/
		if(p.pointpos==p.expr.right.size()) {
			p.reduct=true;
			result.proj.add(p);
			return result;
		}
		
		p.reduct=false;
		result.proj.add(p);
		
		boolean flag=false;
		while(true) {
			flag=false;
			projlist temp_projlist=new projlist();
			for(project temp_project:result.proj) {
				/*如果是可以规约的状态*/
				if(temp_project.reduct) {
					continue;
				}
				/*如果下一个是终结符*/
				if(temp_project.expr.right.elementAt(temp_project.pointpos).isterm) {
					continue;
				}
				/*点后面的一个符号的内容，which must be a non_term*/
				terminator next_term=temp_project.expr.right.elementAt(temp_project.pointpos);
				/*如果点位于倒数第2个的位置*/
				if(temp_project.pointpos==temp_project.expr.right.size()-1) {
					/*从所有表达式里面找到对应的左边的非终结符是点后面的符号的*/
					for(expression temp_expr:expr) {
						if(temp_expr.left.equals(next_term)) {
							/*创建一个表达式进来*/
							project new_project=new project(temp_expr,temp_project.term);
							if(new_project.expr.right.size()==new_project.pointpos) {
								new_project.reduct=true;
							}
							if(isIn(result,new_project)==false&&isIn(temp_projlist,new_project)==false) {
								temp_projlist.proj.add(new_project);
								flag=true;
							}
						}
					}
				}
				else {
					/*如果不是在倒数第二个的位置*/
					for(expression temp_expr:expr) {
						if(temp_expr.left.equals(next_term)) {
							/*对于这个推导式，需要考虑他的下一个字符是什么*/
							for(terminator peek_term:temp_project.expr.right.elementAt(temp_project.pointpos+1).FIRST_SET) {
								project new_project=new project(temp_expr,peek_term);
								if(new_project.expr.right.size()==new_project.pointpos) {
									new_project.reduct=true;
								}
								if(isIn(result,new_project)==false&&isIn(temp_projlist,new_project)==false) {
									temp_projlist.proj.add(new_project);
									flag=true;
								}
							}
						}
							
					}
				}
				
				
			}
			result.proj.addAll(temp_projlist.proj);
			
			if(flag==false) {
				break;
			}
		}
		return result;
	}
	
	private static boolean isIn(projlist p,project q) {
		boolean find=false;
		for(project temp:p.proj) {
			if(temp.equal(q)) {
				find=true;
				break;
			}
			else {
				continue;
			}
		}
		return find;
	}
	
	private static boolean isIn(Vector<terminator> p,terminator q) {
		boolean find=false;
		for(terminator temp:p) {
			if(q.equals(temp)) {
				find=true;
				break;
			}
		}
		return find;
	}
	
	public static void set_goto_action() {
		/*语法的终结符*/
		terminator end=new terminator("#");
		end.FIRST_SET.add(end);
		term.add(end);
		linenum=term.size()+non_term.size();
		sheets=new sheet(linenum);
		
		/*定义初始项目集族*/
		project begin_proj=new project(first_expression,end);
		projlist first_projlist=createclosure(begin_proj);
		actionlist first_action_list=new actionlist(linenum);
		sheets.addLine(first_action_list);
		projs.add(first_projlist);
		/*将第一项添加到action表和goto表当中*/
		boolean flag=true;
		projlist now_projlist=first_projlist;
		int count=0;
		while(flag) {
			flag=false;
			Vector<terminator> next_symbol=new Vector<terminator>();
			/*step1 先统计遇到哪些符号会需要跳转*/
			for(project temp_project:now_projlist.proj) {
				/*如果是一个不可规约的项目*/
				if(temp_project.reduct==false) {
					/*需要将其添加到*/
					terminator next_term=temp_project.expr.right.elementAt(temp_project.pointpos);
					if(isIn(next_symbol,next_term)==false) {
						next_symbol.add(next_term);
					}
				}
				/*如果是一个可以规约的项目*/
				else {
					/*需要设置对应的action表*/
					/*step1 找出是那个expression*/
					int x=temp_project.expr.value;
					action new_action=new action(action.reduction, x);
					sheets.action_sheet.elementAt(count).array.put(temp_project.term,new_action);
				}
			}
			
			/*step2 求每一个可跳转项目的闭包*/
			for(terminator temp_term:next_symbol) {
				
			}
		}
		return;
		
	}
	
	public static void main(String arg[]) throws IOException {
		initialize();
		ReadFromFile();
		GetFirst();
		System.out.println("first set OK,test create closure");
		set_goto_action();
	}
	
}
