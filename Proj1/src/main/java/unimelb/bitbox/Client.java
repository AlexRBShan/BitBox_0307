package unimelb.bitbox;

import unimelb.bitbox.util.*;
import java.io.*;
import java.net.*;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Read configurations
		Configuration.getConfiguration();
		int portnum = Integer.parseInt(Configuration.getConfigurationValue("port"));
		String[] peers = Configuration.getConfigurationValue("peers").split(",");
		String ip = "localhost";
		
		try(Socket client = new Socket(ip,portnum)){
			// create input and output streams for 
			DataInputStream input = new DataInputStream(client.getInputStream());
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
			Document doc1 = new Document();
			doc1.append("command", "REQUEST_HANDSHAKE");
			System.out.println("Command: " + doc1.toJson());
			output.writeUTF(doc1.toJson());
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
