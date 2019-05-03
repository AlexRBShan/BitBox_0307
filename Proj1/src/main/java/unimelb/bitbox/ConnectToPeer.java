package unimelb.bitbox;

import java.util.logging.Logger;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.*;
import java.net.Socket;

import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.Protocol;

public class ConnectToPeer{
	private static Logger log = Logger.getLogger(ConnectToPeer.class.getName());
	private HostPort targetPeer;
	private Queue<HostPort> peersAvailable; 
	private Socket mySocket;
	private HostPort localHostPort;
	
	
	public ConnectToPeer(HostPort targetPeer) {
		this.targetPeer = targetPeer;
		this.peersAvailable = new LinkedList<HostPort>();
		this.peersAvailable.offer(targetPeer);
		this.mySocket = null;
		
		String localHost = Configuration.getConfigurationValue("advertisedName");
		int localPort = Integer.parseInt(Configuration.getConfigurationValue("port"));
		this.localHostPort = new HostPort(localHost, localPort);
	}
	
	public boolean Connect(){
		while(!peersAvailable.isEmpty()){
			// get the head of queue for the peer to connect
			HostPort peer = peersAvailable.poll();
			try {
				Socket socket = new Socket(peer.host, peer.port);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
				
				Document hskRequest = Protocol.HANDSHAKE_REQUEST(this.localHostPort);
				writer.println(hskRequest.toJson());
				writer.flush();
				log.info("Trying to Handshake with peer " + peer.host + ":" + peer.port);
				
				Document hskResponse = Document.parse(reader.readLine());
				log.info("Server respond with: " + hskResponse.toJson());

				// parsing returned document command
				switch(hskResponse.getString("command")) {
					// Handshake successfully
				    case "HANDSHAKE_RESPONSE":
				    	Document serverhost = (Document) hskResponse.get("hostPort");
				    	HostPort hostReturned = new HostPort(serverhost);
				    	// this.targetPeer = new HostPort(targetHost,targetPort);
				    	this.targetPeer = hostReturned;
				    	this.mySocket = socket;
				    	log.info("peer " + targetPeer.toString() + " is connected!");
				    	return true;
				    // Connection is denied, retrieve possible target peers
				    case "CONNECTION_REFUSED":
				    	log.info("Connection Rejected by peer " + peer.toString());
				    	@SuppressWarnings("unchecked")
				    	ArrayList<Document> peersOnList = (ArrayList<Document>) hskResponse.get("peers");
				    	
				    	// loop through all peers returned from server
				    	for(Document potentialPeer:peersOnList) {
				    		HostPort potentialHost = new HostPort(potentialPeer.getString("host")
				    				,potentialPeer.getInteger("port"));
				    		// add the target peer into peerAvailable
				    		peersAvailable.offer(potentialHost);	
				    	}
				    	socket.close();
				    // otherwise
				    default:
				    	log.info("Invalid, Closing connection for " + peer.toString());
				    	socket.close();
				}
			} catch(IOException e) {
				log.info(e.toString());
				return false;
			}	
		}
		return false;
	}
	
	public Socket getSocket() {
		return this.mySocket;
	}
	public HostPort getHost() {
		return this.targetPeer;
	}

}

