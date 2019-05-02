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
import unimelb.bitbox.EventProcess;
import unimelb.bitbox.Protocol;

public class TestEvent1 implements FileSystemObserver {
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	protected FileSystemManager fileSystemManager;
	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	private Protocol protocol;
	
	public TestEvent1() throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager=new FileSystemManager(Configuration.getConfigurationValue("path"),this);
		this.socket = new Socket("localhost", 4444);
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
		this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
	}

	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		EventProcess ep = new EventProcess(fileSystemManager);
		switch(fileSystemEvent.event) {
		case FILE_CREATE:
			
			 Document doc = protocol.FILE_CREATE_REQUEST(fileSystemEvent.fileDescriptor, fileSystemEvent.pathName);
			 writer.println(doc.toJson());
			 writer.flush();
			 TimeUnit.SECONDS.sleep(1);
			 /*
			 Document rec = Document.parse(reader.readLine());
			 switch(rec.getString("command")){
			 case "FILE_CREATE_RESPONSE":
				 System.out.println("Response: " + rec.toJson());
				 
			 case "FILE_BYTE_REQUEST":
				 Document doc2 = ep.fileByteResponse(rec);
				 writer.println(doc2.toJson());
				 */
			 }	
	}

	
}
