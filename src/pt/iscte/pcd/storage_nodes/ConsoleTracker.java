package pt.iscte.pcd.storage_nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleTracker extends Thread {

    public ConsoleTracker() {
	// Não sei se será necessário efetuar algo ao nivel do construtor
    }

    @Override
    public void run() {
		System.out.println("Consola Ativada:");
		System.out.println(" ");
		System.out.println("Digite ERROR e o numero do byte");
		while (true) {
			try {
				BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
				String command = input.readLine();
				//System.out.println(command);
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void corruptByte(int nByte){
		newNode
	}

}
