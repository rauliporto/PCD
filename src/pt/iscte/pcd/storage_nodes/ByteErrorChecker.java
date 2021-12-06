package pt.iscte.pcd.storage_nodes;

import pt.iscte.pcd.CloudByte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ByteErrorChecker extends Thread {

	private CloudByte[] file;
	private final NodeSocket socketDirectory;

	public ByteErrorChecker(CloudByte[] file, NodeSocket socketDirectory){
		this.file = file;
		this.socketDirectory = socketDirectory;
	}

	@Override
	public void run() {
		while(true) {
			for(int i=0; i < file.length; i++){
				if(!file[i].isParityOk()){
					try {
						file[i] = getRealCloudByte(new ByteBlockRequest(i, 1));
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Erro de paridade!");
					//fazer correção de erros
				}
			}
		}
		
	}
	private CloudByte getRealCloudByte(ByteBlockRequest received) throws IOException {
		CountDownLatch cdl = new CountDownLatch(2);
		ArrayList<CloudByte> results = new ArrayList<CloudByte>();
		ArrayList<String[]> nodesList = socketDirectory.getNodes();
		for (int i = 0; i < nodesList.size(); i++) {
			(new ByteErrorCorrection(nodesList.get(i)[1], nodesList.get(i)[2], results, received, cdl)).start();
		}
		try {
			cdl.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(results.get(0).equals(results.get(1)))
			return results.get(0);
		else
		if (results.get(1).equals(results.get(2)))
			return results.get(1);
		else
			return results.get(0);
	}
}
