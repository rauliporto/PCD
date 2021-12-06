package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class RequestsDownload extends Thread {

    private String adressServer;
    private String nodePortServer;
    private PoolRequests pool;
    private CloudByte[] file;

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
                InetAddress url = InetAddress.getByName(adressServer);
                Socket socket = new Socket(url, Integer.parseInt(nodePortServer));
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ByteBlockRequest blockRequest = pool.get();

                out.writeObject(blockRequest);
                CloudByte[] cloudBytesArray = (CloudByte[]) in.readObject();
                saveDownloadedCloudBytes(cloudBytesArray, blockRequest);
                counter++;
              //  System.out.println(counter);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        System.out.println("Feito download de " + counter + " blocos");
    }

    public void saveDownloadedCloudBytes(CloudByte[] cloudBytesArray, ByteBlockRequest blockRequest) {
        for (int i = 0; i < blockRequest.getLength(); i++) {
            file[(i + blockRequest.getStartIndex())] = cloudBytesArray[i];
        }
    }
}
