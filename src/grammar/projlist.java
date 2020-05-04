package grammar;
import java.util.*;

public class projlist implements Cloneable{
	Vector<project> proj;
	int val;
	
	public projlist() {
		proj=new Vector<project>();
		val=0;
	}
	
	public projlist(int _val) {
		val=_val;
		proj=new Vector<project>();
	}
	
	
	public projlist clone() {
		projlist newp = null;  
        try{  
            newp = (projlist)super.clone();  
        }catch(CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return newp; 
	}
}
