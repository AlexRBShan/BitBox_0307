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
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.Protocol;
import unimelb.bitbox.ConnectToPeer;
import unimelb.bitbox.ProcessEvent;

public class TCPClient extends Thread{
	private static Logger log = Logger.getLogger(TCPClient.class.getName());
	private FileSystemManager fileSystemManager;
	private ArrayList<HostPort> peersToConnect;
	private ArrayList<HostPort> hostConnected;
	private HashMap<HostPort, Socket> peersConnected;
	private Queue<HostPort> peersAvailable;
	private HashMap<HostPort, BufferedReader> peersReader;
	
	private Queue<FileSystemEvent> eventQueue;
	
	
	public TCPClient(FileSystemManager fileSystemManager, ArrayList<HostPort> peersToConnect, Queue<FileSystemEvent> eventQueue) {
		this.fileSystemManager = fileSystemManager;
		this.peersToConnect = peersToConnect;
		this.hostConnected = new ArrayList<HostPort>();
		this.peersConnected = new HashMap<HostPort, Socket>();
		this.peersAvailable = new LinkedList<HostPort>();
		this.eventQueue = eventQueue;
		this.peersReader = new HashMap<HostPort, BufferedReader>();
	}
	
	@Override
	public void run() {
		// wait sometime for server to start
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Initial Connection to all peers
		for(HostPort peer: peersToConnect) {
			peersAvailable.add(peer);
			log.info("trying to connect to peer: " + peer.toString());
			ConnectToPeer newConnection = new ConnectToPeer(peer);
			if(newConnection.Connect()) {
				HostPort newHost = newConnection.getHost();
				Socket newSocket = newConnection.getSocket();
				PeerMaster.addPeer(newHost);
				peersConnected.put(newHost, newSocket);
				hostConnected.add(newHost);
				
				BufferedReader reader;
				try {
					reader = new BufferedReader(new InputStreamReader(newSocket.getInputStream(), "UTF8"));
					peersReader.put(newHost, reader);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		// generate file event to all peers connected
		while(true) {
			try {
				Thread.sleep(100);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			// handle event
			if(!PeerMaster.eventQueue.isEmpty()) {
				//FileSystemEvent newEvent = FILE_CREATE;
				FileSystemEvent newEvent = PeerMaster.eventQueue.poll();
				for(HostPort host:hostConnected) {
					Socket socket = peersConnected.get(host);
					ProcessEvent ep = new ProcessEvent(this.fileSystemManager, newEvent, socket);
					ep.start();
				}
			}
			//handle response
			try {
				for(HostPort host:hostConnected) {
					BufferedReader read = peersReader.get(host);
					Socket socket = peersConnected.get(host);
					if(read.ready()) {
						Document response = Document.parse(read.readLine());
						log.info("get from peer: " + response.toJson());
						ProcessRequest rp = new ProcessRequest(this.fileSystemManager, response, socket);
						rp.start();
					}else {
						continue;
					}
					
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
