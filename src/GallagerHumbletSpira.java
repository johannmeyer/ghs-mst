import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GallagerHumbletSpira implements GallagerHumbletSpira_RMI{

    
    public static final int STATUS_FIND = 0;
    public static final int STATUS_FOUND = 1;
    public static final int STATUS_SLEEPING = -1;
    
    private int id;
    
    protected int LN; //Level of the current fragment it is part of
    protected int FN; //Name of the current fragment it is part of
    protected int SN; //State of the node,-1 = sleeping, 0 = find, 1 = found
    protected int in_branch; //ID of edge towards core
    protected int test_edge; //ID of edge checked whether other end in same fragment
    protected Edge best_edge; // candidate for MWOE
    protected int find_count; //Number of report messages expected
    
    protected Queue<Message> message_queue; //Message queue
    
    protected List<Edge> edges; //Keeps track of edges
    	
    public GallagerHumbletSpira(int id, List<Edge> edges) {
        this.id = id;
        this.edges = edges;
        this.message_queue = new LinkedList<Message>();
    }
    
    public void receiveMessage(Message m) {
        m.execute(this);
    }

    public void wakeUp() {
        //TODO
    }
    
    public void sendInitiate(int receiveID, int level, int name, int state) {
        //TODO
    }
    
    public void sendAccept(int receiveID) {
        //TODO
    }
    
    public void sendReject(int receiveID) {
        //TODO
    }
    
    public void test() {
        //TODO
    }
    
    public void report() {
        //TODO
    }
    
    public void change_root() {
        //TODO
    }
    
    public void halt() {
        //TODO
    }
    
    @SuppressWarnings("unused")
	private void println(String message)
    {
        String pidStr = "(" + this.id + ") ";
        System.err.println(pidStr + message);
    }
    
}
