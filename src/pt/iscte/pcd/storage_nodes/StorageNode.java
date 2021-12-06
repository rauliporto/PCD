package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class StorageNode {

    private static final int FILE_SIZE = 1000000;

    private RequestsAnswerer requestsAnswerer; //Uma thread responsável por responder a pedidos dos outros nodes
    private ByteErrorChecker checker;
    private final String nodeAdress;
    private final String directoryPort;
    private final String nodePort;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private CloudByte[] file;

    //Construtor
    public StorageNode(String directoryAdress, String directoryPort, String nodePort, String fileName) throws IOException {
        this.nodeAdress = directoryAdress;
        this.directoryPort = directoryPort;
        this.nodePort = nodePort;
        System.out.println("---------------------------------");
        directoryRegister();


        if (fileName != null) {
            convertToCloudBytes(fileName);
            System.out.println("Documento Convertido");
        }
        else {
            System.out.println("A Obter o Ficheiro");
            this.file = new CloudByte[FILE_SIZE];
            createRequestDownload();
        }
        startConsole();
        startServerSocket(nodePort);
    }

    //Lancamento dos ByteBlockRequest ref ao ficheiro que pretende fazer o download e das threads para irem buscar os CloudBytes
    private void createRequestDownload() throws IOException {
        ArrayList<String[]> nodesList =  getNodesList();
        PoolRequests poolRequests = new PoolRequests(FILE_SIZE / 100);
        for(int i=0; i < nodesList.size(); i++){
                System.out.println("Thread Criada para NO " + nodesList.get(i)[1] + "na porta " + nodesList.get(i)[2]);
                (new RequestsDownload(nodesList.get(i)[1],nodesList.get(i)[2], poolRequests, file)).start();
        }
    }

    // Lancamento da SocketServer para responnder aos pedidos , pedidos que são executados em Threads à parte
    private void startServerSocket(String nodePort) throws IOException {
        ServerSocket nodeServerSocket = new ServerSocket(Integer.parseInt(nodePort));
        while (true) {
            try {
                Socket nodeSocket = nodeServerSocket.accept();
                RequestsAnswerer receiveConnection = new RequestsAnswerer(file,nodeSocket);
                receiveConnection.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Conversão do ficheiro em CloudBytes
    private void convertToCloudBytes(String fileName) throws IOException {
        byte[] fileArray = Files.readAllBytes(Paths.get(fileName));
        this.file = new CloudByte[fileArray.length];
        for (int i = 0; i < fileArray.length; i++) {
            this.file[i] = new CloudByte(fileArray[i]);
        }

    }

    // Registo no Diretorio
    private void directoryRegister() throws IOException {
        InetAddress url = InetAddress.getByName(nodeAdress);
        socket = new Socket(url, Integer.parseInt(directoryPort));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        out.println("INSC " + nodeAdress + " " + nodePort);
        System.out.println("Registo Efetuado no Diretorio com os seguintes dados " + nodeAdress + " " + nodePort);
    }

    // Inicialização da consola e execucao da mesma
    private void startConsole(){
        (new Console(file)).start();
    }




    // Finalização da socket de contacto com o diretorio
    private void disconnect() throws IOException {
        socket.close();
    }

    // Obter listagem de todos os NOS exepto eu proprio
    public ArrayList<String[]>  getNodesList() throws IOException {
        ArrayList<String[]> nodesList  = new ArrayList<String[]>();
        String[] aux;
        out.println("nodes");
        System.out.println("----------- LISTA NOS -----------");
        while (true) {
            aux = (in.readLine()).split(" ");
            if (aux[0].equals("END")) {
                System.out.println("              FIM                ");
                break;
            } else {
                if (!aux[2].equals(nodePort)) {
                    nodesList.add(aux);
                    System.out.println("Valor " + aux[0] + aux[1] + aux[2]);
                }
            }
        }
        System.out.println("---------------------------------");
        return nodesList;
    }
//metodos da coneccao estaticos da classe

    /* --------------------------------------------------------------------
       ------------------------------  MAIN  ------------------------------
                                   Classe Main
       --------------------------------------------------------------------  */
    public static void main(String[] args) throws IOException {
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
