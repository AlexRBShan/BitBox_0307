package unimelb.bitbox;

import java.util.logging.Logger;
import java.util.Queue;
import java.util.ArrayList;

import unimelb.bitbox.util.HostPort;

public class InitPeerConnection {
	private static Logger log = Logger.getLogger(InitPeerConnection.class.getName());
	private ArrayList<HostPort> peersToConnect = new ArrayList<HostPort>();
	
	public InitPeerConnection(ArrayList<HostPort> peersToConnect) {
		this.peersToConnect = peersToConnect;
	}
	
	
	

}
