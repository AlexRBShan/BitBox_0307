package unimelb.bitbox;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemObserver;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

//import added by RS
import javax.net.ServerSocketFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import unimelb.bitbox.util.Document;

public class ServerMain implements FileSystemObserver {
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	protected FileSystemManager fileSystemManager;
	
	public ServerMain() throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager=new FileSystemManager(Configuration.getConfigurationValue("path"),this);
		
		// by RS
		int serverport = Integer.parseInt(Configuration.getConfigurationValue("port"));
		String[] peers = Configuration.getConfigurationValue("peers").split(",");
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		try(ServerSocket server = factory.createServerSocket(serverport)){
			System.out.println("Server ready at port: " + serverport + ", listening...");
			
			while(true) {
				Socket client = server.accept();
				Thread t = new Thread(() -> ClientHandler(client));
				t.start();
			}
			
		} catch(IOException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		// TODO: process events
		System.out.println("found event: " + fileSystemEvent.toString());
	}
	
	public void ClientHandler(Socket client) {
	try(Socket clientSocket = client){
			// Input stream
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
			// Output Stream
		    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
		    
		    // Receive data
		    while(true){
		    	if(input.available() > 0){
		    		Document msg = Document.parse(input.readUTF());
		    		System.out.println("COMMAND RECEIVED: "+msg.toJson());  		
		    	}
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
