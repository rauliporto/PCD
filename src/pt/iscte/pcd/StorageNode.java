package pt.iscte.pcd;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StorageNode {

	//Constante - alterar localização
	private int PORTO = 8080;


	// variaveis da classe
	private Socket socket;
	private CloudByte[] cloudArrayFile;

	//Construtor - se argumento diferente de nullo converte para cloudbytes, caso contrario ....
	public StorageNode(String fileName) throws IOException {
		//

		if(fileName != null)
		convertToCloudBytes(fileName);


		// TODO Auto-generated constructor stub
	}




	private void convertToCloudBytes(String fileName) throws IOException {
		byte[] fileArray = Files.readAllBytes(Paths.get(fileName));
		for (int i = 0; i < fileArray.length; i++ ) {
				cloudArrayFile[i] = new CloudByte(fileArray[i]);
		}
	}
	private void connect() throws IOException {
		InetAddress url = InetAddress.getByName("localhost");
		socket = new Socket(url,PORTO);

	}

	private void disconnect() throws IOException {
		socket.close();

	}


	//registar nó no diretorio
	public void register() throws IOException {
		String register_string = "INSC 127.0.0.1 " + PORTO;
		System.out.println(register_string);
		PrintWriter register = new PrintWriter (new BufferedWriter (new OutputStreamWriter ( socket.getOutputStream ())) , true );
		register.println(register_string);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		String str = in.readLine();
	 	System.out.println(str);
		 register.println("end");


	}

	
	// obter a lista de todos os N�s atrav�s da solicita��o ao diretorio
	public String[] getNodesList() throws IOException {

		connect();
		String[] list = new String[10];
		int i = 0;

		while(true) {

			if (("valor" == "end") || ( "valor" == "null"))
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
	public static void main (String[] args) throws IOException {

		StorageNode novo_no = new StorageNode(args[1]);
		novo_no.register();

	}
	
}
