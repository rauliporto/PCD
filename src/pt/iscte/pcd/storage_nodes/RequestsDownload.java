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
        while (!pool.isEmpty()) {
            try {
                InetAddress url = InetAddress.getByName(adressServer);
                Socket socket = new Socket(url, Integer.parseInt(nodePortServer));
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(pool.get());
                CloudByte[] downloadedByteBlock = (CloudByte[]) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }


}
