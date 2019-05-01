package unimelb.bitbox;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.logging.Logger;
import java.util.ArrayList;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;

public class TCPServer implements Runnable {
	private static Logger log = Logger.getLogger(TCPServer.class.getName());
	private Thread thread;
	private int portNumber;
	private String hostNmae;
	//private ClientHandler client;
	private ServerSocket server = null;
	
	private int maxClientConnected;
	private int numClientConnected = 0;
	
	private ArrayList<Document> peerConnected = new ArrayList<Document>();
	
	public TCPServer(String hostName, int portNumber) {
		this.hostNmae = hostName;
		this.portNumber = portNumber;
		//this.Arraylist<ClientHandler>
		
		try {
			this.server = new ServerSocket(this.portNumber);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		log.info("Server ready at port: " + this.portNumber + ", listening...");
		
		// listening to client connections
		while(true) {
			try {
				Socket client = this.server.accept();
				this.numClientConnected += 1;
				Thread t = new Thread(() -> clientHandler(client));
				//ClientHandler t = new ClientHandler(client);
				t.start();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void start() {
		if(thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	public void stop() {
		if(thread != null) {
			thread = null;
		}
	}
	
	private void clientHandler(Socket s) {
		String clientIP = s.getInetAddress().getHostName();
		int clientPort = s.getPort();
		HostPort clientHost = new HostPort(clientIP, clientPort);
		
		// if connection is not full, add new peer in
	}
	
	private Document parse_command(Document doc) {
		Document response = new Document();
		Document hostinfo = new Document();
		hostinfo.append("host",this.hostNmae);
		hostinfo.append("port", this.portNumber);
		String command = doc.getString("command");
		switch(command) {
		case "REQUEST_HANDSHAKE":
			if(numClientConnected > maxClientConnected) {
				response.append("command", "CONNECTION_REFUSED");
				response.append("message", "connection limit reached");
				response.append("peers", this.peerConnected);
			} else {
				response.append("command", "HANDSHAKE_RESPONSE");
				response.append("hostPort", hostinfo);
			}
		case "FILE_CREATE_REQUEST":
			;
		case "FILE_DELETE_REQUEST":
			;
		case "FILE_MODIFY_REQUEST":
			;
		case "DIRECTORY_CREATE_REQUEST":
			;
		case "DIRECTORY_DELETE_REQUEST":
			;
		}
		return response;
	}

}
