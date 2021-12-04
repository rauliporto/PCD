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
    private Console console; // consola para responder aos comandos de erro solicitados
    private ByteErrorChecker checker;
    private final String nodeAdress;
    private final String directoryPort;
    private final String nodePort;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private CloudByte[] cloudArrayFile;
    private PoolRequests poolRequests;

    //Construtor
    public StorageNode(String directoryAdress, String directoryPort, String nodePort, String fileName) throws IOException {
        this.nodeAdress = directoryAdress;
        this.directoryPort = directoryPort;
        this.nodePort = nodePort;
        directoryRegister();
        if (fileName != null) {
            System.out.println("Tem ficheiro para converter");
            convertToCloudBytes(fileName);
        }
        else {
            System.out.println("Não tem ficheiro e vai buscar os NOS");
            createRequestDownload();
        }
        startConsole();
        startServerSocket(nodePort);
    }

    private void createRequestDownload() throws IOException {
        ArrayList<String> nodesList =  getNodesList();
        this.cloudArrayFile = new CloudByte[FILE_SIZE];
        this.poolRequests = new PoolRequests(FILE_SIZE/100);
        for(int i=0; i < nodesList.size(); i++){
            String[] aux = nodesList.get(i).split(" ");
            if(!aux[2].equals(nodePort)){
            //    CRIAR THEREAD
                System.out.println("NO: 1-" + aux[0] + " 2-" + aux[1] + " 3-" + aux[2]);
                RequestsDownload requestsDownload = new RequestsDownload(aux[1],aux[2], poolRequests, cloudArrayFile);
                requestsDownload.start();
            }
        }

    }

    private void startServerSocket(String nodePort) throws IOException {
        ServerSocket nodeServerSocket = new ServerSocket(Integer.parseInt(nodePort));
        while (true) {
            try {
                Socket nodeSocket = nodeServerSocket.accept();
                RequestsAnswerer receiveConnection = new RequestsAnswerer(cloudArrayFile,nodeSocket);
                receiveConnection.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Convert o ficheiro em array de cloudbytes
    private void convertToCloudBytes(String fileName) throws IOException {
        byte[] fileArray = Files.readAllBytes(Paths.get(fileName));
        cloudArrayFile = new CloudByte[fileArray.length];
        for (int i = 0; i < fileArray.length; i++) {
            cloudArrayFile[i] = new CloudByte(fileArray[i]);
        }
        System.out.println("Ficheiro totalmente convertido");
    }

    // Ligação ao diretorio e inicio da consola, da GUI, pedidos e solicitação de pedidos
    private void directoryRegister() throws IOException {
        connect();
        register();
        System.out.println("Registo Efetuado no Diretorio");
    }

    private void startConsole(){
       console = new Console(cloudArrayFile);
        console.start();
    }


    // Criaçao de socket para contacto com servidor
    private void connect() throws IOException {
        InetAddress url = InetAddress.getByName(nodeAdress);
        socket = new Socket(url, Integer.parseInt(directoryPort));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    // Finalização da socket de contacto com servidor
    private void disconnect() throws IOException {
        socket.close();
    }

    // Registar nó no diretorio
    public void register() throws IOException {
        String register_string = "INSC 127.0.0.1 " + nodePort;
        System.out.println(" Valor da msg a enviar: " + register_string);
        out.println(register_string);
    }


    // obter a lista de todos os Nós atraves da solicitacao ao diretorio - ESBOÇO
    public ArrayList<String>  getNodesList() throws IOException {
        ArrayList<String> nodesList  = new ArrayList<String>();
        String aux;
        out.println("nodes");
        System.out.println("A Obter os NOS do diretorio:");
        while (true) {
            aux = in.readLine();
            if (aux.equals("END")) {
                System.out.println("Não existem mais NOS");
                break;
            } else {
                nodesList.add(aux);
                System.out.println(aux);
            }
        }
        System.out.println("Finalizada a lista");
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
