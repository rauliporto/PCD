package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class StorageNode {

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
    private ArrayList<String> nodesList;


    //Construtor
    public StorageNode(String directoryAdress, String directoryPort, String nodePort, String fileName) throws IOException {
        this.nodeAdress = directoryAdress;
        this.directoryPort = directoryPort;
        this.nodePort = nodePort;
        if (fileName != null) {
            System.out.println("Tem ficheiro e converteu-o");
            start();
            convertToCloudBytes(fileName);
        } else {
            System.out.println("Não tem ficheiro e vai buscar os NOS");
            start();
            getNodesList();
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
    private void start() throws IOException {
        connect();
        register();
        console = new Console();
        console.start();
        System.out.println("Registo Efetuado no Diretorio");
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
    public void getNodesList() throws IOException {
        nodesList = new ArrayList<String>();
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
    }


    // Obter ou converter a listagem de dados existentes no Nó para converter para Cloudbyte
    public void getPresentData() {


    }

    // Obter listagem de dados existentens em outro Nó
    public String[] getFileList(String nome_do_no_a_definir) {
        String[] fileList = new String[10];
        return fileList;

    }

    /* --------------------------------------------------------------------
       ------------------------------ CONSOLE -----------------------------
                                   Classe Aninhada
       --------------------------------------------------------------------  */
    public class Console extends Thread {
        public Console() {
        }

        @Override
        public void run() {
            System.out.println("Consola Ativada:");
            System.out.println(" ");
            System.out.println("Digite ERROR e o numero do byte");
            Scanner in = new Scanner(System.in);
            while (true) {
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                String command = null;
                try {
                    command = input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(command);
                if (command != null) {
                    String[] result = command.split(" ");
                    if ((result.length == 2) && (result[0].equals("ERROR"))) {
                        System.out.println("Dados corretos");
                        System.out.println(("A Injetar erro no byte " + result[1]));
                        corruptByte(Integer.parseInt(result[1]));
                    } else
                        System.out.println("Comando inválido, por favor verifique os dados introduzidos");
                } else
                    System.out.println("Comando inválido, por favor verifique os dados introduzidos");
            }
        }

        private void corruptByte(int nByte) {
            System.out.println(cloudArrayFile[nByte - 1].toString());
            cloudArrayFile[nByte - 1].makeByteCorrupt();
            System.out.println("Byte Corrompido");
            System.out.println(cloudArrayFile[nByte - 1].toString());
        }

    }

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
