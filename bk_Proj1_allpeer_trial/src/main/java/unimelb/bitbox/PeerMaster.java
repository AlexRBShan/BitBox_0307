package unimelb.bitbox;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class PeerMaster {
	//Configurations
	public static String path = Configuration.getConfigurationValue("path");
	public static int myPort = Integer.parseInt(Configuration.getConfigurationValue("port"));
	public static String myHost = Configuration.getConfigurationValue("advertisedName");
	public static String[] peersList = Configuration.getConfigurationValue("peers").split(",");
	public static int maxIncomingPeer = Integer.parseInt(Configuration.
			getConfigurationValue("maximumIncommingConnections"));
	public static long blockSize = Long.parseLong(Configuration.getConfigurationValue("blockSize"));
	public static long syncInterval = Long.parseLong(Configuration.getConfigurationValue("syncInterval"));
	// localhost
	public static HostPort localHost = new HostPort(myHost,myPort);
	
	// Number of peers connected
	private static int numIncomingPeer = 0;
	
	// list of connected peers, sockets and bufferred readers
	private static ArrayList<HostPort> peerList = new ArrayList<HostPort>();
	private static HashMap<HostPort, Socket> peersSocket;
	private static HashMap<HostPort, BufferedReader> peersReader;
	
	// list of Incoming peers connection
	private static ArrayList<HostPort> peerIncomingList = new ArrayList<HostPort>();
	
	// event Queue;
	public static Queue<FileSystemEvent> eventQueue = new LinkedList<FileSystemEvent>();
	
	//others
	private static Socket mySocket;
	
	//Functions 
	
	public static ArrayList<HostPort> getPeerList(){
		return peerList;
	}
	
	public static Socket getSocket(HostPort peer) {
		return peersSocket.get(peer);
	}
	
	public static BufferedReader getReader(HostPort peer) {
		return peersReader.get(peer);
	}
	
	// add a peer not already connected to peer list
	public static boolean addPeer(HostPort newPeer, Socket socket) {
		mySocket = socket;
		if(containPeer(newPeer)) {
			return false;
		} else {
			peerList.add(newPeer);
			peersSocket.put(newPeer, mySocket);
			return true;
		}
	}
	
	// remove a peer already connected from peer list
	public static boolean removePeer(HostPort oldPeer) {
		if(containPeer(oldPeer)) {
			peerList.remove(oldPeer);
			peersSocket.remove(oldPeer);
			peersReader.remove(oldPeer);
			return true;
		} else {
			
			return false;
		}
	}
	
	// check if a new peer is already connected.
	public static boolean containPeer(HostPort peerNew) {
		for(HostPort peerConnected : peerList) {
			if(peerConnected.equals(peerNew)) {
				return true;
			}
		}
		return false;
	}
	
	// check if peer is full
	public static boolean isPeerFull() {
		if(numIncomingPeer >= maxIncomingPeer) {
			return true;
		} else {
			return false;
		}
	}
	public static void addIncomingPeer(HostPort inPeer) {
		numIncomingPeer += 1;
		peerIncomingList.add(inPeer);
	}
	public static void removeIncomingPeer(HostPort inPeer) {
		numIncomingPeer -= 1;
		peerIncomingList.remove(inPeer);
	}
	public static boolean containIncomingPeer(HostPort inPeer) {
		for(HostPort peerConnected : peerIncomingList) {
			if(peerConnected.equals(inPeer)) {
				return true;
			}
		}
		return false;
	}
	
	// convert current list to an ArrayList of document
	public static ArrayList<Document> peerIncomingListToDoc(){
		ArrayList<Document> doc = new ArrayList<Document>();
		for(HostPort peer: peerIncomingList) {
			doc.add(peer.toDoc());
		}
		return doc;
	}

}
