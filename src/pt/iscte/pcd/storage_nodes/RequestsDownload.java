package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class RequestsDownload extends Thread {

    private final String adressServer;
    private final String nodePortServer;
    private final PoolRequests pool;
    private final CloudByte[] file;
    private Socket socket;

    public RequestsDownload(String adressServer, String nodePortServer, PoolRequests pool, CloudByte[] file) {
        this.adressServer = adressServer;
        this.nodePortServer = nodePortServer;
        this.pool = pool;
        this.file = file;
    }

    @Override
    public void run() {
        int counter = 0;
        while (!pool.isEmpty()) {
            try {
                ByteBlockRequest blockRequest = pool.get();
                InetAddress url = InetAddress.getByName(adressServer);
                socket = new Socket(url, Integer.parseInt(nodePortServer));
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                out.writeObject(blockRequest);
                CloudByte[] cloudBytesArray = (CloudByte[]) in.readObject();
                saveDownloadedCloudBytes(cloudBytesArray, blockRequest);
                counter++;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Feito download de " + counter + " blocos");
    }

    private void saveDownloadedCloudBytes(CloudByte[] cloudBytesArray, ByteBlockRequest blockRequest) {
        for (int i = 0; i < blockRequest.getLength(); i++) {
            file[(i + blockRequest.getStartIndex())] = cloudBytesArray[i];
        }
    }
}
