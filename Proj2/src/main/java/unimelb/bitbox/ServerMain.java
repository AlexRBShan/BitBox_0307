package unimelb.bitbox;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;

import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemObserver;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

import unimelb.bitbox.TCPServer;
import unimelb.bitbox.TCPClient;
import unimelb.bitbox.util.HostPort;

public class ServerMain implements FileSystemObserver {
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	protected FileSystemManager fileSystemManager;
	//Queue<FileSystemEvent> eventQueue = new LinkedList<FileSystemEvent>();
	
	public ServerMain() throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager=new FileSystemManager(PeerMaster.path,this);
		
		// read configurations for peers to connect
		int port = PeerMaster.myPort;
		String[] peersList = PeerMaster.peersList;
		ArrayList<HostPort> peersToConnect = new ArrayList<HostPort>();
		for(String peer:peersList){
			peersToConnect.add(new HostPort(peer));
		}
		
	
		// initialize Server part
		TCPServer newServer = new TCPServer(this.fileSystemManager, port);
		newServer.start();
		
		// initialize Client part
		TCPClient newClient = new TCPClient(this.fileSystemManager,peersToConnect);
		newClient.start();
		
		// wait some time for connection, generate Sync events
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SyncEvent sync = new SyncEvent(fileSystemManager);
		sync.start();
		
		
	}

	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		//store file system event to queue
		PeerMaster.eventToPeer(fileSystemEvent);
	}
	
}
