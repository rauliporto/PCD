package pt.iscte.pcd.storage_nodes;

import java.io.Serializable;

public class ByteBlockRequest implements Serializable {
    private int startIndex,length;

    public ByteBlockRequest(int startIndex){
        this.startIndex = startIndex;
        this.length = 100;

    }
    public ByteBlockRequest(int startIndex, int length){
        this.startIndex = startIndex;
        this.length = length;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getLength() {
        return length;
    }
}
