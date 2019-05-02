package unimelb.bitbox;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Logger;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class RequestOperator {
	private static Logger log = Logger.getLogger(RequestOperator.class.getName());
	private FileSystemManager fileSystemManager;
	public boolean hasShortcut = false;
	
	
	public RequestOperator(FileSystemManager fileSystemManager) {
		this.fileSystemManager=fileSystemManager;
	}
	
	
	
	
	public Document fileCreateResponse(Document request) {
		System.out.println("RequestOperator " + request.toJson());
		String command = request.getString("command");
		Document descriptor = (Document)request.get("fileDescriptor");
		String pathName = request.getString("pathName");
		System.out.println("~~~~~~" + pathName);
		String md5 = descriptor.getString("md5");
		long fileSize = descriptor.getLong("fileSize");
		long lastModified = descriptor.getLong("lastModified");
		//valid command
		if (command.equals("FILE_CREATE_REQUEST")) {
			//pathname already exists
			System.out.println("~~~~~~" + pathName);
			if (fileSystemManager.fileNameExists(pathName)) {
				//return file created false response
				return Protocol.FILE_CREATE_RESPONSE(request, "pathname already exists", false);					 
			}
			else {
				//safe pathname
				if(fileSystemManager.isSafePathName(pathName)) {
					try {
						//
						if (fileSystemManager.createFileLoader(pathName, md5, fileSize, lastModified)) {
							//file created successfully
							log.info("File Loader Created for " + pathName);
							if (fileSystemManager.checkShortcut(pathName)) {
								log.info("Shortcut is found for " + pathName);
								hasShortcut=true;
							}							
							return Protocol.FILE_CREATE_RESPONSE(request, "file loader ready", true);
						}
						else {
							//have problem creating the file
							return Protocol.FILE_CREATE_RESPONSE(request, "there was a problem creating the file", false);
						}
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return Protocol.FILE_CREATE_RESPONSE(request, "there was a problem creating the file", false);
				}
				else {
					//unsafe pathname
					return Protocol.FILE_CREATE_RESPONSE(request, "unsafe pathname given", false);
				}
			}		 
		}
		else {
			return Protocol.INVALID_PROTOCOL("bad message");
		}
	}
	
	
	//first time request
	public Document firstFileByteRequest(Document request) {
		Document descriptor = (Document) request.get("fileDescriptor");
		long fileSize = descriptor.getLong("fileSize");
		long blockSize = PeerStatistics.blockSize;
		long position = 0;
		if (fileSize <= blockSize) {
			return Protocol.FILE_BYTES_REQUEST(request, position, fileSize);
		}
		else {
			return Protocol.FILE_BYTES_REQUEST(request, position, blockSize);
		}			
	}

	
	//continue request
	public Document fileByteRequest(Document request) {
		String command = request.getString("command");
		String pathName = request.getString("pathName");
		String encodedContent = request.getString("content");
		long position = request.getLong("position");
		long length = request.getLong("length");
		Document descriptor = (Document) request.get("fileDescriptor");
		long fileSize = descriptor.getLong("fileSize");
		long blockSize = PeerStatistics.blockSize;
		if (command.equals("FILE_BYTES_RESPONSE")) {
			try {
				byte[] bytes = Base64.getDecoder().decode(encodedContent);
				ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
				if(fileSystemManager.writeFile(pathName, byteBuffer, position)) {
					// file write success
					log.info("");
				}else {
					// file write fail
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(position + length == fileSize) {
				try {
					if(fileSystemManager.checkWriteComplete(pathName)) {
						//file load complete
						log.info("Check Write Complete for " + pathName);
					}else {
						//file load not complete
						log.info("Check Write inComplete for " + pathName);
					}
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return Protocol.FILE_BYTES_REQUEST(request, fileSize, 0);
			}
			if (position + length + blockSize <= fileSize) {
				return Protocol.FILE_BYTES_REQUEST(request, position+length, blockSize);
			}
			else {
				return Protocol.FILE_BYTES_REQUEST(request, position+length, fileSize-(position+length));
			}

		}
		else {
			return Protocol.INVALID_PROTOCOL("bad message");
		}
	}
	
	
	public Document fileByteResponse(Document request) {
		String command = request.getString("command");
		Document descriptor = (Document) request.get("fileDescriptor");
		String md5 = descriptor.getString("md5");
		long position = request.getLong("position");
		long length = request.getLong("length");
		String encodedContent="";
		if (command.equals("FILE_BYTES_REQUEST")) {
			try {
				ByteBuffer byteBuffer = fileSystemManager.readFile(md5, position, length);
				if(byteBuffer == null) {
					return Protocol.FILE_BYTES_RESPONSE(request, encodedContent, "unsucessfull read", false);
				}
				encodedContent = Base64.getEncoder().encodeToString(byteBuffer.array());
				return Protocol.FILE_BYTES_RESPONSE(request, encodedContent, "successful read", true);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Protocol.FILE_BYTES_RESPONSE(request, encodedContent, "unsucessfull read", false);
		}
		else {
			return Protocol.INVALID_PROTOCOL("bad message");
		}
	}
		

	
	
	public Document fileDeleteResponse(Document request) {
		String command = request.getString("command");
		String pathName = request.getString("pathName");
		Document descriptor = (Document)request.get("fileDescriptor");
		String md5 = descriptor.getString("md5");
		long lastModified = descriptor.getLong("lastModified");
		if (command.equals("FILE_DELETE_REQUEST")) {
			if (fileSystemManager.fileNameExists(pathName)) {
				if (fileSystemManager.isSafePathName(pathName)) {
					if (fileSystemManager.deleteFile(pathName, lastModified, md5)) {
						//file deleted successfully
						return Protocol.FILE_DELETE_RESPONSE(request, "file deleted", true);
					}
					else {
						//having problem deleting the file
						return Protocol.FILE_DELETE_RESPONSE(request, "there was a problem deleting the file", false);
					}
				}
				else {
					//unsafe pathname
					return Protocol.FILE_CREATE_RESPONSE(request, "unsafe pathname given",false);
				}
			}
			else {
				//pathname already exists
				return Protocol.FILE_CREATE_RESPONSE(request, "pathname does not exist",false);				
			}
		}
		else {
			return Protocol.INVALID_PROTOCOL("bad message");
		}
	}
	

	
	public Document fileModifyResponse(Document request) {
		String command = request.getString("command");
		String pathName = request.getString("pathName");
		Document descriptor = (Document)request.get("fileDescriptor");
		String md5 = descriptor.getString("md5");
		long lastModified = descriptor.getLong("lastModified");
		if (command.equals("FILE_MODIFY_REQUEST")) {
			if (fileSystemManager.fileNameExists(pathName, md5)) {
				//pathname already exists
				return Protocol.FILE_MODIFY_RESPONSE(request, "file already exists with matching content", false);
			}
			else {
				if (fileSystemManager.isSafePathName(pathName)) {
					//safe pathName
					if (fileSystemManager.fileNameExists(pathName)) {
						//the modified file exists
						try {
							if(fileSystemManager.modifyFileLoader(pathName, md5, lastModified)) {
							//file modified successfully
								try {
									if (fileSystemManager.checkShortcut(pathName)) {
										hasShortcut=true;
									}
								} catch (NoSuchAlgorithmException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							return Protocol.FILE_MODIFY_RESPONSE(request, "file loader ready", true);
							}
							else {
							//having problem modifying the file
							return Protocol.FILE_MODIFY_RESPONSE(request, "there was a problem modifying the file", false);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return Protocol.FILE_MODIFY_RESPONSE(request, "there was a problem modifying the file", false);
					}
					else {
						//pathname given does not exist
						return Protocol.FILE_MODIFY_RESPONSE(request, "pathname does not exist", false);
					}
				}					
				else {
					//unsafe pathname
					return Protocol.FILE_MODIFY_RESPONSE(request, "unsafe pathname given", false);
				}
			}
		}
		else {
			return Protocol.INVALID_PROTOCOL("bad message");
		}
	}
	
	

	
	
	public Document directoryCreateResponse(Document request) {
		String command = request.getString("command");
		String pathName = request.getString("pathName");
		if (command.equals("DIRECTORY_CREATE_REQUEST")) {
			if (fileSystemManager.dirNameExists(pathName)) {
				//path already exists
				return Protocol.DIRECTORY_CREATE_RESPONSE(request, "pathname already exists", false);				
			}
			else {
				if(fileSystemManager.isSafePathName(pathName)) {
					if (fileSystemManager.makeDirectory(pathName)) {
						//directory created successfully
						return Protocol.DIRECTORY_CREATE_RESPONSE(request, "directory created", true);
					}
					else {
						//have problem creating the directory
						return Protocol.DIRECTORY_CREATE_RESPONSE(request, "there was a problem creating the directory", false);
					}
				}
				else {
					//unsafe pathname
					return Protocol.DIRECTORY_CREATE_RESPONSE(request, "unsafe pathname given", false);
				}
			}		 
		}
		else {
			return Protocol.INVALID_PROTOCOL("message");
		}
	}
	
	

	
	
	public Document directoryDeleteResponse(Document request) {
		String command = request.getString("command");
		String pathName = request.getString("pathName");
		if (command.equals("DIRECTORY_DELETE_REQUEST")) {
			if (fileSystemManager.dirNameExists(pathName)) {
				if (fileSystemManager.isSafePathName(pathName)) {
					if (fileSystemManager.deleteDirectory(pathName)) {
						//directory deleted successfully
						return Protocol.DIRECTORY_DELETE_RESPONSE(request, "directory deleted", true);
					}
					else {
						//having problem deleting the directory
						return Protocol.DIRECTORY_DELETE_RESPONSE(request, "there was a problem deleting the directory", false);
					}
				}
				else {
					//unsafe pathname
					return Protocol.DIRECTORY_CREATE_RESPONSE(request, "unsafe pathname given",false);
				}				
			}
			else {
				//pathname already exists
				return Protocol.DIRECTORY_CREATE_RESPONSE(request, "pathname does not exist",false);
			}
		}
		else {
			return Protocol.INVALID_PROTOCOL("bad message");
		}
	}
	
	
	
}
