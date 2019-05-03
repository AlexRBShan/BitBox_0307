package unimelb.bitbox;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemObserver;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

import unimelb.bitbox.TCPServer;
import unimelb.bitbox.util.HostPort;

public class ServerMain2 implements FileSystemObserver {
	private static Logger log = Logger.getLogger(ServerMain2.class.getName());
	protected FileSystemManager fileSystemManager;
	//Queue<FileSystemEvent> eventQueue = new LinkedList<FileSystemEvent>();
	
	public ServerMain2() throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager=new FileSystemManager("peer2",this);
		
		// read configurations for server
		HostPort serverHost = new HostPort("localhost",8112);

		// ready to listen from peers
		TCPServer newServer = new TCPServer(fileSystemManager,serverHost.port);
		newServer.start();
		
	}

	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		// TODO: process events
		System.out.println("found event: " + fileSystemEvent.toString());
	}
	
}
