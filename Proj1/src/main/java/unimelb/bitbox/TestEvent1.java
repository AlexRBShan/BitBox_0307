package unimelb.bitbox;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemObserver;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

public class TestEvent1 implements FileSystemObserver {
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	protected FileSystemManager fileSystemManager;
	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	public TestEvent1() throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager=new FileSystemManager("share",this);
		this.socket = new Socket("localhost", 4444);
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
		this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"), true);
	}

	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		System.out.println("Found Event: " + fileSystemEvent);
		
		ProcessEvent ep = new ProcessEvent(this.fileSystemManager, fileSystemEvent, this.socket);
		ep.start();
	}

	
}
