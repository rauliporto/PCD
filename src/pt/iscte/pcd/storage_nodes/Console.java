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
        boolean close = false;
        while (!close) {
            System.out.println(" ");
            System.out.println("Digite:");
            System.out.println("        ERROR X (onde X é o numero do byte)");
            System.out.println("        ou ");
            System.out.println("        EXIT (para sair) ");
            String command = in.nextLine();
            if (command != null) {
                String[] result = command.split(" ");
                 // Byte do erro a injetar
                if ((result.length == 2) && (result[0].equals("ERROR"))) {
                    int byteError =  Integer.parseInt(result[1]);
                    if((byteError > 0 ) && (byteError <= cloudArrayFile.length)) {
                        System.out.println("Valor do byte atual " + cloudArrayFile[byteError - 1].getValue());
                        cloudArrayFile[byteError - 1].makeByteCorrupt();
                        System.out.println("Valor do byte após ser corrompido " + cloudArrayFile[byteError - 1].getValue());
                    }
                    else
                        System.out.println("Valor do Byte Inválido");
                    } else {
                    if ((result.length == 1) && (result[0].equals("EXIT"))) {
                        close = true;
                        System.out.println("A encerrar a consola");
                    } else
                        System.out.println("Comando inválido, por favor verifique os dados introduzidos");
                }

            } else
                System.out.println("Nenhum comando detetado");
        }
    }
}
