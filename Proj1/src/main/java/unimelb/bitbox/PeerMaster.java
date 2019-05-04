package unimelb.bitbox;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

public class PeerMaster {
	//Configurations
	public static String path;
	public static int myPort;
	public static String myHost;
	public static String[] peersList;
	public static int maxIncomingPeer;
	public static long blockSize;
	public static long syncInterval;
	
	// number of incoming connections
	public static int numPeersConnection = 0;	
	// list of peers connection
	public static ArrayList<HostPort> peerList = new ArrayList<HostPort>();
	
	// event Queue;
	public static HashMap<HostPort, Queue<FileSystemEvent>> peerEventQ = new HashMap<HostPort, Queue<FileSystemEvent>>();
	public static Queue<FileSystemEvent> eventQueue = new LinkedList<FileSystemEvent>();
	public static Queue<FileSystemEvent> eventQueue2 = new LinkedList<FileSystemEvent>();
	
	// add a peer not already connected to peer list
	public static boolean addPeer(HostPort peerNew) {
		if(containPeer(peerNew)) {
			return false;
		} else {
			Queue<FileSystemEvent> newQ = new LinkedList<FileSystemEvent>();
			peerList.add(peerNew);
			peerEventQ.put(peerNew, newQ);
			return true;
		}
	}
	
	// remove a peer already connected from peer list
	public static boolean removePeer(HostPort peerNew) {
		if(containPeer(peerNew)) {
			peerList.remove(peerNew);
			peerEventQ.remove(peerNew);
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
		if(numPeersConnection >= maxIncomingPeer) {
			return true;
		} else {
			return false;
		}
	}
	
	// convert current list to an ArrayList of document
	public static ArrayList<Document> peerListToDoc(){
		ArrayList<Document> doc = new ArrayList<Document>();
		for(HostPort peer: peerList) {
			doc.add(peer.toDoc());
		}
		return doc;
	}
	
	// add event to all connected peer
	public static void eventToPeer(FileSystemEvent event) {
		for(HostPort peer:peerList) {
			peerEventQ.get(peer).offer(event);
		}
	}
	public static void eventToPeer(ArrayList<FileSystemEvent> event) {
		for(HostPort peer:peerList) {
			peerEventQ.get(peer).addAll(event);
		}
	}

}
