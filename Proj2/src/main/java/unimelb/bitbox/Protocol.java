package unimelb.bitbox;

import java.util.ArrayList;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;
import unimelb.bitbox.util.FileSystemManager.FileDescriptor;

public class Protocol {


	// Invalid Protocol
	static public Document INVALID_PROTOCOL(String msg) {
		Document doc = new Document();
		doc.append("command","INVALID_PROTOCOL");
		doc.append("message", msg);		
		return doc;
	}

	// Handshake related protocols
	static public Document HANDSHAKE_REQUEST(HostPort peer) {
		Document doc = new Document();
		doc.append("command", "HANDSHAKE_REQUEST");
		doc.append("hostPort", peer.toDoc());
		return doc;
	}
	static public Document HANDSHAKE_RESPONSE(HostPort peer) {
		Document doc = new Document();
		doc.append("command", "HANDSHAKE_RESPONSE");
		doc.append("hostPort", peer.toDoc());
		return doc;
	}
	static public Document CONNECTION_REFUSED(ArrayList<Document> peerList) {	
		Document doc = new Document();
		doc.append("command", "CONNECTION_REFUSED");
		doc.append("message", "connection limit reached");
		doc.append("peers", peerList);
		return doc;
	}
	
	// File Related Protocols
	
	
	
	static public Document FILE_CREATE_REQUEST(FileDescriptor fileDescriptor, String pathName) {
		Document doc = new Document();
		doc.append("command", "FILE_CREATE_REQUEST");
		doc.append("fileDescriptor", fileDescriptor.toDoc());
		doc.append("pathName", pathName);
		return doc;
	}
	
	
	
	static public Document FILE_CREATE_RESPONSE(Document request, String msg, boolean status) {
		Document fileDescriptor = (Document) request.get("fileDescriptor");
		String pathName = request.getString("pathName");
		Document doc = new Document();
		doc.append("command", "FILE_CREATE_RESPONSE");
		doc.append("fileDescriptor", fileDescriptor);
		doc.append("pathName", pathName);
		doc.append("message",msg);
		doc.append("status", status);
		return doc;
	}
	
	static public Document FILE_BYTES_REQUEST(Document request, long position, long length) {
		Document fileDescriptor = (Document) request.get("fileDescriptor");
		String pathName = request.getString("pathName");
		Document doc = new Document();
		doc.append("command", "FILE_BYTES_REQUEST");
		doc.append("fileDescriptor", fileDescriptor);
		doc.append("pathName", pathName);
		doc.append("position", position);
		doc.append("length", length);
		return doc;
		
	}
	static public Document FILE_BYTES_RESPONSE(Document request,String content, String message, Boolean status) {
		Document fileDescriptor = (Document)request.get("fileDescriptor");
		String pathName = request.getString("pathName");
		long position = request.getLong("position");
		long length = request.getLong("length");
		Document doc = new Document();
		doc.append("command", "FILE_BYTES_RESPONSE");
		doc.append("fileDescriptor", fileDescriptor);
		doc.append("pathName", pathName);
		doc.append("position", position);
		doc.append("length",length);
		doc.append("content", content);
		doc.append("message", message);
		doc.append("status", status);
		return doc;
	}
	static public Document FILE_DELETE_REQUEST(FileDescriptor fileDescriptor, String pathName) {
		Document doc2 = new Document();
		doc2.append("command", "FILE_DELETE_REQUEST");
		doc2.append("fileDescriptor", fileDescriptor.toDoc());
		doc2.append("pathName", pathName);
		return doc2;
	}
	static public Document FILE_DELETE_RESPONSE(Document request, String message, boolean status) {
		Document fileDescriptor = (Document)request.get("fileDescriptor");
		String pathName = request.getString("pathName");
		Document doc = new Document();
		doc.append("command", "FILE_DELETE_RESPONSE");
		doc.append("fileDescriptor", fileDescriptor);
		doc.append("pathName", pathName);
		doc.append("message", message);
		doc.append("status", status);
		return doc;
	}
	static public Document FILE_MODIFY_REQUEST(FileDescriptor fileDescriptor, String pathName) {
		Document doc = new Document();
		doc.append("command", "FILE_MODIFY_REQUEST");
		doc.append("fileDescriptor", fileDescriptor.toDoc());
		doc.append("pathName", pathName);
		return doc;
	}
	static public Document FILE_MODIFY_RESPONSE(Document request, String message, boolean status) {
		Document fileDescriptor = (Document)request.get("fileDescriptor");
		String pathName = request.getString("pathName");
		Document doc = new Document();
		doc.append("command", "FILE_MODIFY_RESPONSE");
		doc.append("fileDescriptor", fileDescriptor);
		doc.append("pathName", pathName);
		doc.append("message", message);
		doc.append("status", status);
		return doc;
	}
	static public Document DIRECTORY_CREATE_REQUEST(String pathName) {
		Document doc = new Document();
		doc.append("command", "DIRECTORY_CREATE_REQUEST");
		doc.append("pathName", pathName);
		return doc;
	}
	static public Document DIRECTORY_CREATE_RESPONSE(Document request, String message, Boolean status) {
		String pathName = request.getString("pathName");
		Document doc = new Document();
		doc.append("command", "DIRECTORY_CREATE_RESPONSE");
		doc.append("pathName", pathName);
		doc.append("message", message);
		doc.append("status", status);
		return doc;
	}
	static public Document DIRECTORY_DELETE_REQUEST(String pathName) {
		Document doc = new Document();
		doc.append("command", "DIRECTORY_DELETE_REQUEST");
		doc.append("pathName", pathName);
		return doc;
	}
	static public Document DIRECTORY_DELETE_RESPONSE(Document request, String message, Boolean status) {
		String pathName = request.getString("pathName");
		Document doc = new Document();
		doc.append("command", "DIRECTORY_DELETE_RESPONSE");
		doc.append("pathName", pathName);
		doc.append("message", message);
		doc.append("status", status);
		return doc;
	}
	
	
	
	
}
