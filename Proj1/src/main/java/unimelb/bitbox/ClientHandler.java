package unimelb.bitbox;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.Protocol;

/**
 * A ClientHandler used for handling requests to all peers connected.
 * @author Rongbing Shan
 */

public class ClientHandler extends Thread {
	private static Logger log = Logger.getLogger(ClientHandler.class.getName());
	private Socket socket = null;
	private String hostName;
	private int portNum = -1;

	// Constructor 
	public ClientHandler(Socket s) { 
	     this.socket = s;
	     this.hostName = s.getInetAddress().getHostName();
	     this.portNum = s.getPort();
	 }
	
	@Override
	public void run() {
		log.info("Client Handler for "+ hostName + "(" + portNum +")" + " is running...");
		try {
			// Buffer Reader and Writer
			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF8"));
					    
					    // Receive data
		    while(true){
		    	if(input.available() > 0){
		    		Document msg = Document.parse(input.readUTF());
		    		System.out.println("COMMAND RECEIVED: "+msg.toJson());
		    		Document response = parse_command(msg);
		    		System.out.println("MSG TO REPLY	: "+response.toJson());
		    		output.writeUTF(response.toJson());
		    	}
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	
	// parse document commands
	private Document parse_command(Document doc) {
		Document response = new Document();
		Document hostinfo = new Document();
		hostinfo.append("host",this.hostName);
		hostinfo.append("port", this.portNum);
		String command = doc.getString("command");
		switch(command) {
		case "REQUEST_HANDSHAKE":
			if(PeerStatistics.isPeerFull()) {
				response.append("command", "CONNECTION_REFUSED");
				response.append("message", "connection limit reached");
				response.append("peers", PeerStatistics.peerListToDoc());
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
