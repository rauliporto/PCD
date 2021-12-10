package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.util.concurrent.*;

public class NodeSchedule extends Thread {
    private final CloudByte[] file;
    private final NodeSocket socketDirectory;

    public NodeSchedule(CloudByte[] file, NodeSocket socketDirectory) {
        this.file = file;
        this.socketDirectory = socketDirectory;
    }

    @Override
    public void run() {
        System.out.println("---------------------------------");
        System.out.println("------------ Tarefas ------------");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.schedule((new ByteErrorChecker(file, socketDirectory,1)), 1, TimeUnit.SECONDS);
        System.out.println("- Iniciado 1º Processo          -");
        scheduledExecutorService.schedule((new ByteErrorChecker(file, socketDirectory,2)), 5, TimeUnit.SECONDS);
        System.out.println("- Iniciado 2º Processo          -");

    }


}
