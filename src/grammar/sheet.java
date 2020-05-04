package grammar;
import java.util.*;

public class sheet {
	public int linenum; 
	Vector<actionlist> action_sheet;
	
	public sheet(int _linenum) {
		linenum=_linenum;
		action_sheet=new Vector<actionlist>();
	}

	public void addLine(actionlist add) {
		action_sheet.add(add);
	}
	
	public void setLineNum(int x) {
		linenum=x;
	}
}
