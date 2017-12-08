import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GallagerHumbletSpira extends UnicastRemoteObject implements GallagerHumbletSpira_RMI{
    
    private static final String naming = "Node-";
    public static final int STATUS_FIND = 0;
    public static final int STATUS_FOUND = 1;
    public static final int STATUS_SLEEPING = -1;
    
    private int id;
    private HashMap<Integer, String> ip_LUT;
    
    protected int LN; //Level of the current fragment it is part of
    protected int FN; //Name of the current fragment it is part of
    protected int SN = STATUS_SLEEPING; //State of the node,-1 = sleeping, 0 = find, 1 = found
    protected int in_branch; //ID of edge towards core
    protected int test_edge; //ID of edge checked whether other end in same fragment
    protected Edge best_edge; // candidate for MWOE
    protected int find_count; //Number of report messages expected
    
    protected Queue<Message> message_queue; //Message queue
    
    protected List<Edge> edges; //Keeps track of edges
    	
    public GallagerHumbletSpira(int id, List<Edge> edges, HashMap<Integer, String> ip_LUT) throws RemoteException {
        this.id = id;
        this.edges = edges;
        this.message_queue = new LinkedList<Message>();
        this.ip_LUT = ip_LUT;
        
        bind();
    }
    
    /**
     * Binding the remote object (stub) in the local registry
     */
    private void bind() {
        try{
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(naming + id, this);
            System.err.println("Node " + id + " ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
    
    public void receiveMessage(Message m) {
        m.execute(this);
    }
    
    private void sendMessage(int destination, Message m) {
        String destName = "//" + ip_LUT.get(destination) + ":1099/" + naming + destination;
        println("sent " + m.getClass() + " to: " + destName);
        try {
            GallagerHumbletSpira_RMI dest = (GallagerHumbletSpira_RMI) Naming.lookup(destName);
            dest.receiveMessage(m);
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        
        
    }
    
    public void sendInitiate(int receiveID, int level, int name, int state) {
        sendMessage(receiveID, new InitiateMessage(id, level, name, state));
    }
    
    public void sendAccept(int receiveID) {
        sendMessage(receiveID, new AcceptMessage(id));
    }
    
    public void sendReject(int receiveID) {
        sendMessage(receiveID, new RejectMessage(id));
    }
    
    public void sendChangeRoot(int receiveID) {
        sendMessage(receiveID, new ChangeRootMessage(id));
    }
    
    public void sendReport(int receiveID, int best_wt) {
        sendMessage(receiveID, new ReportMessage(id, best_wt));
    }
    
    public void sendTest(int receiveID, int level, int name) {
        sendMessage(receiveID, new TestMessage(id, level, name));
    }
    
    public void sendConnect(int receiveID, int level) {
        sendMessage(receiveID, new ConnectMessage(id, level));
    }  

    public void wakeUp() {
        println("Woke up");
        int min_edge_dst = Edge.getMWOE(edges).getDst();
        Edge.getMWOE(edges).setStatus(Edge.IN_MST);
        LN = 0;
        SN = STATUS_FOUND;
        find_count = 0;
        sendConnect(min_edge_dst, LN);
    }
    
    public void test() {
        if(Edge.getMWOE(edges) != null) {
            test_edge = Edge.getMWOE(edges).getDst();
            sendTest(test_edge, LN, FN);
        } else {
            test_edge = Edge.EDGE_NIL;
            report();
        }
    }
    
    public void report() {
        if(find_count == 0 && test_edge == Edge.EDGE_NIL) {
            SN = STATUS_FOUND;
            sendReport(in_branch, best_edge.getWeight());
        }
    }
    
    public void change_root() {
        //TODO
    }
    
    public void halt() {
        //TODO
    	synchronized(System.err){
    		println("*******************************");
    		println("Node " + id);
    		for (Edge adjacent_edge: edges)
    		{
    			if(adjacent_edge.getStatus() == Edge.IN_MST)
    				println (adjacent_edge.toString());
    		}
    		println("################################");
    	}
    }
    
	private void println(String message)
    {
    	
        String pidStr = "(" + this.id + ") ";
        synchronized(System.err){
        System.err.println(pidStr + message);
        }
    }
    
}
