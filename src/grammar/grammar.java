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
	public static expression first_expression;	//�� ReadFromFile() �����г�ʼ��
	public static int linenum;		//action���goto��Ŀ��
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
		/*���ı���ÿһ�б��浽���ݽṹ����������*/
		List<String> list = new ArrayList<String>();
		InputStreamReader read = new InputStreamReader(new FileInputStream(filename));// ���ǵ������ʽ
		BufferedReader bufferedReader = new BufferedReader(read);
		String line="";
		while ((line = bufferedReader.readLine()) != null)
        {
            list.add(line);
        }
		bufferedReader.close();
		/*��ʼ���﷨�Ļ�����Ϣ*/
		initialize();
		/*����õ�����Ϣ*/
		/*step1 ��ȡ���еķ��ս��*/
		for(String temp:list) {
			String [] word=temp.split(" ");
			if(isNonTerm(word[0])==null) {
				terminator new_term=new terminator(word[0]);
				new_term.isterm=false;
				non_term.add(new_term);
			}
		}
		/*step2 ������е��ս�������ս���ļ�������ȥ*/
		for(String temp:list) {
			String [] word=temp.split(" ");
			for(int i=1;i<word.length;i++) {
				/*ֻ�в��Ƿ��ս����֮ǰû�б����ȥ�Ĳ���Ҫ��ӽ�ȥ*/
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
		/*step3 �����е���Ϊ���ʽʹ��expression��洢����*/
		int count=0;
		for(String temp:list) {
			String [] word=temp.split(" ");
			terminator left=isNonTerm(word[0]);
			expression new_expr=new expression(left);
			for(int i=1;i<word.length;i++) {
				terminator t;
				if((t=isTerm(word[i]))==null) {
					/*��һ�����ս��*/
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
		/*step4 ������������Ŀ����
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
		/*��ȡ�����ս����first��*/
		/*�����ս����fist���Ͼ������Լ�*/
		for(terminator temp:term) {
			temp.FIRST_SET.add(temp);
		}
		/*���ս����first����*/
		boolean flag=true;		//���Ǻţ����first���ϲ��ٱ仯������ѭ��
		while(flag) {
			flag=false;
			/*����non_term*/
			for(terminator temp_term:non_term) {
				/*�������е��Ƶ�ʽ*/
				for(expression temp_expr:expr) {
					/*�������һ���Ǹ��ַ�*/
					if(temp_expr.left==temp_term) {
						/*����Ƶ�ʽ���ұߵĵ�һ�����ս������FIRST����û�б������*/
						if(temp_expr.right.firstElement().isterm
							&&temp_term.FIRST_SET.contains(temp_expr.right.elementAt(0))==false) {
							temp_term.FIRST_SET.add(temp_expr.right.elementAt(0));
							flag=true;
						}
						else if(temp_term.isterm==false) {
							/*�����ұߵ�һ�����ս����first����*/
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
		/*����һ�����е���Ŀ���������е���Ŀ*/
		projlist result=new projlist();
		
		/*���ֱ���ǿ��Թ�Լ��״̬*/
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
				/*����ǿ��Թ�Լ��״̬*/
				if(temp_project.reduct) {
					continue;
				}
				/*�����һ�����ս��*/
				if(temp_project.expr.right.elementAt(temp_project.pointpos).isterm) {
					continue;
				}
				/*������һ�����ŵ����ݣ�which must be a non_term*/
				terminator next_term=temp_project.expr.right.elementAt(temp_project.pointpos);
				/*�����λ�ڵ�����2����λ��*/
				if(temp_project.pointpos==temp_project.expr.right.size()-1) {
					/*�����б��ʽ�����ҵ���Ӧ����ߵķ��ս���ǵ����ķ��ŵ�*/
					for(expression temp_expr:expr) {
						if(temp_expr.left.equals(next_term)) {
							/*����һ�����ʽ����*/
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
					/*��������ڵ����ڶ�����λ��*/
					for(expression temp_expr:expr) {
						if(temp_expr.left.equals(next_term)) {
							/*��������Ƶ�ʽ����Ҫ����������һ���ַ���ʲô*/
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
		/*�﷨���ս��*/
		terminator end=new terminator("#");
		end.FIRST_SET.add(end);
		term.add(end);
		linenum=term.size()+non_term.size();
		sheets=new sheet(linenum);
		
		/*�����ʼ��Ŀ����*/
		project begin_proj=new project(first_expression,end);
		projlist first_projlist=createclosure(begin_proj);
		actionlist first_action_list=new actionlist(linenum);
		sheets.addLine(first_action_list);
		projs.add(first_projlist);
		/*����һ����ӵ�action���goto����*/
		boolean flag=true;
		projlist now_projlist=first_projlist;
		int count=0;
		while(flag) {
			flag=false;
			Vector<terminator> next_symbol=new Vector<terminator>();
			/*step1 ��ͳ��������Щ���Ż���Ҫ��ת*/
			for(project temp_project:now_projlist.proj) {
				/*�����һ�����ɹ�Լ����Ŀ*/
				if(temp_project.reduct==false) {
					/*��Ҫ������ӵ�*/
					terminator next_term=temp_project.expr.right.elementAt(temp_project.pointpos);
					if(isIn(next_symbol,next_term)==false) {
						next_symbol.add(next_term);
					}
				}
				/*�����һ�����Թ�Լ����Ŀ*/
				else {
					/*��Ҫ���ö�Ӧ��action��*/
					/*step1 �ҳ����Ǹ�expression*/
					int x=temp_project.expr.value;
					action new_action=new action(action.reduction, x);
					sheets.action_sheet.elementAt(count).array.put(temp_project.term,new_action);
				}
			}
			
			/*step2 ��ÿһ������ת��Ŀ�ıհ�*/
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
