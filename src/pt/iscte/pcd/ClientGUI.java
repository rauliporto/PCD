package pt.iscte.pcd;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ClientGUI {
	
	private JFrame frame;
	
	public ClientGUI() {
		
		this.frame = new JFrame("Client");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addFrameContent();
		frame.pack();
		frame.setVisible(true); 
	}
	
	private void addFrameContent() {
		
		JTextField textFieldConsulta = new JTextField(20);
		JTextField textFieldComprimento = new JTextField(20);
		JLabel labelConsulta = new JLabel("Posi��o a consultar:");
		JLabel labelComprimento = new JLabel("Comprimento:");
		JButton buttonConsultar = new JButton("Consultar");
		JTextArea textAreaRespostas = new JTextArea(900,200);
		
		buttonConsultar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("O que vai acontecer quando carregar consultar");
				//TODO O QUE ACONTECE QUANDO CARREGO EM CONSULTAR
			}
		});

		frame.setLayout(new FlowLayout());
		Dimension d = new Dimension(900,200);
		frame.setPreferredSize(d);
		frame.add(labelConsulta);
		frame.add(textFieldConsulta);
		frame.add(labelComprimento);
		frame.add(textFieldComprimento);
		frame.add(buttonConsultar);
		frame.add(textAreaRespostas);
		
	}

	public static void main(String[] args) {
		
		ClientGUI frame = new ClientGUI();
		
	}

}
