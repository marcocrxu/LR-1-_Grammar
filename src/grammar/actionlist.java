package grammar;
import java.util.*;

public class actionlist {
	public Vector<action> list;
	public Map<terminator,action> array;
	public actionlist() {
		array=new HashMap<terminator,action>();
		list=new Vector<action>();
	}
	
	public actionlist(int linenum) {
		array=new HashMap<terminator,action>(linenum);
		list=new Vector<action>(linenum);
	}
}
