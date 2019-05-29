package unimelb.bitbox;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager;

public class ConnectFromPeer extends Thread {
	private static Logger log = Logger.getLogger(ConnectFromPeer.class.getName());
	private HostPort localHostPort;
	private Socket socket;
	
	public ConnectFromPeer(Socket socket) {
		this.socket = socket;

		String localHost = PeerMaster.myHost;
		int localPort = PeerMaster.myPort;
		this.localHostPort = new HostPort(localHost, localPort);
	}
	
	@Override
	public void run() {
		try {
			// Input stream Output Stream
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

			Document handShakeReque = Document.parse(reader.readLine());

			if (!handShakeReque.containsKey("command")) {
				// not exist cmd message
				Document output = Protocol.INVALID_PROTOCOL("message must contain a command field as string");
				writer.println(output.toJson());
				socket.close();
			}else if (!handShakeReque.containsKey("hostPort")) {
				// Cannot Identify peer, Exit.
				Document output = Protocol.INVALID_PROTOCOL("message must contain host and port for handshake");
				writer.println(output.toJson());
				socket.close();
			}else {
				// Otherwise
				if (!handShakeReque.getString("command").equals("HANDSHAKE_REQUEST")) {
					// Not Valid Command
					Document output = Protocol.INVALID_PROTOCOL("handshake is required");
					writer.println(output.toJson());
					socket.close();
				} else {
					Document hostPort = (Document)handShakeReque.get("hostPort");
					HostPort currentClient= new HostPort(hostPort);
					// HANDSHAKE_REQUEST is received
					if(PeerMaster.isPeerFull()){
						// peer full
						Document handShakeRspon = Protocol.CONNECTION_REFUSED(PeerMaster.peerIncomingListToDoc());
						writer.println(handShakeRspon.toJson());
						socket.close();
					}else if(PeerMaster.containPeer(currentClient)) {
						// peer already exists in file
						Document handShakeRspon = Protocol.INVALID_PROTOCOL("handshake request after successful handshake");
						writer.println(handShakeRspon.toJson());
						socket.close();
					}else {
						// handshake response
						log.info("Handshake success with client " + socket.getInetAddress().getHostAddress()
								+ ":" + socket.getPort());
						
						Document handShakeRspon = Protocol.HANDSHAKE_RESPONSE(this.localHostPort);
						writer.println(handShakeRspon.toJson());
						
						// add peer to master list
						PeerMaster.addIncomingPeer(currentClient);
						PeerMaster.addPeer(currentClient, this.socket);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}	

	}

}
