package unimelb.bitbox;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.logging.Logger;
import java.util.ArrayList;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager;

public class TCPServer extends Thread {
	private static Logger log = Logger.getLogger(TCPServer.class.getName());
	
	private FileSystemManager fileSystemManager;
	private int port;
	
	public TCPServer(FileSystemManager fileSystemManager, int portNumber) {
		this.fileSystemManager = fileSystemManager;
		this.port = portNumber;
	}
	
	@Override
	public void run() {
		
		try {
			@SuppressWarnings("resource")
			ServerSocket socket = new ServerSocket(this.port);
			while (true) {
				log.info("Server ready for connection, listenning...");
				Socket clientSocket = socket.accept();
				// Start a new thread for a connection
				PeerStatistics.numPeersConnection++;
				//Thread t = new Thread(() -> serveClient(clientSocket));
				//t.start();
				ConnectFromPeer sp = new ConnectFromPeer(fileSystemManager, clientSocket);
				sp.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
