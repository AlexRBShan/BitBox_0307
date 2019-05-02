package unimelb.bitbox;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class PeerStatistics {
	// blockSize
	public static long blockSize = Long.parseLong(Configuration.getConfigurationValue("blockSize"));
	// number of peers connected
	public static int numPeersConnection = 0;
	// max number of peers connected
	private static int maxPeersConnection = Integer.parseInt(Configuration.
			getConfigurationValue("maximumIncommingConnections"));
	// list of peers connection
	private static ArrayList<HostPort> peerList = new ArrayList<HostPort>();
	
	// event Queue;
	public static Queue<FileSystemEvent> eventQueue = new LinkedList<FileSystemEvent>();
	
	// add a peer not already connected to peer list
	public static boolean addPeer(HostPort peerNew) {
		if(containPeer(peerNew)) {
			return false;
		} else {
			peerList.add(peerNew);
			return true;
		}
	}
	
	// remove a peer already connected from peer list
	public static boolean removePeer(HostPort peerNew) {
		if(containPeer(peerNew)) {
			peerList.remove(peerNew);
			return true;
		} else {
			
			return false;
		}
	}
	
	// check if a new peer is already connected.
	private static boolean containPeer(HostPort peerNew) {
		for(HostPort peerConnected : peerList) {
			if(peerConnected.equals(peerNew)) {
				return true;
			}
		}
		return false;
	}
	
	// check if peer is full
	public static boolean isPeerFull() {
		if(numPeersConnection >= maxPeersConnection) {
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

}
