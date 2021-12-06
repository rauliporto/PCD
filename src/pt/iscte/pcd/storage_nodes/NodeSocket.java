package pt.iscte.pcd.storage_nodes;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class NodeSocket {
    private final String directoryAdress;
    private final String directoryPort;
    private final String nodePort;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private InetAddress url;
    private final String myIP;

    public NodeSocket(String directoryAdress, String directoryPort, String nodePort) throws IOException {
        this.directoryAdress = directoryAdress;
        this.directoryPort = directoryPort;
        this.nodePort = nodePort;
        this.myIP = (InetAddress.getLocalHost()).getHostAddress();
    }

    public void register() throws IOException {
        connect();
        out.println("INSC " + myIP + " " + nodePort);
        if((in.readLine()).equals("OK"))
            System.out.println(" - Registado: " + myIP + " " + nodePort);
        else
            System.out.println(" -Já Registado: " + myIP + " " + nodePort);
        disconnect();
    }

    private void connect() throws IOException {
        url = InetAddress.getByName(directoryAdress);
        socket = new Socket(url, Integer.parseInt(directoryPort));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

    }

    private void disconnect() throws IOException {
        socket.close();
    }

    public ArrayList<String[]> getNodes() throws IOException {
        connect();
        ArrayList<String[]> nodesList = new ArrayList<String[]>();
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
                    System.out.println(" - NO#: " + aux[1] + " " + aux[2]);
                }
            }
        }
        System.out.println("---------------------------------");
        disconnect();
        return nodesList;
    }
}
