package unimelb.bitbox;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import java.util.Queue;
import java.util.LinkedList;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemObserver;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;
import unimelb.bitbox.EventProcess;
import unimelb.bitbox.Protocol;

public class TestEvent2 implements FileSystemObserver{
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	protected FileSystemManager fileSystemManager;
	
	public TestEvent2() throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager=new FileSystemManager("testReceive",this);
		try {
			ServerSocket server = new ServerSocket(4444);
			while(true) {
				Socket socket = server.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
				
				Document rec = Document.parse(reader.readLine());
				EventProcess ep = new EventProcess(fileSystemManager);
				
				switch(rec.getString("command")) {
				case "FILE_CREATE_REQUEST":
					ep.fileCreateResponse(rec);
				}
				
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		System.out.println("TestReceive FOUND Event: " + fileSystemEvent.toString());
	}
		
}