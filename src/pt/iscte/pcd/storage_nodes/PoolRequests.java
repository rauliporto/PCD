package pt.iscte.pcd.storage_nodes;

import java.util.LinkedList;

public class PoolRequests {

    private LinkedList<ByteBlockRequest> results = new LinkedList<ByteBlockRequest>();

    public synchronized void put(ByteBlockRequest newBlock) {
        results.add(newBlock);
        notifyAll();
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

    public boolean isEmpty(){
        return results.isEmpty();

    }
}