package unimelb.bitbox;

import java.util.logging.Logger;
import java.util.Queue;
import java.util.ArrayList;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.Protocol;

public class TCPClient extends Thread{
	private static Logger log = Logger.getLogger(ClientHandler.class.getName());
	private ArrayList<HostPort> peersToConnect;
	private ArrayList<HostPort> peersConnected = new ArrayList<HostPort>();
	private Queue<FileSystemEvent> eventQueue;
	
	public TCPClient(ArrayList<HostPort> peersToConnect, Queue<FileSystemEvent> eventQueue) {
		this.peersToConnect = peersToConnect;
		this.eventQueue = eventQueue;
	}
	
	@Override
	public void run() {
		for(HostPort peer:peersToConnect) {
			HostPort connectedPeer = InitPeerConnect(peer);
			if(!peersConnected.contains(connectedPeer)) {
				peersConnected.add(connectedPeer);
			}
		}
		
		
	}
	
	// initial peer connections
	private HostPort InitPeerConnect(HostPort targetPeer){
		String hostConnected = null;
		int portConnected = -1;
		try {
			// send handshake to the target peer and get the response document
			Document handShkRespond = HandshakeConnect(targetPeer);
		
			// parsing returned document command
			switch(handShkRespond.getString("command")) {
				// Handshake successfully
			    case "HANDSHAKE_RESPONSE":
			    	Document serverhost = (Document) handShkRespond.get("hostPort");
			    	hostConnected = serverhost.getString("host");
			    	portConnected = serverhost.getInteger("port");
			    	
			    // Connection is denied, try to connect from the returned peer list
			    case "CONNECTION_REFUSED":
			    	log.info("Connection Rejected by server " + targetPeer.toString());
			    	@SuppressWarnings("unchecked")
			    	ArrayList<Document> peersOnList = (ArrayList<Document>) handShkRespond.get("peers");
			    	
			    	// loop through all peers returned from server
			    	for(Document potentialPeer:peersOnList) {
			    		HostPort potentialHost = new HostPort(potentialPeer.getString("host")
			    				,potentialPeer.getInteger("port"));
			    		// connect to the listed peer
			    		Document newResponce = HandshakeConnect(potentialHost);
			    		switch(newResponce.getString("command")) {
			    		    case "HANDSHAKE_RESPONSE":
					    	    Document newserverhost = (Document) handShkRespond.get("hostPort");
					    	    hostConnected = newserverhost.getString("host");
						    	portConnected = newserverhost.getInteger("port");
			    		    case "CONNECTION_REFUSED":
			    		    	continue;
			    		}
			    	}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		log.info("Successfully handshake with server:" + hostConnected + ":" + portConnected);
		HostPort peerConnected = new HostPort(hostConnected,portConnected);
		return peerConnected;
	}
	
	private Document HandshakeConnect(HostPort peer) throws IOException{
		Socket socket = new Socket(peer.host, peer.port);
		// create input and output streams for 
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
		
		Protocol p = new Protocol();
		Document handShake = p.HANDSHAKE_REQUEST(peer.host, peer.port);
		
		writer.println(handShake.toJson());
		log.info("Handshake with server " + peer.host + ":" + peer.port);
		Document handShkRespond = Document.parse(reader.readLine());
		log.info("Server respond with: " + handShkRespond.toString());
		socket.close();
		
		return handShkRespond;
	}
}
