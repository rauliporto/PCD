package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class RequestsAnswerer extends Thread {
    private final Socket nodeSocket;
    private CloudByte[] file;
    private final NodeSocket socketDirectory;


    public RequestsAnswerer(CloudByte[] file, Socket nodeSocket, NodeSocket socketDirectory) throws IOException {
        this.file = file;
        this.nodeSocket = nodeSocket;
        this.socketDirectory = socketDirectory;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(nodeSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(nodeSocket.getOutputStream());
            ByteBlockRequest received = (ByteBlockRequest) in.readObject();
            if (received != null) {
                CloudByte[] sendRequested = getSubCloudBytes(received);
                out.writeObject(sendRequested);
                out.reset();
            } else {
                System.out.println("Erro ao obter informação do outro node");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public CloudByte[] getSubCloudBytes( ByteBlockRequest received) throws IOException {
        CloudByte[] aux = new CloudByte[received.getLength()];
        for (int i = 0; i < aux.length; i++) {
            if(!file[i + received.getStartIndex()].isParityOk()) {
                System.out.println("Erro detetado no byte: " + file[i + received.getStartIndex()].toString());
                file[i + received.getStartIndex()] = getRealCloudByte(new ByteBlockRequest(i + received.getStartIndex(),1));
                System.out.println("Erro detetado no byte: " + file[i + received.getStartIndex()].toString());
            }
            aux[i] = file[i + received.getStartIndex()];
        }
        return aux;
    }

    private CloudByte getRealCloudByte(ByteBlockRequest received) throws IOException {
            CountDownLatch cdl = new CountDownLatch(2);
            ArrayList<String[]> nodesList = socketDirectory.getNodes();
            ArrayList<CloudByte> results = new ArrayList<CloudByte>();
            for (int i = 0; i < nodesList.size(); i++) {
                (new ByteErrorCorrection(nodesList.get(i)[1], nodesList.get(i)[2], results, received, cdl)).start();
            }
            try {
                cdl.await();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(results.get(0).equals(results.get(1)))
                return results.get(0);
            else
                if (results.get(1).equals(results.get(2)))
                    return results.get(1);
          else
                    return results.get(0);
        }



}
