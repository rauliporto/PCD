package pt.iscte.pcd.storage_nodes;

import java.util.LinkedList;

public class PoolRequests {

    private final LinkedList<ByteBlockRequest> results;

    public PoolRequests(int size){
        results = new LinkedList<ByteBlockRequest>();
        for(int i=0; i < size; i++){
            results.add(new ByteBlockRequest(i*100));
        }
        System.out.println("Criado " + results.size() +" ByteBlockRequests");
    }

    public synchronized ByteBlockRequest get() {
        while (results.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return results.removeFirst();
    }

    public synchronized boolean isEmpty(){
        return results.isEmpty();
    }
}