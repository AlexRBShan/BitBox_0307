package unimelb.bitbox;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class RequestOperator {
	private Protocol protocol = new Protocol();
	private FileSystemManager fileSystemManager;
	
	
	public RequestOperator(FileSystemManager fileSystemManager) {
		this.fileSystemManager=fileSystemManager;
	}
	
	
	
	
	public Document fileCreateResponse(Document msg) {
		String command = msg.getString("command");
		Document descriptor = (Document)msg.get("fileDescriptor");
		String pathName = descriptor.getString("pathName");
		String md5 = descriptor.getString("md5");
		long length = descriptor.getLong("length");
		long lastModified = descriptor.getLong("lastModified");
		//valid command
		if (command.equals("FILE_CREATE_REQUEST")) {
			//pathname already exists
			if (fileSystemManager.fileNameExists(pathName)) {
				//return file created false response
				return protocol.FILE_CREATE_RESPONSE(msg, "pathname already exists", false);					 
			}
			else {
				//safe pathname
				if(fileSystemManager.isSafePathName(pathName)) {
					try {
						//
						if (fileSystemManager.createFileLoader(pathName, md5, length, lastModified)) {
							//file created successfully
							return protocol.FILE_CREATE_RESPONSE(msg, "file created", true);
						}
						else {
							//have problem creating the file
							return protocol.FILE_CREATE_RESPONSE(msg, "there was a problem creating the file", false);
						}
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return protocol.FILE_CREATE_RESPONSE(msg, "there was a problem creating the file", false);
				}
				else {
					//unsafe pathname
					return protocol.FILE_CREATE_RESPONSE(msg, "unsafe pathname given", false);
				}
			}		 
		}
		else {
			return protocol.INVALID_PROTOCOL("bad message");
		}
	}
	
	/*
	//first time request
	public Document fileByteRequest(long fileSize, long blockSize) {		
		long position = 0;
		if (fileSize <= blockSize) {
			return protocol.FILE_BYTES_REQUEST(event, position, fileSize);
		}
		else {
			return protocol.FILE_BYTES_REQUEST(event, position, blockSize);
		}			
	}
	*/
	
	//continue request
	public Document fileByteRequest(Document msg, long fileSize, long blockSize) {
		String command = msg.getString("command");
		String pathName = msg.getString("pathName");
		String encodedContent = msg.getString("content");
		long position = msg.getLong("position");
		long length = msg.getLong("length");
		if (command.equals("FILE_BYTE_RESPONSE")) {
			try {
				byte[] bytes = Base64.getDecoder().decode(encodedContent);
				ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
				fileSystemManager.writeFile(pathName, byteBuffer, position);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (position + length + blockSize <= fileSize) {
				return protocol.FILE_BYTES_REQUEST(msg, position+length, blockSize);
			}
			else {
				return protocol.FILE_BYTES_REQUEST(msg, position+length, fileSize-(position+length));
			}

		}
		else {
			return protocol.INVALID_PROTOCOL("bad message");
		}
	}
	
	
	public Document fileByteResponse(Document msg) {
		String command = msg.getString("command");
		Document descriptor =Document.parse(msg.get("fileDescriptor").toString()) ;
		String md5 = descriptor.getString("md5");
		long position = msg.getLong("position");
		long length = msg.getLong("length");
		String encodedContent="";
		if (command.equals("FILE_BYTE_REQUEST")) {
			try {
				ByteBuffer byteBuffer = fileSystemManager.readFile(md5, position, length);
				Base64.Encoder encoder = Base64.getEncoder();
				encodedContent = encoder.encodeToString(byteBuffer.array());
				return protocol.FILE_BYTES_RESPONSE(msg, encodedContent, "successful read", true);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return protocol.FILE_BYTES_RESPONSE(msg, encodedContent, "unsucessfull read", false);
		}
		else {
			return protocol.INVALID_PROTOCOL("bad message");
		}
	}
		

	
	
	public Document fileDeleteResponse(Document msg) {
		String command = msg.getString("command");
		String pathName = msg.getString("pathName");
		Document descriptor = (Document)msg.get("fileDescriptor");
		String md5 = descriptor.getString("md5");
		long lastModified = descriptor.getLong("lastModified");
		if (command.equals("FILE_DELETE_REQUEST")) {
			if (fileSystemManager.fileNameExists(pathName)) {
				//pathname already exists
				return protocol.FILE_CREATE_RESPONSE(msg, "pathname already exists",false);
			}
			else {
				if (fileSystemManager.isSafePathName(pathName)) {
					if (fileSystemManager.deleteFile(pathName, lastModified, md5)) {
						//file deleted successfully
						return protocol.FILE_DELETE_RESPONSE(msg, "file deleted", true);
					}
					else {
						//having problem deleting the file
						return protocol.FILE_DELETE_RESPONSE(msg, "there was a problem deleting the file", false);
					}
				}
				else {
					//unsafe pathname
					return protocol.FILE_CREATE_RESPONSE(msg, "unsafe pathname given",false);
				}
			}
		}
		else {
			return protocol.INVALID_PROTOCOL("bad message");
		}
	}
	

	
	public Document fileModifyResponse(Document msg) {
		String command = msg.getString("command");
		String pathName = msg.getString("pathName");
		Document descriptor = (Document)msg.get("fileDescriptor");
		String md5 = descriptor.getString("md5");
		long lastModified = descriptor.getLong("lastModified");
		if (command.equals("FILE_MODIFY_REQUEST")) {
			if (fileSystemManager.fileNameExists(pathName, md5)) {
				//pathname already exists
				return protocol.FILE_MODIFY_RESPONSE(msg, "file already exists with matching content", false);
			}
			else {
				if (fileSystemManager.isSafePathName(pathName)) {
					//safe pathName
					if (fileSystemManager.fileNameExists(pathName)) {
						//the modified file exists
						try {
							if(fileSystemManager.modifyFileLoader(pathName, md5, lastModified)) {
							//file modified successfully
							return protocol.FILE_MODIFY_RESPONSE(msg, "file loader ready", true);
							}
							else {
							//having problem modifying the file
							return protocol.FILE_MODIFY_RESPONSE(msg, "there was a problem modifying the file", false);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return protocol.FILE_MODIFY_RESPONSE(msg, "there was a problem modifying the file", false);
					}
					else {
						//pathname given does not exist
						return protocol.FILE_MODIFY_RESPONSE(msg, "pathname does not exist", false);
					}
				}					
				else {
					//unsafe pathname
					return protocol.FILE_MODIFY_RESPONSE(msg, "unsafe pathname given", false);
				}
			}
		}
		else {
			return protocol.INVALID_PROTOCOL("bad message");
		}
	}
	
	

	
	
	public Document directoryCreateResponse(Document msg) {
		String command = msg.getString("command");
		String pathName = msg.getString("pathName");
		if (command.equals("DIRECTORY_CREATE_REQUEST")) {
			if (fileSystemManager.dirNameExists(pathName)) {
				//path already exists
				return protocol.DIRECTORY_CREATE_RESPONSE(msg, "pathname already exists", false);				
			}
			else {
				if(fileSystemManager.isSafePathName(pathName)) {
					if (fileSystemManager.makeDirectory(pathName)) {
						//directory created successfully
						return protocol.DIRECTORY_CREATE_RESPONSE(msg, "directory created", true);
					}
					else {
						//have problem creating the directory
						return protocol.DIRECTORY_CREATE_RESPONSE(msg, "there was a problem creating the directory", false);
					}
				}
				else {
					//unsafe pathname
					return protocol.DIRECTORY_CREATE_RESPONSE(msg, "unsafe pathname given", false);
				}
			}		 
		}
		else {
			return protocol.INVALID_PROTOCOL("message");
		}
	}
	
	

	
	
	public Document directoryDeleteResponse(Document msg) {
		String command = msg.getString("command");
		String pathName = msg.getString("pathName");
		if (command.equals("DIRECTORY_DELETE_REQUEST")) {
			if (fileSystemManager.dirNameExists(pathName)) {
				//pathname already exists
				return protocol.DIRECTORY_CREATE_RESPONSE(msg, "pathname already exists",false);
			}
			else {
				if (fileSystemManager.isSafePathName(pathName)) {
					if (fileSystemManager.deleteDirectory(pathName)) {
						//directory deleted successfully
						return protocol.DIRECTORY_DELETE_RESPONSE(msg, "directory deleted", true);
					}
					else {
						//having problem deleting the directory
						return protocol.DIRECTORY_DELETE_RESPONSE(msg, "there was a problem deleting the directory", false);
					}
				}
				else {
					//unsafe pathname
					return protocol.DIRECTORY_CREATE_RESPONSE(msg, "unsafe pathname given",false);
				}
			}
		}
		else {
			return protocol.INVALID_PROTOCOL("bad message");
		}
	}
	
	
}
