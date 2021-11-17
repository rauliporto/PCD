package pt.iscte.pcd.client;

import pt.iscte.pcd.CloudByte;
import pt.iscte.pcd.storage_nodes.ByteBlockRequest;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class Client {

    private final JFrame frame;
    private final String nodeAdress;
    private final String nodePort;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String nodeAdress, String nodePort) {
        this.frame = new JFrame("Client");
        this.nodePort = nodePort;
        this.nodeAdress = nodeAdress;
        System.out.println("Node a ligar AO IP: " + nodeAdress + " na porta " + nodePort);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addFrameContent();
        frame.pack();
        frame.setVisible(true);
    }

    private void addFrameContent() {

        JTextField textFieldConsulta = new JTextField(20);
        JTextField textFieldComprimento = new JTextField(20);
        JLabel labelConsulta = new JLabel("Posição a consultar:");
        JLabel labelComprimento = new JLabel("Comprimento:");
        JButton buttonConsultar = new JButton("Consultar");
        JTextArea textAreaRespostas = new JTextArea(900, 200);

        buttonConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    textAreaRespostas.setText(requestBytes(Integer.parseInt(textFieldComprimento.getText()), Integer.parseInt(textFieldConsulta.getText())));


                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                System.out.println("O que vai acontecer quando carregar consultar");
                //TODO O QUE ACONTECE QUANDO CARREGO EM CONSULTAR
            }
        });

        frame.setLayout(new FlowLayout());
        Dimension d = new Dimension(900, 200);
        frame.setPreferredSize(d);
        frame.add(labelConsulta);
        frame.add(textFieldConsulta);
        frame.add(labelComprimento);
        frame.add(textFieldComprimento);
        frame.add(buttonConsultar);
        frame.add(textAreaRespostas);
    }

    private String requestBytes(int size, int position) throws IOException, ClassNotFoundException {
        InetAddress url = InetAddress.getByName(nodeAdress);
        Socket socket = new Socket(url, Integer.parseInt(nodePort));

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ByteBlockRequest request = new ByteBlockRequest(position, size);
        out.writeObject(request);
        System.out.println("Solicitado Bloco ao Node");
        CloudByte[] received = (CloudByte[]) in.readObject();
        System.out.println("Informação recebida");
        String receivedBytes = convertToString(received);
        socket.close();
        return receivedBytes;
    }

    private String convertToString(CloudByte[] received) {
        String aux = "";
        for (CloudByte a : received) {
            aux = (aux + " " + a.getValue());
        }
        System.out.println("Valor da Informação recebida: " + aux);
        return aux;
    }

    public static void main(String[] args) {
        Client frame;
        if (args[0] != null && args[1] != null)
            frame = new Client(args[0], args[1]);

    }

}
