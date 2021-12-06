package pt.iscte.pcd.directory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class RequestsDirectory extends Thread {

    private ArrayList<String> nodesList;
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public RequestsDirectory(ArrayList<String> nodesList, Socket socket) {
        this.nodesList = nodesList;
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            String aux = in.readLine();
            String[] received = aux.split(" ");
            if (received[0].equals("INSC") && received.length == 3) {
                insert(aux);
            } else {
                if (received[0].equals("nodes") && received.length == 1) {
                    System.out.println("------- Solicitação de NOS-------");
                    for (String s : nodesList) {
                        out.println(s);
                    }
                    out.println("END");
                } else {
                    System.err.println(" - ERRO MSG: " + aux);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("---------------------------------");
    }

    //
    private void insert(String newNode) {
        boolean exist = false;
        if (nodesList.size() == 0) {
            System.out.println(" - Registo: " + newNode);
            nodesList.add(newNode);
            out.println("OK");
        } else {
            for (int i = 0; i < nodesList.size(); i++) {
                if (newNode.equals(nodesList.get(i))) {
                    exist = true;
                    System.out.println(" - N Registado: " + newNode);
                    out.println("NOK");
                    break;
                }
            }
            if (!exist) {
                nodesList.add(newNode);
                System.out.println(" - Registo: " + newNode);
                out.println("OK");
            }
        }

    }
}
