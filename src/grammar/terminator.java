package grammar;
import java.util.*;

public class terminator {
	String val;
	Vector<terminator> FIRST_SET;
	public boolean isterm; 
	public terminator(String _val){
		val=_val;
		isterm=false;
		FIRST_SET=new Vector<terminator>();
	}
}
