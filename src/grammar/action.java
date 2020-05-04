package grammar;

public class action {
	//mark 为0表示拒绝，为1时表示s， 为2表示r，为3表示接受acc
    int type;
    int val;
    
    public static int reject=0;
    public static int stackpush=1;
    public static int reduction=2;
    public static int accept=3;
    
    public action(int _type,int _val) {
    	type=_type;
    	val=_val;
    }
}
