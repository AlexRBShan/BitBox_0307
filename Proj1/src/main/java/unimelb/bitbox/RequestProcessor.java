package unimelb.bitbox;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.net.Socket;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

public class RequestProcessor extends Thread{
	private static Logger log = Logger.getLogger(RequestProcessor.class.getName());
	private FileSystemManager fileSystemManager; 
	private Document request;
	private String command;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private boolean isComplete; 
	
	public RequestProcessor(FileSystemManager fileSystemManager,Document request,Socket socket) {
		this.fileSystemManager = fileSystemManager;
		this.request = request;
		this.command = request.getString("command");
		this.socket = socket;
    	try {
    		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
    		this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"), true);
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    	this.isComplete = false;
	}
	
	@Override
	public void run() {
		switch(command) {
		case "HANDSHAKE_REQUEST":
			// Can possible move handshake request also here.
			processHandshake();
		case "FILE_CREATE_REQUEST":
			processFileCreate();
		case "FILE_DELETE_REQUEST":
			processFileDelete();
		case "FILE_MODIFY_REQUEST":
			processFileModify();
		case "DIRECTORY_CREATE_REQUEST":
			processDirectoryCreate();
		case "DIRECTORY_DELETE_REQUEST":
			processDirectoryDelete();
		case "FILE_BYTE_RESPONSE":
			processFileByte();
		default:
			processInvalid();
		}
		
		
	}
	
	private void processHandshake() {
		;
	}
	
	private void processFileCreate() {
		log.info("Start Processing File Create: " + this.request.getString("pathName"));
		;
	}
	
	private void processFileDelete() {
		log.info("Start Processing File Delete: " + this.request.getString("pathName"));
		;
	}
	
	private void processFileModify() {
		log.info("Start Processing File Modify: " + this.request.getString("pathName"));
		;
	}
	
	private void processDirectoryCreate() {
		log.info("Start Processing Directory Create: " + this.request.getString("pathName"));
		while(!isComplete) {
			RequestOperator requestOperator = new RequestOperator(this.fileSystemManager);
			Document result = requestOperator.directoryCreateResponse(this.request);
			if(result.getBoolean("status")) {
				this.isComplete = true;
			}
		}
	}
	
	private void processDirectoryDelete() {
		log.info("Start Processing Directory Delete: " + this.request.getString("pathName"));
		while(!isComplete) {
			RequestOperator requestOperator = new RequestOperator(this.fileSystemManager);
			Document result = requestOperator.directoryDeleteResponse(this.request);
			if(result.getBoolean("status")) {
				this.isComplete = true;
			}
		}
		
	}
	
	private void processFileByte() {
		;
	}
	
	private void processInvalid() {
		
	}


}
