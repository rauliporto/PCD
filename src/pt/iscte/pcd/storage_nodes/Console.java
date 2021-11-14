package pt.iscte.pcd.storage_nodes;

/*
Class para o funcionamento da consola que fica
sempre em espera de forma a obter os comandos.
 */

import pt.iscte.pcd.CloudByte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console extends Thread {

    private CloudByte[] cloudArrayFile;

    public Console(CloudByte[] cloudArrayFile) {
        this.cloudArrayFile = cloudArrayFile;
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

    private void corruptByte(int nByte) {
        System.out.println(cloudArrayFile[nByte - 1].toString());
        cloudArrayFile[nByte - 1].makeByteCorrupt();
        System.out.println("Corrompido");
        System.out.println(cloudArrayFile[nByte - 1].toString());
    }

}
