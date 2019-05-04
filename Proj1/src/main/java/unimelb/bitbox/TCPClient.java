package unimelb.bitbox;

import java.util.logging.Logger;
import java.util.ArrayList;

import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.ConnectToPeer;

public class TCPClient extends Thread{
	private static Logger log = Logger.getLogger(TCPClient.class.getName());
	private FileSystemManager fileSystemManager;
	private ArrayList<HostPort> peersToConnect;
	
	public TCPClient(FileSystemManager fileSystemManager, ArrayList<HostPort> peersToConnect) {
		this.fileSystemManager = fileSystemManager;
		this.peersToConnect = peersToConnect;
	}
	
	@Override
	public void run() {
		// wait sometime for server to start
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Initial Connection to all peers
		for(HostPort peer: peersToConnect) {
			ConnectToPeer newConnection = new ConnectToPeer(this.fileSystemManager, peer);
			newConnection.start();
		}
	}
	
}
