package grammar;

public class action {
	//mark Ϊ0��ʾ�ܾ���Ϊ1ʱ��ʾs�� Ϊ2��ʾr��Ϊ3��ʾ����acc
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
