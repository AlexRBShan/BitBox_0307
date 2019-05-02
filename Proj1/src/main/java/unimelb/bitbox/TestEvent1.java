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
import unimelb.bitbox.EventProcess;
import unimelb.bitbox.Protocol;

public class TestEvent1 implements FileSystemObserver {
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	protected FileSystemManager fileSystemManager;
	
	public TestEvent1() throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager=new FileSystemManager(Configuration.getConfigurationValue("path"),this);
		
		
	}

	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		try {
			Socket socket = new Socket("localhost", 4444);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
			
			Protocol protocol = new Protocol();
			EventProcess ep = new EventProcess(fileSystemManager);
			
			switch(fileSystemEvent.event) {
			case FILE_CREATE:
				 Document doc = protocol.FILE_CREATE_REQUEST(fileSystemEvent);
				 writer.println(doc.toJson());
				 Document rec = Document.parse(reader.readLine());
				 switch(rec.getString("command")){
				 case "FILE_CREATE_RESPONSE":
					 System.out.println("Response: " + rec.toJson());
					 
				 case "FILE_BYTE_REQUEST":
					 Document doc2 = ep.fileByteResponse(rec);
					 writer.println(doc2.toJson());
				 }
				 
			case FILE_DELETE:
				;
			case FILE_MODIFY:
				;
			case DIRECTORY_CREATE:
				;
			case  DIRECTORY_DELETE:
				;
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
	}

	
}
