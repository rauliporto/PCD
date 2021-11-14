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

public class StorageNode {

    private RequestsAnswerer requestsAnswerer; //Uma thread responsável por responder a pedidos dos outros nodes
    private Console console;
    private ByteErrorChecker checker;
    private final String nodeAdress;
    private final String directoryPort;
    private final String nodePort;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    private CloudByte[] cloudArrayFile;

    //Construtor - se argumento diferente de nullo converte para cloudbytes, caso contrario ....
    public StorageNode(String directoryAdress, String directoryPort, String nodePort, String fileName) throws IOException {
        this.nodeAdress = directoryAdress;
        this.directoryPort = directoryPort;
        this.nodePort = nodePort;
        if (fileName != null)
            convertToCloudBytes(fileName);
        console = new Console(cloudArrayFile);
        console.start();

    }

    public StorageNode(String nodeAdress, String directoryPort, String nodePort) {
        this.nodeAdress = nodeAdress;
        this.directoryPort = directoryPort;
        this.nodePort = nodePort;
        console = new Console(cloudArrayFile);
        console.start();
    }


    private void convertToCloudBytes(String fileName) throws IOException {
        byte[] fileArray = Files.readAllBytes(Paths.get(fileName));
        cloudArrayFile = new CloudByte[fileArray.length];
        for (int i = 0; i < fileArray.length; i++) {
            cloudArrayFile[i] = new CloudByte(fileArray[i]);
        }
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
        System.out.println(register_string);
        out.println(register_string);
       // String str = in.readLine();
       // System.out.println(str);
 // Está a faltar algo pois fica preso aqui o codigo - perguntei como funciona o diretorio para perceber o que é necessário
    }


    // obter a lista de todos os Nós atraves da solicitacao ao diretorio - ESBOÇO
    // Questionei o que responde o diretorio, se um array ou string por cada NÓ
    public String[] getNodesList() throws IOException {
        connect();
        String[] list = new String[10];
        int i = 0;
        while (true) {
            if (("valor" == "end") || ("valor" == "null"))
                break;
            else
                list[i] = "valor";
        }
        disconnect();
        return list;

    }


    // Obter ou converter a listagem de dados existentes no Nó para converter para Cloudbyte
    public void getPresentData() {


    }

    // Obter listagem de dados existentens em outro Nó
    public String[] getFileList(String nome_do_no_a_definir) {
        String[] fileList = new String[10];
        return fileList;

    }

    // Fase 3 - Mecanismos de injeção de erros



    public static void main(String[] args) throws IOException, InterruptedException {


        //Iniciação do Nó
        StorageNode newNode = new StorageNode("127.0.0.1", "8080", "8081", "data.bin");
        newNode.connect();
        newNode.register();




        // Iniciação da Consola


        //Iniciação do Server para GUI



        // while true
        while(true)
        Thread.sleep(300);

    }

}
