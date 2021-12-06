package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StorageNode {

    private static final int FILE_SIZE = 1000000;
    private CloudByte[] file;
    private final NodeSocket socketDirectory;

    public StorageNode(String directoryAdress, String directoryPort, String nodePort, String fileName) throws IOException, InterruptedException {
        this.socketDirectory = new NodeSocket(directoryAdress, directoryPort, nodePort);
        System.out.println("---------------------------------");
        socketDirectory.register();
        if (fileName != null) {
            convertToCloudBytes(fileName);
            System.out.println("Documento Convertido");
        } else {
            System.out.println("A Obter o Ficheiro");
            createRequestDownload();
        }
        startConsole();
        startServerSocket(nodePort);
    }

    // Obtenção do ficheiro através dos outros Nodes
    private void createRequestDownload() throws IOException, InterruptedException {
        this.file = new CloudByte[FILE_SIZE];
        PoolRequests poolRequests = new PoolRequests(FILE_SIZE / 100);
        ArrayList<String[]> nodesList = socketDirectory.getNodes();
        for (int i = 0; i < nodesList.size(); i++) {
            System.out.println("Thread NO# " + nodesList.get(i)[1] + ":" + nodesList.get(i)[2]);
            (new RequestsDownload(nodesList.get(i)[1], nodesList.get(i)[2], poolRequests, file)).start();
        }
        System.out.println("---------------------------------");
        while (!poolRequests.isEmpty()) {
            Thread.sleep(500);
        }
    }

    // SocketServer para responder aos pedidos
    private void startServerSocket(String nodePort) throws IOException {
        ServerSocket nodeServerSocket = new ServerSocket(Integer.parseInt(nodePort));
        while (true) {
            try {
                Socket nodeSocket = nodeServerSocket.accept();
                (new RequestsAnswerer(file, nodeSocket, socketDirectory)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Conversão do ficheiro em CloudBytes
    private void convertToCloudBytes(String fileName) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(fileName));
        this.file = new CloudByte[fileBytes.length];
        for (int i = 0; i < fileBytes.length; i++) {
            this.file[i] = new CloudByte(fileBytes[i]);
        }

    }

    // Inicialização da Consola do Node
    private void startConsole() {
        (new Console(file)).start();
    }

    // --------------------------------------------------------------------
    // ------------------------------  MAIN  ------------------------------
    // --------------------------------------------------------------------

    public static void main(String[] args) throws IOException, InterruptedException {
        if ((args.length == 3) || (args.length == 4)) {
            StorageNode newNode;
            if (args.length == 3) {
                newNode = new StorageNode(args[0], args[1], args[2], null);
            } else {
                newNode = new StorageNode(args[0], args[1], args[2], args[3]);
            }
        } else {
            System.err.println("Argumentos Inválidos");
        }
        while (true) {
            // Verficar esta parte
        }
    }
}
