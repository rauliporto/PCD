package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

public class ByteErrorChecker extends Thread {

	private CloudByte[] file;

	public ByteErrorChecker(CloudByte[] file){
		this.file = file;
	}

	@Override
	public void run() {
		while(true) {
			for(int i=0; i < file.length; i++){
				if(!file[i].isParityOk()){
					System.out.println("Erro de paridade!");
					//fazer correção de erros
				}
			}
		}
		
	}

}
