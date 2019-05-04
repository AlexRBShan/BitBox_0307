package unimelb.bitbox;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;

public class Peer 
{
	private static Logger log = Logger.getLogger(Peer.class.getName());
    public static void main( String[] args ) throws IOException, NumberFormatException, NoSuchAlgorithmException
    {
    	System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tc] %2$s %4$s: %5$s%n");
        log.info("BitBox Peer starting...");
        Configuration.getConfiguration();
        
        //read configurations and store to peer master
        PeerMaster.path = Configuration.getConfigurationValue("path");
        PeerMaster.myPort = Integer.parseInt(Configuration.getConfigurationValue("port"));
        PeerMaster.myHost = Configuration.getConfigurationValue("advertisedName");
        PeerMaster.peersList = Configuration.getConfigurationValue("peers").split(",");
        PeerMaster.maxIncomingPeer = Integer.parseInt(Configuration.
    			getConfigurationValue("maximumIncommingConnections"));
        PeerMaster.blockSize = Long.parseLong(Configuration.getConfigurationValue("blockSize"));
        PeerMaster.syncInterval = Long.parseLong(Configuration.getConfigurationValue("syncInterval"));
        
        new ServerMain();
        
    }
}
