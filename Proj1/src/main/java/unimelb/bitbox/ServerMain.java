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
		
		// read configurations for server
		int serverPortNumber = Integer.parseInt(Configuration.getConfigurationValue("port"));
		String hostName = Configuration.getConfigurationValue("advertisedName");
		HostPort serverHost = new HostPort(hostName,serverPortNumber);
		// read configurations for peers to connect
		String[] peersList = Configuration.getConfigurationValue("peers").split(",");
		ArrayList<HostPort> peersToConnect = new ArrayList<HostPort>();
		for(String peer:peersList){
			peersToConnect.add(new HostPort(peer));
		}
		// ready to listen from peers
		//TCPServer newServer = new TCPServer(serverHost.host,serverHost.port);
		//newServer.start();
		
		for(FileSystemEvent event:fileSystemManager.generateSyncEvents()) {
			PeerStatistics.eventQueue.offer(event);
		}
		
		// initialize connection to other peers
		TCPClient newClient = new TCPClient(peersToConnect,PeerStatistics.eventQueue);
		newClient.start();
		
		
	}

	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		// TODO: process events
		System.out.println("found event: " + fileSystemEvent.toString());
		//store file system event to queue
		PeerStatistics.eventQueue.offer(fileSystemEvent);
		System.out.println("size of event: " + PeerStatistics.eventQueue.size());
	}
	
}
