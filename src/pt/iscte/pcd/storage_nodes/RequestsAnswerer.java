package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.*;
import java.net.Socket;

public class RequestsAnswerer extends Thread {
    private Socket nodeSocket;
    private CloudByte[] file;

    public RequestsAnswerer(CloudByte[] file, Socket nodeSocket) throws IOException {
        this.file = file;
        this.nodeSocket = nodeSocket;
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


    public CloudByte[] getSubCloudBytes(ByteBlockRequest received) {
        CloudByte[] aux = new CloudByte[received.getLength()];
        for (int i = 0; i < aux.length; i++) {
            if(!file[i + received.getStartIndex()].isParityOk()) {
                System.out.println("Erro detetado no byte: " + file[i + received.getStartIndex()].toString());
                //e faz correção de erros
            }
            aux[i] = file[i + received.getStartIndex()];
        }
        return aux;
    }


}
