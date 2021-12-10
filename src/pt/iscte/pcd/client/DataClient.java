package pt.iscte.pcd.client;

import pt.iscte.pcd.CloudByte;
import pt.iscte.pcd.storage_nodes.ByteBlockRequest;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class DataClient {

    private final JFrame frame;
    private final String nodeAdress;
    private final String nodePort;
    private BufferedReader in;
    private PrintWriter out;

    public DataClient(String nodeAdress, String nodePort) {
        this.frame = new JFrame("Client GUI StorageNode");
        this.nodePort = nodePort;
        this.nodeAdress = nodeAdress;
        System.out.println("-------------------------------------------");
        System.out.println("------------- GUI StorageNode -------------");
        System.out.println("-------------------------------------------");
        System.out.println("- Ligado ao Node: " + nodeAdress + ":" + nodePort + "          -");
        System.out.println("-------------------------------------------");
        addFrameContent();

    }

    private void addFrameContent() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JTextField textFieldIndex = new JTextField(20);
        JTextField textFieldSize = new JTextField(20);
        JLabel labelIndex = new JLabel("Posição a consultar:");
        JLabel labelSize = new JLabel("Comprimento:");
        JButton buttonSearch = new JButton("Consultar");
        JTextArea textAreaReply = new JTextArea(800,80);
        textAreaReply.setEditable(true);
        textAreaReply.setText("Introduza a posição e o tamanho a consultar");
        textFieldIndex.setText("1");
        textFieldSize.setText("1");
        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    textAreaReply.setText(requestBytes(Integer.parseInt(textFieldSize.getText()), Integer.parseInt(textFieldIndex.getText())));
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.setLayout(new FlowLayout());
        Dimension d = new Dimension(900, 200);
        frame.setPreferredSize(d);
        frame.add(labelIndex);
        frame.add(textFieldIndex);
        frame.add(labelSize);
        frame.add(textFieldSize);
        frame.add(buttonSearch);
        frame.add(textAreaReply);
        frame.pack();
        frame.setVisible(true);
    }

    private String requestBytes(int size, int position) throws IOException, ClassNotFoundException {
        InetAddress url = InetAddress.getByName(nodeAdress);
        Socket socket = new Socket(url, Integer.parseInt(nodePort));

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        out.writeObject(new ByteBlockRequest(position-1, size));
        System.out.println("- Solicitado Bloco ao Node                -");
        System.out.println("-                                         -");
        CloudByte[] received = (CloudByte[]) in.readObject();
        System.out.println("- Informação recebida                     -");
        System.out.println("-                                         -");
        socket.close();
        return convertToString(received);
    }

    private String convertToString(CloudByte[] received) {
        String aux = "";
        for (CloudByte a : received) {
            aux = (aux + " " + a.getValue() + "[" + (!a.isParityOk()?"NOK":"OK") + "]" );
        }
        System.out.println("- Valor da Informação recebida: " + aux);
        System.out.println("-------------------------------------------");
        return aux;
    }

    public static void main(String[] args) {
        DataClient frame;
        if (args[0] != null && args[1] != null)
            frame = new DataClient(args[0], args[1]);
    }

}
