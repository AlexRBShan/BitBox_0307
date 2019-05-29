package unimelb.bitbox;

import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager;

import java.util.LinkedList;
import java.util.Queue;

import unimelb.bitbox.ConnectToPeer;

public class TCPClient extends Thread{
	private FileSystemManager fileSystemManager;
	public static Queue<HostPort> peerToConnect = new LinkedList<HostPort>();;
	
	public TCPClient(FileSystemManager fileSystemManager) {
		this.fileSystemManager = fileSystemManager;
	}
	
	@Override
	public void run() {
		for(String host: PeerMaster.peerArray) {
			HostPort peer = new HostPort(host);
			peerToConnect.offer(peer);
		}
		// wait sometime for server to start
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Initial Connection to all peers
		while(true) {
			while(!peerToConnect.isEmpty()) {
				HostPort peer = peerToConnect.poll();
				ConnectToPeer newConnection = new ConnectToPeer(this.fileSystemManager, peer);
				newConnection.start();
		}
		}
	}
	
}
