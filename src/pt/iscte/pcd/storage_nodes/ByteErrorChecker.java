package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ByteErrorChecker extends Thread {

    private final CloudByte[] file;
    private final NodeSocket socketDirectory;
    private final Lock lock;
    private final int numero;

    public ByteErrorChecker(CloudByte[] file, NodeSocket socketDirectory, int numero) {
        this.file = file;
        this.socketDirectory = socketDirectory;
        this.lock = new ReentrantLock();
        this.numero = numero;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (int i = 0; i < file.length; i++) {
                    if (!file[i].isParityOk()) {
                        System.out.println("- Thread #" + numero + " encontrou erro no byte " + i + " -");
                        System.out.println("---------------------------------");
                        if (lock.tryLock(0, TimeUnit.SECONDS)) {
                            try {
                                lock.lock();
                                file[i] = getRealCloudByte(new ByteBlockRequest(i, 1));
                                System.out.println("- Corrigido pela Thread #" + numero + "      -");
                                System.out.println("---------------------------------");
                            } finally {
                                lock.unlock();
                            }

                        }
                    }
                }
                Thread.sleep(5000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private CloudByte getRealCloudByte(ByteBlockRequest received) throws IOException {
        ArrayList<String[]> nodesList = socketDirectory.getNodes();
        ArrayList<CloudByte> results = new ArrayList<CloudByte>();
        if (nodesList.size() == 1) {
            CountDownLatch cdl = new CountDownLatch(1);
            (new ByteErrorCorrection(nodesList.get(0)[1], nodesList.get(0)[2], results, received, cdl)).start();
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (nodesList.size() > 1) {
            CountDownLatch cdl = new CountDownLatch(2);
            for (int i = 0; i < nodesList.size(); i++) {
                (new ByteErrorCorrection(nodesList.get(i)[1], nodesList.get(i)[2], results, received, cdl)).start();
            }
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (results.get(0).equals(results.get(1)))
                return results.get(0);
            else
                return results.get(2); // nunca vai acontecer esta situação
        }
        System.err.println(" Sem Nodes para correção do erro");
        return null;
    }
}
