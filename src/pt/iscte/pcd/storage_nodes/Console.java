package pt.iscte.pcd.storage_nodes;

/*
Class para o funcionamento da consola que fica
sempre em espera de forma a obter os comandos.
 */

import pt.iscte.pcd.CloudByte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Console extends Thread {

    private CloudByte[] cloudArrayFile;

    public Console(CloudByte[] cloudArrayFile) {
        this.cloudArrayFile = cloudArrayFile;
    }

    @Override
    public void run() {
        System.out.println("---------------------------------");
        System.out.println("------------ Consola ------------");
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println(" ");
            System.out.println("Digite:");
            System.out.println("        ERROR X (onde X é o numero do byte)");
            System.out.println("        ou ");
            System.out.println("        EXIT (para sair) ");
            String command = in.nextLine();
            if (command != null) {
                String[] result = command.split(" ");
                if ((result.length == 2) && (result[0].equals("ERROR"))) {
                    System.out.println(("Valor do byte atual " + result[1]).toString());
                    cloudArrayFile[Integer.parseInt(result[1])-1].makeByteCorrupt();
                    System.out.println(("Valor do byte após ser corrompido " + result[1]).toString());
                } else {
                    if ((result.length == 1) && (result[0].equals("EXIT")))
                        System.out.println("A fechar"); // Necessita de fechar as threads todas
                    else
                        System.out.println("Comando inválido, por favor verifique os dados introduzidos");
                }

            } else
                System.out.println("Nenhum comando detetado");
        }
    }
}
