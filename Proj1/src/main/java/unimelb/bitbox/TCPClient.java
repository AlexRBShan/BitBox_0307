package unimelb.bitbox;

import java.util.logging.Logger;
import java.util.Queue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.Protocol;
import unimelb.bitbox.ConnectToPeer;
import unimelb.bitbox.EventProcessor;

public class TCPClient extends Thread{
	private static Logger log = Logger.getLogger(TCPClient.class.getName());
	
	private ArrayList<HostPort> peersToConnect;
	private ArrayList<HostPort> hostConnected;
	private HashMap<HostPort, Socket> peersConnected;
	private Queue<HostPort> peersAvailable;
	
	private Queue<FileSystemEvent> eventQueue;
	
	
	public TCPClient(ArrayList<HostPort> peersToConnect, Queue<FileSystemEvent> eventQueue) {
		this.peersToConnect = peersToConnect;
		this.hostConnected = new ArrayList<HostPort>();
		this.peersConnected = new HashMap<HostPort, Socket>();
		this.peersAvailable = new LinkedList<HostPort>();
		this.eventQueue = eventQueue;
	}
	
	@Override
	public void run() {
		//Initial Connection to all peers
		for(HostPort peer: peersToConnect) {
			peersAvailable.add(peer);
			log.info("trying to connect to peer: " + peer.toString());
			ConnectToPeer newConnection = new ConnectToPeer(peer);
			if(newConnection.Connect()) {
				HostPort newHost = newConnection.getHost();
				peersConnected.put(newHost, newConnection.getSocket());
				hostConnected.add(newHost);
			}
		}
		
		// generate file event to all peers connected
		while(true) {
			FileSystemEvent newEvent = eventQueue.poll();
			for(HostPort host:hostConnected) {
				Socket socket = peersConnected.get(host);
				eventprocess(socket, "Hello" + newEvent.toString());
			}
		}
		
	}
	
	//other functions
	private void eventprocess(Socket socket, String msg){
		try {
			//BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
			
			writer.println(msg);
			writer.flush();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
