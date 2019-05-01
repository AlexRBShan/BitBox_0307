package unimelb.bitbox;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Logger;
import java.net.Socket;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;
import unimelb.bitbox.util.HostPort;

public class EventProcessor {
	private static Logger log = Logger.getLogger(EventProcessor.class.getName());
	private FileSystemManager fileSystemManager; 
	private FileSystemEvent eventToHandle;
	private Socket socket;
    
    
    public EventProcessor(FileSystemManager fileSystemManager,FileSystemEvent eventToHandle,Socket socket) {
    	this.eventToHandle = eventToHandle;
    	this.fileSystemManager = fileSystemManager;
    	this.socket = socket;
    }
    
    private void processFileCreate() {
    	
    }

}
