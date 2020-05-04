package grammar;
import java.util.*;

public class expression {
	terminator left;
	Vector<terminator> right;
	public int value;
	public expression(terminator _left,Vector<terminator> _right){
		value=0;
		left=_left;
		right=_right;
	}
	public expression(terminator _left){
		left=_left;
		right=new Vector<terminator>();
		value=0;
	}
}
