package unimelb.bitbox;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemObserver;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

public class TestEvent2 implements FileSystemObserver{
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	protected FileSystemManager fileSystemManager;
	
	public TestEvent2() throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager=new FileSystemManager("testReceive",this);
		
		try {
			@SuppressWarnings("resource")
			ServerSocket server = new ServerSocket(4444);
			while(true) {
				System.out.println("Server ready for listenning...");
				Socket socket = server.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"), true);
				while(true) {
					Document rec = Document.parse(reader.readLine());
					System.out.println("command is: " + rec.getString("command"));
					System.out.println("Json: " + rec.toJson());
					ProcessRequest requestProcessor = new ProcessRequest(this.fileSystemManager, rec, socket);
					requestProcessor.start();
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