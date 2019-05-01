package unimelb.bitbox;

import unimelb.bitbox.util.*;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import unimelb.bitbox.*;

public class Client implements FileSystemObserver{
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	protected FileSystemManager fileSystemManager;
	private Queue<FileSystemEvent> eventQueue = new LinkedList<FileSystemEvent>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Read configurations
		Configuration.getConfiguration();
		int portnum = Integer.parseInt(Configuration.getConfigurationValue("port"));
		String[] peers = Configuration.getConfigurationValue("peers").split(",");
		String ip = "localhost";
		HostPort host = new HostPort(ip, portnum);
		ArrayList<HostPort> peersToConnect = new ArrayList<HostPort>();
		peersToConnect.add(host);
	}
	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		// TODO: process events
		System.out.println("found event: " + fileSystemEvent.toString());
		//store file system event to queue
		this.eventQueue.offer(fileSystemEvent);
	}
	public Queue<FileSystemEvent> getQ(){
		return this.eventQueue;
	}
}