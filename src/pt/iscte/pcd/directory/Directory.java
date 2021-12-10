package pt.iscte.pcd.directory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Directory {

    private final int nodePort;
    private ArrayList<String> nodesList;

    public Directory(int nodePort) {
        this.nodePort = nodePort;
        nodesList = new ArrayList<String>();
    }

    public void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(nodePort);
        System.out.println("-----------------------------------------------");
        System.out.println("------------------ Servidor -------------------");
        System.out.println("-----------------------------------------------");
        while (true) {
            Socket socket = serverSocket.accept();
            (new RequestsDirectory(nodesList, socket)).start();
        }
    }


    public static void main(String[] args) throws IOException {
        if (args.length == 1)
            (new Directory(Integer.parseInt(args[0]))).startServer();
        else {
            System.err.println("------------- Erro nos argumentos -------------");
            System.out.println("-----------------------------------------------");
        }
    }
}
