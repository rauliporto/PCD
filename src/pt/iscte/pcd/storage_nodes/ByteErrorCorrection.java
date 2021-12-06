package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ByteErrorCorrection extends Thread {

    private final String adressServer;
    private final String nodePortServer;
    private final ArrayList<CloudByte> value;
    private final ByteBlockRequest request;
    private final CountDownLatch cdl;

    public ByteErrorCorrection(String adressServer, String nodePortServer, ArrayList<CloudByte> value, ByteBlockRequest request, CountDownLatch cdl) {
        this.adressServer = adressServer;
        this.nodePortServer = nodePortServer;
        this.value = value;
        this.request = request;
        this.cdl = cdl;
    }


    @Override
    public void run() {
        try {

            InetAddress url = InetAddress.getByName(adressServer);
            Socket socket = new Socket(url, Integer.parseInt(nodePortServer));
            (new ObjectOutputStream(socket.getOutputStream())).writeObject(request);
            value.add(((CloudByte[]) (new ObjectInputStream(socket.getInputStream())).readObject())[0]);
            cdl.countDown();
            System.out.println("Valor coutndown " + cdl.getCount());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
