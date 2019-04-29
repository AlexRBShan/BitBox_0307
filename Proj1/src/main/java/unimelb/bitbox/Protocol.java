package unimelb.bitbox;

import java.util.ArrayList;
import java.util.Base64;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;
import unimelb.bitbox.util.*;

public class Protocol {
	//public static void main (String args[]) {

	public Document INVALID_PROTOCOL() {
		Document doc = new Document();
		doc.append("command","INVALID_PROTOCOL");
		doc.append("message", "message must contain a command field as string");		
		return doc;
	}
	
	/*
	public void CONNECTION_REFUSED(String[] peers, String message) {	
		Document doc = new Document();
		doc.append("command", "CONNECTION_REFUSED");
		doc.append("message", message);
		doc.append("peers", );
		for (int i=0;i<peers.length;i++) {
			Document doc1 = new Document();
			//doc1.append("host", val);
			//doc1.append("port", val);
		}
	}
	*/

	public Document HANDSHAKE_REQUEST(String host, int port) {
		Document doc1 = new Document();
		doc1.append("host", "host");
		doc1.append("port", port);
		Document doc2 = new Document();
		doc2.append("command", "HANDSHAKE_REQUEST");
		doc2.append("hostport", doc1);
		return doc2;
	}
	public Document HANDSHAKE_RESPONSE(String host, int port) {
		Document doc1 = new Document();
		doc1.append("host", "host");
		doc1.append("port", 123);
		Document doc2 = new Document();
		doc2.append("command", "HANDSHAKE_REQUEST");
		doc2.append("hostport", doc1);
		System.out.println(doc2.toJson());
		return doc2;
	}
	public Document FILE_CREATE_REQUEST(FileSystemEvent FILE_CREATE) {
		Document doc1 = new Document();
		doc1.append("md5",FILE_CREATE.fileDescriptor.md5);
		doc1.append("lastModified", FILE_CREATE.fileDescriptor.lastModified);
		doc1.append("fileSize", FILE_CREATE.fileDescriptor.fileSize);
		Document doc2 = new Document();
		doc2.append("command", "FILE_CREATE_REQUEST");
		doc2.append("fileDescriptor", doc1);
		doc2.append("pathName", FILE_CREATE.pathName);
		return doc2;
		
	}
	public Document FILE_CREATE_RESPONSE(FileSystemEvent FILE_CREATE, String msg, boolean status) {
		Document doc1 = new Document();
		doc1.append("md5",FILE_CREATE.fileDescriptor.md5);
		doc1.append("lastModified", FILE_CREATE.fileDescriptor.lastModified);
		doc1.append("fileSize", FILE_CREATE.fileDescriptor.fileSize);
		Document doc2 = new Document();
		doc2.append("command", "FILE_CREATE_REQUEST");
		doc2.append("fileDescriptor", doc1);
		doc2.append("pathName", FILE_CREATE.pathName);
		doc2.append("message",msg);
		doc2.append("status", status);
		return doc2;
	}
	public Document FILE_BYTES_REQUEST(FileSystemEvent FILE, int blockSize) {
		Document doc1 = new Document();
		doc1.append("md5",FILE.fileDescriptor.md5);
		doc1.append("lastModified", FILE.fileDescriptor.lastModified);
		doc1.append("fileSize", FILE.fileDescriptor.fileSize);
		Document doc2 = new Document();
		doc2.append("command", "FILE_BYTES_REQUEST");
		doc2.append("fileDescriptor", doc1);
		doc2.append("pathName", FILE.pathName);
		doc2.append("position", 0);
		doc2.append("length", blockSize);
		return doc2;
		
	}
	public Document FILE_BYTES_RESPONSE(FileSystemEvent FILE, int blockSize, byte[] Byte, String message, Boolean status) {
		Document doc1 = new Document();
		doc1.append("md5",FILE.fileDescriptor.md5);
		doc1.append("lastModified", FILE.fileDescriptor.lastModified);
		doc1.append("fileSize", FILE.fileDescriptor.fileSize);
		Document doc2 = new Document();
		doc2.append("command", "FILE_BYTES_RESPONSE");
		doc2.append("fileDescriptor", doc1);
		doc2.append("pathName", FILE.pathName);
		doc2.append("length",blockSize);
		Base64.Encoder encoder = Base64.getEncoder();
		String encodedByte = encoder.encodeToString(Byte);
		doc2.append("content", encodedByte);
		doc2.append("message", message);
		doc2.append("status", status);
		return doc2;
	}
	public Document FILE_DELETE_REQUEST(FileSystemEvent FILE_DELETE) {
		Document doc1 = new Document();
		doc1.append("md5",FILE_DELETE.fileDescriptor.md5);
		doc1.append("lastModified", FILE_DELETE.fileDescriptor.lastModified);
		doc1.append("fileSize", FILE_DELETE.fileDescriptor.fileSize);
		Document doc2 = new Document();
		doc2.append("command", "FILE_DELETE_REQUEST");
		doc2.append("fileDescriptor", doc1);
		doc2.append("pathName", FILE_DELETE.pathName);
		return doc2;
	}
	public Document FILE_DELETE_RESPONSE(FileSystemEvent FILE_DELETE, String message, boolean status) {
		Document doc1 = new Document();
		doc1.append("md5",FILE_DELETE.fileDescriptor.md5);
		doc1.append("lastModified", FILE_DELETE.fileDescriptor.lastModified);
		doc1.append("fileSize", FILE_DELETE.fileDescriptor.fileSize);
		Document doc2 = new Document();
		doc2.append("command", "FILE_DELETE_RESPONSE");
		doc2.append("message", message);
		doc2.append("status", status);
		return doc2;
	}
	public Document FILE_MODIFY_REQUEST(FileSystemEvent FILE_MODIFY) {
		Document doc1 = new Document();
		doc1.append("md5",FILE_MODIFY.fileDescriptor.md5);
		doc1.append("lastModified", FILE_MODIFY.fileDescriptor.lastModified);
		doc1.append("fileSize", FILE_MODIFY.fileDescriptor.fileSize);
		Document doc2 = new Document();
		doc2.append("command", "FILE_MODIFY_REQUEST");
		doc2.append("fileDescriptor", doc1);
		doc2.append("pathName", FILE_MODIFY.pathName);
		return doc2;
	}
	public Document FILE_MODIFY_RESPONSE(FileSystemEvent FILE_MODIFY, String message, boolean status) {
		Document doc1 = new Document();
		doc1.append("md5",FILE_MODIFY.fileDescriptor.md5);
		doc1.append("lastModified", FILE_MODIFY.fileDescriptor.lastModified);
		doc1.append("fileSize", FILE_MODIFY.fileDescriptor.fileSize);
		Document doc2 = new Document();
		doc2.append("command", "FILE_MODIFY_RESPONSE");
		doc2.append("message", message);
		doc2.append("status", status);
		return doc2;
	}
	public Document DIRECTORY_CREATE_REQUEST(FileSystemEvent FILE) {
		Document doc = new Document();
		doc.append("command", "DIRECTORY_CREATE_REQUEST");
		doc.append("pathName", FILE.path);
		return doc;
	}
	public Document DIRECTORY_CREATE_RESPONSE(FileSystemEvent FILE, String message, Boolean status) {
		Document doc = new Document();
		doc.append("command", "DIRECTORY_CREATE_RESPONSE");
		doc.append("pathName", FILE.path);
		doc.append("message", message);
		doc.append("status", status);
		return doc;
	}
	public Document DIRECTORY_DELETE_REQUEST(FileSystemEvent FILE) {
		Document doc = new Document();
		doc.append("command", "DIRECTORY_DELETE_REQUEST");
		doc.append("pathName", FILE.path);
		return doc;
	}
	public Document DIRECTORY_DELETE_REPONSE(FileSystemEvent FILE, String message, Boolean status) {
		Document doc = new Document();
		doc.append("command", "DIRECTORY_DELETE_RESPONSE");
		doc.append("pathName", FILE.path);
		doc.append("message", message);
		doc.append("status", status);
		return doc;
	}
	
	
	
	
}
