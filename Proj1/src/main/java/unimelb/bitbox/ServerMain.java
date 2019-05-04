package unimelb.bitbox;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;

import unimelb.bitbox.util.Configuration;
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
		fileSystemManager=new FileSystemManager(Configuration.getConfigurationValue("path"),this);
		
		// read configurations for peers to connect
		int port = Integer.parseInt(Configuration.getConfigurationValue("port"));
		String[] peersList = Configuration.getConfigurationValue("peers").split(",");
		ArrayList<HostPort> peersToConnect = new ArrayList<HostPort>();
		for(String peer:peersList){
			peersToConnect.add(new HostPort(peer));
		}
		
		//PeerStatistics.eventQueue.addAll(fileSystemManager.generateSyncEvents());
		// generate Sync events
		SyncEvent sync = new SyncEvent(fileSystemManager);
		sync.start();
		
		// initialize Server part
		TCPServer newServer = new TCPServer(this.fileSystemManager, port);
		newServer.start();
		
		// initialize Client part
		TCPClient newClient = new TCPClient(this.fileSystemManager,peersToConnect,PeerMaster.eventQueue);
		newClient.start();
		
		
	}

	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		//store file system event to queue
		PeerMaster.eventQueue.offer(fileSystemEvent);
		PeerMaster.eventQueue2.offer(fileSystemEvent);
	}
	
}
