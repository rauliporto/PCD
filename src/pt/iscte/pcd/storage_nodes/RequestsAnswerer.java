package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestsAnswerer extends Thread {
    private ServerSocket nodeServerSocket;
    private CloudByte[] file;

    public RequestsAnswerer(CloudByte[] file, String nodePort) throws IOException {
        this.file = file;
        nodeServerSocket = new ServerSocket(Integer.parseInt(nodePort));
    }

    @Override
    public void run() {
        try {
            Socket nodeSocket = nodeServerSocket.accept();
            ObjectInputStream in = new ObjectInputStream(nodeSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(nodeSocket.getOutputStream());
            while (true) {
                ByteBlockRequest received = (ByteBlockRequest) in.readObject();
                if (received != null) {

                    CloudByte[] sendRequested = getSubCloudBytes(received);
                    out.writeObject(sendRequested);

                } else {
                    System.out.println("Erro ao obter informação do outro node");
                }
                // Não sei se é necessário para dar reset ao conteudo das vars
                out.reset();
                in.reset();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public CloudByte[] getSubCloudBytes(ByteBlockRequest received) {
        CloudByte[] aux = new CloudByte[received.getLength()];
        for (int i = 0; i < aux.length; i++) {
            aux[i] = file[i + received.getStartIndex()];
        }
        return aux;
    }

}
