package pt.iscte.pcd.client;

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

    private JFrame frame;
    private String nodeAdress;
    private String nodePort;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String nodeAdress, String nodePort) {
        this.frame = new JFrame("Client");
        this.nodePort = nodePort;
        this.nodeAdress = nodeAdress;
		System.out.println("Node a ligar IP: " + nodeAdress + " na porta " + nodePort);
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
					requestBytes(1,2);
				} catch (IOException ex) {
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

    private void requestBytes(int size, int position) throws IOException {
        InetAddress url = InetAddress.getByName(nodeAdress);
        Socket socket = new Socket(url, Integer.parseInt(nodePort));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        String requestString = "REQ " + nodeAdress + " " + nodePort + " " + size + " " + position;
        System.out.println(" Valor da msg a enviar: " + requestString);
        out.println(requestString);

    }

    public static void main(String[] args) {
		Client frame;
        if (args[0] != null && args[1] != null)
            frame = new Client(args[0], args[1]);

    }

}
