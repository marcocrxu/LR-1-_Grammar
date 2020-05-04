package grammar;

public class project {
	public int pointpos;
	public boolean reduct;
	public expression expr;
	public terminator term;
	public project(expression _expr,terminator _term){
		pointpos=0;
		expr=_expr;
		reduct=false;
		term=_term;
	}
	
	public project(expression _expr,terminator _term,int _pointpos) {
		expr=_expr;
		pointpos=_pointpos;
		reduct=false;
		term=_term;
	}
	
	public project(expression _expr,terminator _term,int _pointpos,boolean _reduct) {
		expr=_expr;
		pointpos=_pointpos;
		reduct=_reduct;
		term=_term;
	}
	
	public boolean equal(project p) {
		if(p.pointpos==pointpos&&
				p.reduct==reduct&&
				p.term.equals(term)&&
				p.expr.equals(expr)) {
			return true;
		}
		else {
			return false;
		}
			
	}
	
}
