

public class TestMessage implements Message {

    private int id;
    private int L;
    private int F;
    
    public TestMessage(int id, int L, int F) {
        this.id = id;
        this.L = L;
        this.F = F;
    }
    
    public int getId() {
        return id;
    }
    
    public void execute(GallagerHumbletSpira instance) {
        if(instance.SN == GallagerHumbletSpira.STATUS_SLEEPING) {
            instance.wakeUp();
        }
        instance.println(String.format("Test Message Executing"));
        if(L > instance.LN) {
        	instance.println("Adding test message to queue");
            instance.message_queue.add(this);
        } else {
            if(F != instance.FN) {
                instance.sendAccept(id);
            } else {
            	Edge senderEdge = Edge.getEdge(instance.edges, id);
                if(senderEdge.getStatus() == Edge.UNKNOWN) {
                    senderEdge.setStatus(Edge.NOT_IN_MST);
                }
                
                if(instance.test_edge != id) {
//                	instance.println(String.format("Hello... id: %d, instance_id: %d", id, instance.id ));
                    instance.sendReject(id);
                } else {
                    instance.test();
                }
            }
        }
    }

}
